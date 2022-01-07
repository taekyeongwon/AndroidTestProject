package com.tkw.kr.myapplication.core.network.base

import io.reactivex.Single
import retrofit2.Call

abstract class BaseNetwork <T>: BusinessInterface {
    protected var network: T

    init {
        network = createNetwork()
    }

    abstract fun createNetwork(): T
}

interface BusinessInterface {
    val API: BaseApiProtocol    //api 호출용 변수
    fun <V : ServerResult> parsingResponse(call: Call<V>, callback: NetResultCallback<V>)       //콜백으로 응답 처리하는 경우 호출
    suspend fun <V : ServerResult> parsingResponseSuspend(call: suspend () -> Any): NetResult<V> //코루틴으로 응답 처리하는 경우 호출
    fun <V : ServerResult> Call<V>.toSingle(): Single<V>
}
