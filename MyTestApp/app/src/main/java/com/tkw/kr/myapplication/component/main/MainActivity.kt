package com.tkw.kr.myapplication.component.main

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import com.tkw.kr.myapplication.R
import com.tkw.kr.myapplication.base.BaseView
import com.tkw.kr.myapplication.component.github.GithubActivity
import com.tkw.kr.myapplication.component.map.GoogleMapActivity
import com.tkw.kr.myapplication.core.factory.MyProviderFactory
import com.tkw.kr.myapplication.util.setOnSingleClickListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseView<MainViewModel>() {
    override val layoutResourceId: Int
        get() = R.layout.activity_main
    override lateinit var viewModel: MainViewModel

    override fun initView() {
        viewModel = ViewModelProvider(this, MyProviderFactory()).get(MainViewModel::class.java)
    }

    override fun initObserver() {

    }

    override fun initListener() {
        btn_github.setOnSingleClickListener {
            val githubIntent = Intent(this@MainActivity, GithubActivity::class.java)
            startActivity(githubIntent)
        }

        btn_googlemap.setOnSingleClickListener {
            val googlemapIntent = Intent(this@MainActivity, GoogleMapActivity::class.java)
            startActivity(googlemapIntent)
        }
    }
}