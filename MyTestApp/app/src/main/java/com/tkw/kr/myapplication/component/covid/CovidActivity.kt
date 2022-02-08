package com.tkw.kr.myapplication.component.covid

import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.tkw.kr.myapplication.R
import com.tkw.kr.myapplication.base.BaseView
import com.tkw.kr.myapplication.core.factory.MyProviderFactory
import com.tkw.kr.myapplication.util.setOnSingleClickListener
import kotlinx.android.synthetic.main.activity_covid.*

class CovidActivity: BaseView<CovidViewModel>() {
    override val layoutResourceId: Int
        get() = R.layout.activity_covid
    override lateinit var viewModel: CovidViewModel

    override fun initView() {
        viewModel = ViewModelProvider(this, MyProviderFactory()).get(CovidViewModel::class.java)

        viewModel.getTotalCount()
    }

    override fun initObserver() {
        viewModel.totalCount.observe(this, androidx.lifecycle.Observer {
            Log.d("disaster", "totalCount : " + it)
            if(it == 0) {
                return@Observer
            }
            if(viewModel.getSavedList() != null && viewModel.getSavedList()?.disasterSmsList?.size != 0) {
                //저장된 데이터가 있으면 이전 토탈 개수와 비교하여 차이만큼 조회해서 신규 데이터만 추가
                Log.d("totalCount", "getRecentData(${it - viewModel.getLastTotalCount()})")
                viewModel.getRecentData(it - viewModel.getLastTotalCount())
            } else {
                //저장된 데이터가 없으면(최초, 데이터 삭제 시) 전체 조회
                Log.d("totalCount", "getTotalData(${it})")
                viewModel.getTotalData(it)
            }
        })

        viewModel.accCount.observe(this, Observer {
            Log.d("disaster", "accCount : " + it)
            tv_acc_count.text = it.toString()
        })
    }

    override fun initListener() {
        btn_refresh.setOnSingleClickListener {
            viewModel.getTotalCount()
        }
    }
}