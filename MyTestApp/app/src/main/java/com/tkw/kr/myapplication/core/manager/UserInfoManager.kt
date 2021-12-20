package com.tkw.kr.myapplication.core.manager

import com.tkw.kr.myapplication.MainApplication
import com.tkw.kr.myapplication.core.helper.LocaleHelper

object UserInfoManager {
    val languageCode: String
    get() = LocaleHelper.getSelectedLanguage(MainApplication.application.applicationContext, "")
}