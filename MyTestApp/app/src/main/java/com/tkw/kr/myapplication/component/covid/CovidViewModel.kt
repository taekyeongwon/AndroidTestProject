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

    fun getTotalCount() {   //totCnt값 가져오기 위해 1개만 요청
        model.getTotalCount(object: DisasterPostCallback {
            override fun onCallback(covidDTO: CovidDTO?) {
                _totalCount.postValue(covidDTO?.rtnResult?.totCnt ?: 0)
            }
        })
    }

    fun getRecentData(lastTotalCount: Int) {   //이전 토탈 카운트와 차이만큼 조회
        if(lastTotalCount == 0) {   //이전에 저장된 데이터로만 누적확진자 계산
            _accCount.postValue(model.calculateAccumulateCount())
            return
        } else {    //신규 데이터 개수 만큼 조회 한 뒤 누적확진자 계산
            model.getRecentData(lastTotalCount, object: DisasterPostCallback {
                override fun onCallback(covidDTO: CovidDTO?) {
                    _accCount.postValue(model.calculateAccumulateCount())
                }
            })
        }
    }

    fun getTotalData(totalCount: Int) { //전체 데이터 조회
        model.getTotalData(totalCount, object : DisasterPostCallback {
            override fun onCallback(covidDTO: CovidDTO?) {
                _accCount.postValue(model.calculateAccumulateCount())
            }
        })
    }

    fun getSavedList(): CovidDTO? = model.getSavedList()
    fun getLastTotalCount(): Int = model.getLastTotalCount()
}