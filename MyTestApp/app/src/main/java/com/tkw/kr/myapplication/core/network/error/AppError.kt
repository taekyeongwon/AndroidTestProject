package com.tkw.kr.myapplication.core.network.error

import android.content.Context
import com.tkw.kr.myapplication.MainApplication
import com.tkw.kr.myapplication.R
import com.tkw.kr.myapplication.core.network.base.ServerResult
import java.lang.Exception

object AppError {
    fun getMessage(context: Context, error: Exception): String {
        if(error is Base) {
            return error.getMessage(context)
        }

        return error.localizedMessage
    }

    fun showAlert(context: Context, error: Exception) {
        if(error is Base) {
//            TMAlertDialog.showAlert(context, error.getMessage(context))
        }
    }

    open class Base(message: String? = "", cause: Throwable? = null): Exception(message, cause) {
        open fun getMessage(context: Context): String {
            return "에러 발생"
        }

        open fun getResultCode(): String {
            return "-1"
        }

        open fun getTokenErrorCode(): Int {
            return -1
        }

        open fun getStackTraceString(): String {
            return "서버 통신 실패"
        }
    }

    /**
     * status code 200 외의 값인 경우 처리(300 ~ 500) 메세지 정의 필요.
     */
    class Network(val httpCode: Int, cause: Throwable? = null) : Base("", cause) {
        private val prefix = "net_network_"

        override fun getMessage(context: Context): String {
            if(httpCode == -1) {
                return cause?.message ?: "서버 통신 실패"
            }
            try {
                val ref = R.string::class.java
                val field = ref.getField(prefix + httpCode)
                val resId = field.getInt(null)
                return context.getString(resId)
            } catch (e: Exception) {
                return "서버 통신 실패"
            }
        }

        override fun getTokenErrorCode(): Int {
            return httpCode
        }

        override fun getStackTraceString(): String {
            return MainApplication.application?.getStackTrace(cause) ?: "서버 통신 실패"
        }
    }

    class Server(val response: ServerResult, cause: Throwable? = null): Base("", cause) {
        private val prefix = "net_server_"

        override fun getMessage(context: Context): String {
            try {
                return response.errorMessage() ?: "서버 통신 실패"
            } catch (e: Exception) {
                val msg = response.errorMessage()
                return msg ?: "서버 통신 실패"
            }
        }

        override fun getResultCode(): String {
            return response.resultCode()
        }

        override fun getStackTraceString(): String {
            return MainApplication.application?.getStackTrace(cause) ?: "서버 통신 실패"
        }
    }
}