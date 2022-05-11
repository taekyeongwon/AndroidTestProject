package com.tkw.kr.myapplication.core.network.base

import android.util.Log

open class BaseResponse(
    var resultCode: Int = 0,
    var desc: String? = null
): ServerResult {

    init {
        Log.d("BaseResponse", "resultCode = "+resultCode)
    }

    override fun isSuccess(): Boolean {
        return resultCode == 0
    }

    override fun resultCode(): String {
        return resultCode.toString()
    }

    override fun errorMessage(): String? {
        return desc
    }
}