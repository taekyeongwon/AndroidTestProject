package com.tkw.kr.myapplication.component.covid

import android.os.HandlerThread
import android.util.Log
import com.google.gson.Gson
import org.json.JSONObject
import org.jsoup.Jsoup
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

interface CovidModel {
    fun getDisasterData(count: Int, callback: DisasterPostCallback)
}

interface DisasterPostCallback {
    fun onCallback(covidDTO: CovidDTO)
}

class CovidModelImpl: CovidModel {
    private var disasterSmsUrl: String = "https://www.safekorea.go.kr/idsiSFK/sfk/cs/sua/web/DisasterSmsList.do"

    override fun getDisasterData(count: Int, callback: DisasterPostCallback) {
        val thread = Thread(Runnable {  //todo rx single로 변경하기
            try {
                val disasterDocument = Jsoup.connect(disasterSmsUrl)
                    .header("Content-Type", "application/json")
                    .requestBody(getData(count).toString())
                    .ignoreContentType(true)
                    .post()

                val covidData = Gson().fromJson(disasterDocument.body().text(), CovidDTO::class.java)
                Log.d("disaster", Gson().toJson(covidData))

                callback.onCallback(covidData)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        })
        thread.start()
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
}