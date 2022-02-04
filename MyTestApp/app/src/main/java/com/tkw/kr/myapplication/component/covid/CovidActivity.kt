package com.tkw.kr.myapplication.component.covid

import android.os.Handler
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.tkw.kr.myapplication.R
import com.tkw.kr.myapplication.base.BaseView
import com.tkw.kr.myapplication.base.BaseViewModel
import com.tkw.kr.myapplication.core.factory.MyProviderFactory
import org.json.JSONObject
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class CovidActivity: BaseView<CovidViewModel>() {
    override val layoutResourceId: Int
        get() = R.layout.activity_covid
    override lateinit var viewModel: CovidViewModel

    private var disasterSmsUrl: String = "https://www.safekorea.go.kr/idsiSFK/sfk/cs/sua/web/DisasterSmsList.do"
    private lateinit var disasterDocument: Document

    override fun initView() {
        viewModel = ViewModelProvider(this, MyProviderFactory()).get(CovidViewModel::class.java)
        getData()

    }

    override fun initObserver() {

    }

    override fun initListener() {

    }

    private fun getData() {
        val thread = Thread(Runnable {
            try {
                disasterDocument = Jsoup.connect(disasterSmsUrl)
                    .header("Content-Type", "application/json")
                    .requestBody(firstGetData().toString())
                    .ignoreContentType(true)
                    .post()

                val covidData = Gson().fromJson(disasterDocument.body().text(), CovidModel::class.java)
                Log.d("disaster", Gson().toJson(covidData))
            } catch (e: IOException) {
                e.printStackTrace()
            }
        })
        thread.start()
    }

    private fun firstGetData(): JSONObject {
        val jsonObject = JSONObject()
        val searchInfo = JSONObject()
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val today = Calendar.getInstance()
        val todayStr = simpleDateFormat.format(today.time)

        searchInfo.put("pageIndex", "1")
        searchInfo.put("pageUnit", "1")
        searchInfo.put("searchBgnDe", todayStr)
        searchInfo.put("searchEndDe", todayStr)
        searchInfo.put("dstr_se_Id", "27")
        searchInfo.put("c_ocrc_type", "DST200")
        jsonObject.put("searchInfo", searchInfo)

        return jsonObject
    }
}