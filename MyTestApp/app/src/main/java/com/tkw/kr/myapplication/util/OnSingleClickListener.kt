package com.tkw.kr.myapplication.util

import android.view.View
import com.tkw.kr.myapplication.base.BaseView

class OnSingleClickListener(private val clickListener: (View) -> Unit) : View.OnClickListener {
    companion object {
        var INTERVAL = 500L
    }

    override fun onClick(v: View?) {
        if(BaseView.clickable) {
            BaseView.clickable = false
            v?.run{
                postDelayed({
                    BaseView.clickable = true
                }, INTERVAL)
                clickListener(v)
            }
        }
    }
}

fun View.setOnSingleClickListener(clickListener: (View) -> Unit) {
    OnSingleClickListener.INTERVAL = 500L
    val singleClickListener = OnSingleClickListener {
        clickListener(it)
    }
    setOnClickListener(singleClickListener)
}

fun View.setOnSingleClickListener2(clickListener: (View) -> Unit) {
    OnSingleClickListener.INTERVAL = 1000L
    val singleClickListener = OnSingleClickListener {
        clickListener(it)
    }
    setOnClickListener(singleClickListener)
}