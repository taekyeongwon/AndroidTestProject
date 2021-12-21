package com.tkw.kr.myapplication.core.network.base

import com.tkw.kr.myapplication.core.network.error.AppError

enum class Status {
    SUCCESS, FAIL//, LOADING
}
data class NetResult<T: ServerResult>(val status: Status, val data: T?, val throwable: AppError.Base?) {
    companion object {
        fun <T: ServerResult> success(data: T?): NetResult<T> = NetResult(status = Status.SUCCESS, data = data, throwable = null)
//        fun <T: BaseResponse> loading(): NetResult<T> = NetResult(status = Status.LOADING, data = null, throwable = null)
        fun <T: ServerResult> error(error: AppError.Base): NetResult<T> = NetResult(status = Status.FAIL, data = null, throwable = error)
    }
}

interface NetResultCallback <T: ServerResult> { //콜백으로 Result 데이터클래스 받기 위한 인터페이스
    fun onResponse(response: NetResult<T>)
}
