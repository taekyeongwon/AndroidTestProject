package com.tkw.kr.myapplication.base

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.tkw.kr.myapplication.core.helper.LocaleHelper
import com.tkw.kr.myapplication.core.manager.UserInfoManager

abstract class BaseView<T: BaseViewModel>: AppCompatActivity() {
    companion object {
        var clickable = true
    }

    abstract val layoutResourceId: Int
    abstract var viewModel: T

    abstract fun initView()
    abstract fun initObserver()
    abstract fun initListener()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(layoutResourceId != -1) setContentView(layoutResourceId)

        initView()
        initObserver()
        initListener()

    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.getUpdateContext(newBase, UserInfoManager.languageCode))
    }

    override fun applyOverrideConfiguration(overrideConfiguration: Configuration?) {
        super.applyOverrideConfiguration(LocaleHelper.applyConfigNightMode(baseContext, overrideConfiguration))
    }

}