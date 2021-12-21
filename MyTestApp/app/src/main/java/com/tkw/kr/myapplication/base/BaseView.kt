package com.tkw.kr.myapplication.base

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.tkw.kr.myapplication.core.alert.MyAlertDialog
import com.tkw.kr.myapplication.core.alert.MyProgressDialog
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

    private val PERMS_REQUEST_CODE = 0x00

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(layoutResourceId != -1) setContentView(layoutResourceId)

        initView()
        initObserver()
        initListener()

        observeAlert()
        observeProgress()
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.getUpdateContext(newBase, UserInfoManager.languageCode))
    }

    override fun applyOverrideConfiguration(overrideConfiguration: Configuration?) {
        super.applyOverrideConfiguration(LocaleHelper.applyConfigNightMode(baseContext, overrideConfiguration))
    }

    private fun observeAlert() {
        viewModel.alertFlag.observe(this, Observer {
            MyAlertDialog.showAlert(this, it.getMessage(this))
            Log.d("Alert", it.getMessage(this))
        })
    }

    private fun observeProgress() {
        viewModel.progressFlag.observe(this, Observer {
            if(it) {
                MyProgressDialog.showAlert(this)
            } else {
                MyProgressDialog.hide()
            }
        })
    }
}