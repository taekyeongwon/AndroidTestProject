package com.tkw.kr.myapplication.core.network.base

import android.util.Log

open class BaseResponse(
    var resultCode: Int = -1,   //-1이여도 resultCode를 응답으로 받지 않으면 0으로 초기화됨.(ex: tmap api..)
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