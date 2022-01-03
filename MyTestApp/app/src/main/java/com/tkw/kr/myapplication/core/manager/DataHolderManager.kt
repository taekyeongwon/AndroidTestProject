package com.tkw.kr.myapplication.core.manager

import java.util.*
import java.util.concurrent.ConcurrentHashMap

object DataHolderManager {
    private var mDataHolder = ConcurrentHashMap<String, String>()   //동시성 보장, 읽기 작업에는 여러 쓰레드 동시에 읽기 가능, 쓰기 작업에는 lock 사용
    fun putData(data: String): String {
        val id = UUID.randomUUID().toString()
        mDataHolder.put(id, data)
        return id
    }

    fun getData(key: String?): String? {
        if(key.isNullOrEmpty()) {
            return null
        }
        val data = mDataHolder.get(key)
        mDataHolder.remove(key)
        return data
    }

    fun peekData(key: String?): String? {
        if(key.isNullOrEmpty()) {
            return null
        }

        return mDataHolder.get(key)
    }
}