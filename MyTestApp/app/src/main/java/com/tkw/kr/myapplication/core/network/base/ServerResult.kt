package com.tkw.kr.myapplication.core.network.base

interface ServerResult {
    fun isSuccess(): Boolean
    fun resultCode(): String
    fun errorMessage(): String?
}