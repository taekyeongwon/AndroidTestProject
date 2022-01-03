package com.tkw.kr.myapplication.component.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.tkw.kr.myapplication.R
import com.tkw.kr.myapplication.base.BaseView
import com.tkw.kr.myapplication.core.factory.MyProviderFactory

class MainActivity : BaseView<MainViewModel>() {
    override val layoutResourceId: Int
        get() = R.layout.activity_main
    override lateinit var viewModel: MainViewModel

    override fun initView() {
        viewModel = ViewModelProvider(this, MyProviderFactory()).get(MainViewModel::class.java)
    }

    override fun initObserver() {
        viewModel.githubRepoData.observe(this, Observer {
            if(it.items != null) {

            }
        })
    }

    override fun initListener() {
        
    }
}