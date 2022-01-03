package com.tkw.kr.myapplication.core.network

import okhttp3.Interceptor
import okhttp3.Response

class MainApiInterceptor: Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder()
//        MainApplication.application?.let {
//            if(!request.url().host().contains(it.getString(R.string.base_url))) {
//                return chain.proceed(request)
//            } else {
        //////////
//        val token = UserInfoManager.instance.accessToken
//        Log.d("App Token", token ?: "")
//        if (token != null && token.isNotEmpty()) {
//            builder.addHeader("Authorization", token)
//        }
//        builder.addHeader("Accept-Language", if(UserInfoManager.instance.language_code == "zh") "zh-CN" else UserInfoManager.instance.language_code)
        //////////

        //}
//        }

        return chain.proceed(builder.build())
    }
}