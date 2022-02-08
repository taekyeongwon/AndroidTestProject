package com.tkw.kr.myapplication.component.covid

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.preference.PreferenceManager
import com.google.gson.Gson
import com.tkw.kr.myapplication.base.BaseViewModel
import com.tkw.kr.myapplication.core.constant.C
import com.tkw.kr.myapplication.util.PreferenceUtil
import java.lang.NumberFormatException
import java.util.regex.Pattern

class CovidViewModel(private val model: CovidModel): BaseViewModel() {
    private val _totalCount = MutableLiveData<Int>()
    val totalCount: LiveData<Int> get() = _totalCount

    private val _accCount = MutableLiveData<Int>()
    val accCount: LiveData<Int> get() = _accCount

    private var lastIndex = -1

    private var covidDTO: CovidDTO? = null
    get() = if(field == null) getSavedList() else field

    fun getTotalCount() {   //totCnt값 가져오기 위해 1개만 요청
        model.getDisasterData(1, object: DisasterPostCallback {
            override fun onCallback(covidDTO: CovidDTO) {
                _totalCount.postValue(covidDTO.rtnResult.totCnt)
            }
        })
    }

    fun getRecentData(lastTotalCount: Int) {   //이전 토탈 카운트와 차이만큼 조회
        if(lastTotalCount == 0) {
            _accCount.postValue(calculateAccumulateCount())
            return
        }
        model.getDisasterData(lastTotalCount, object : DisasterPostCallback {
            override fun onCallback(covidDTO: CovidDTO) {
                val list = parsingData(covidDTO)
                val savedCovid = getSavedList()
                if(savedCovid != null) {
                    savedCovid.disasterSmsList.addAll(0, list)
                    covidDTO.disasterSmsList = savedCovid.disasterSmsList
                    saveList(covidDTO)
                    this@CovidViewModel.covidDTO = covidDTO
                    _accCount.postValue(calculateAccumulateCount())
                }

            }
        })
    }

    fun getTotalData(totalCount: Int) { //전체 데이터 조회
        model.getDisasterData(totalCount, object : DisasterPostCallback {
            override fun onCallback(covidDTO: CovidDTO) {
                var list = parsingData(covidDTO)
                if(list.size == 0) {
                    val savedCovid = getSavedList()
                    covidDTO.disasterSmsList = savedCovid?.disasterSmsList ?: ArrayList()
                } else {
                    covidDTO.disasterSmsList = list //n명 텍스트가 들어간 데이터만 리스트로 뽑아서 교체
                }
                saveList(covidDTO)
                this@CovidViewModel.covidDTO = covidDTO
                _accCount.postValue(calculateAccumulateCount())
            }
        })
    }

    private fun parsingData(covidDTO: CovidDTO): ArrayList<DisasterSmsList> { //n명 텍스트만 추출
        val smsList = ArrayList<DisasterSmsList>()
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

    private fun calculateAccumulateCount(): Int {   //총 누적자 계산
        var accCount = 0
        val covidData = covidDTO
        if(covidData != null) {
            for(i in 0 until covidData.disasterSmsList.size) {
                accCount += covidData.disasterSmsList[i].confirmed_count
            }
        }

        return accCount
    }

    fun getSavedList(): CovidDTO? {
        Log.d("getSavedList()", PreferenceUtil.getString("disasterList"))

        return try {
            Gson().fromJson(PreferenceUtil.getString("disasterList"), CovidDTO::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun saveList(covidDTO: CovidDTO) {
        //0번째 데이터의 시간이 오전 9시 이전인 경우 까지만 저장, 시간이 넘어가면 clear하고 새로 저장
        //임시로 해당 날짜 00:00 ~ 23:59까지만
        val jsonCovid = Gson().toJson(covidDTO)
        PreferenceUtil.putStringSync("disasterList", jsonCovid)
    }

    fun getLastTotalCount(): Int {
        return PreferenceUtil.getInt("totalCount")
    }

    private fun saveLastTotalCount(totalCount: Int) {
        PreferenceUtil.putInt("totalCount", totalCount)
    }
}