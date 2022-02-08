package com.tkw.kr.myapplication.component.covid

import android.os.HandlerThread
import android.util.Log
import com.google.gson.Gson
import com.tkw.kr.myapplication.util.PreferenceUtil
import org.json.JSONObject
import org.jsoup.Jsoup
import java.io.IOException
import java.lang.NumberFormatException
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

interface CovidModel {
    fun getTotalCount(callback: DisasterPostCallback)
    fun getRecentData(diffCount: Int, callback: DisasterPostCallback)
    fun getTotalData(totCount: Int, callback: DisasterPostCallback)
    fun calculateAccumulateCount(): Int
    fun getSavedList(): CovidDTO?
    fun getLastTotalCount(): Int
//    fun getDisasterData(count: Int, callback: DisasterPostCallback)
}

interface DisasterPostCallback {
    fun onCallback(covidDTO: CovidDTO?)
}

class CovidModelImpl: CovidModel {
    private var disasterSmsUrl: String = "https://www.safekorea.go.kr/idsiSFK/sfk/cs/sua/web/DisasterSmsList.do"
    private var lastIndex = -1

    private var covidDTO: CovidDTO? = null
        get() = if(field == null) getSavedList() else field

    override fun getTotalCount(callback: DisasterPostCallback) {
        Thread {
            callback.onCallback(getDisasterData(1))
        }.start()
    }

    override fun getRecentData(diffCount: Int, callback: DisasterPostCallback) {
        Thread {  //todo rx single로 변경하기
            covidDTO = getDisasterData(diffCount)
            val list = parsingData(covidDTO)
            val savedCovid = getSavedList()
            if(savedCovid != null) {
                savedCovid.disasterSmsList.addAll(0, list)
                covidDTO?.disasterSmsList = savedCovid.disasterSmsList
                saveList(covidDTO)
//                covidDTO = covidData
            }
            callback.onCallback(covidDTO)
        }.start()
    }

    override fun getTotalData(totCount: Int, callback: DisasterPostCallback) {
        Thread {  //todo rx single로 변경하기
            covidDTO = getDisasterData(totCount)
            val list = parsingData(covidDTO)
//            if(list.size == 0) {  //
//                val savedCovid = getSavedList()
//                covidDTO?.disasterSmsList = savedCovid?.disasterSmsList ?: ArrayList()
//            } else {
                covidDTO?.disasterSmsList = list //n명 텍스트가 들어간 데이터만 리스트로 뽑아서 교체, 저장된 데이터가 없을 때 전체 요청하므로 바로 list 넣어도 됨
//            }
            saveList(covidDTO)
//            covidDTO = covidData
            callback.onCallback(covidDTO)
        }.start()
    }

    private fun getDisasterData(count: Int): CovidDTO? {
        var covidData: CovidDTO? = null
        try {
            val disasterDocument = Jsoup.connect(disasterSmsUrl)
                .header("Content-Type", "application/json")
                .requestBody(getData(count).toString())
                .ignoreContentType(true)
                .post()

            covidData = Gson().fromJson(disasterDocument.body().text(), CovidDTO::class.java)
            Log.d("disaster", Gson().toJson(covidData))

//            callback.onCallback(covidData)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return covidData
    }

    private fun getData(count: Int = 1): JSONObject {
        val jsonObject = JSONObject()
        val searchInfo = JSONObject()
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val today = Calendar.getInstance()
        val todayStr = simpleDateFormat.format(today.time)

        searchInfo.put("pageIndex", "1")
        searchInfo.put("pageUnit", count.toString())
        searchInfo.put("searchBgnDe", todayStr)
        searchInfo.put("searchEndDe", todayStr)
        searchInfo.put("dstr_se_Id", "27")
        searchInfo.put("c_ocrc_type", "DST200")
        jsonObject.put("searchInfo", searchInfo)

        return jsonObject
    }

    private fun parsingData(covidDTO: CovidDTO?): ArrayList<DisasterSmsList> { //n명 텍스트만 추출
        val smsList = ArrayList<DisasterSmsList>()
        if(covidDTO == null) {
            return smsList
        }
        saveLastTotalCount(covidDTO.rtnResult.totCnt)
        for (i in 0 until covidDTO.disasterSmsList.size) {
            if (lastIndex == covidDTO.disasterSmsList[i].MD101_SN) { //이전에 조회했던 가장 마지막 인덱스까지만 저장하도록
                break
            }

            if(lastIndex != covidDTO.disasterSmsList[0].MD101_SN) {
                lastIndex = covidDTO.disasterSmsList[0].MD101_SN
            }
            val pattern = Pattern.compile("[0-9]+명")
            val matcher = pattern.matcher(covidDTO.disasterSmsList[i].MSG_CN)
            if (matcher.find()) {
                try {
                    covidDTO.disasterSmsList[i].confirmed_count +=
                        covidDTO.disasterSmsList[i].MSG_CN.substring(matcher.start() until matcher.end() - 1)
                            .toInt()

                    smsList.add(covidDTO.disasterSmsList[i])
                } catch (e: NumberFormatException) {
                    e.printStackTrace()
                }
            }
        }
        return smsList
    }

    override fun calculateAccumulateCount(): Int {   //총 누적자 계산
        var accCount = 0
        val covidData = covidDTO
        if(covidData != null) {
            for(i in 0 until covidData.disasterSmsList.size) {
                accCount += covidData.disasterSmsList[i].confirmed_count
            }
        }

        return accCount
    }

    override fun getSavedList(): CovidDTO? {
        Log.d("getSavedList()", PreferenceUtil.getString("disasterList"))

        return try {
            Gson().fromJson(PreferenceUtil.getString("disasterList"), CovidDTO::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun saveList(covidDTO: CovidDTO?) {
        //0번째 데이터의 시간이 오전 9시 이전인 경우 까지만 저장, 시간이 넘어가면 clear하고 새로 저장
        //임시로 해당 날짜 00:00 ~ 23:59까지만
        val jsonCovid = Gson().toJson(covidDTO)
        PreferenceUtil.putStringSync("disasterList", jsonCovid)
    }

    override fun getLastTotalCount(): Int {
        return PreferenceUtil.getInt("totalCount")
    }

    private fun saveLastTotalCount(totalCount: Int) {
        PreferenceUtil.putInt("totalCount", totalCount)
    }
}