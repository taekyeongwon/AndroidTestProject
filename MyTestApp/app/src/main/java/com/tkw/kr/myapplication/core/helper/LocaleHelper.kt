package com.tkw.kr.myapplication.core.helper

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.util.Log
import androidx.preference.PreferenceManager
import com.tkw.kr.myapplication.core.constant.C
import java.util.*

object LocaleHelper {
    /**
     * application 또는 activity단의 context를 preference로 저장된 언어로 업데이트하여 리턴.
     *
     * super.attachBaseContext(LocaleHelper.getUpdateContext(base, lang)) 형태로 attachBaseContext(base: Context) 메소드 오버라이딩하여 사용한다.
     */
    fun getUpdateContext(context: Context, lang: String?): Context {
        return if(!lang.isNullOrEmpty()) {      //preference에 저장된 언어가 있으면 해당 언어로 context 변경
            updateBaseLanguage(context, lang)
        } else {                            //저장된 언어가 없으면 context가 가지고 있는 device 언어로 context 변경
            updateDeviceLanguage(context)
        }
    }

    private fun updateDeviceLanguage(context: Context): Context {
        val config = context.resources.configuration
        val locale = getLocale(config)
        persist(context, "")
        //앱 실행 후 application context attach 시 저장된 언어가 없을 때 preference에 공백값을 넣어 SplashActivity에서 언어선택 화면으로 넘어가기 위함
        return setLocale(context, locale.language)
    }

    private fun updateDefaultLanguage(context: Context): Context {
        val lang = getPersistedData(
            context,
            Locale.getDefault().language
        )
        Log.d("LocaleHelper", "default lang : "+lang)
        persist(context, lang)
        return setLocale(context, lang)
    }

    private fun updateBaseLanguage(context: Context, lang: String): Context {
        persist(context, lang)
        return setLocale(context, lang)
    }

    /**
     * 2020.10.28 tkw
     * androidx appcompat:1.1.0 이슈
     * Nougat 아래 버전 (테스트 기기 : Galaxy S5, Marshmallow)에서 configuration 변경 시 nightmode 처리 중 로케일 설정이 안되는 현상
     * 아래 메소드 재정의하여 해결.
     *
     * activity에서 applyOverrideConfiguration 메소드 재정의.
     * super.applyOverrideConfiguration(LocaleHelper.applyConfigNightMode(baseContext, overrideConfiguration)) 형태로 사용한다.
     *
     * 출처 :
     * https://stackoverflow.com/questions/55265834/change-locale-not-work-after-migrate-to-androidx/58004553#58004553
     * https://qastack.kr/programming/4985805/set-locale-programmatically
     */
    fun applyConfigNightMode(baseContext: Context, overrideConfiguration: Configuration?): Configuration? {
        overrideConfiguration?.let {
            val uiMode = overrideConfiguration.uiMode
            overrideConfiguration.setTo(baseContext.resources.configuration)
            overrideConfiguration.uiMode = uiMode
        }
        return overrideConfiguration
    }

    fun <T> restartApplication(context: Context, locale: String?, restartActivity: Class<T>) {
        locale?.let {
            persist(context, locale)

            if(context is Activity) {
                context.finishAffinity()        //root 액티비티까지 종료
                val intent = Intent(context, restartActivity)
                context.startActivity(intent)
            }

            System.runFinalization()
            //현재 동작중인 스레드의 종료를 기다림.
            // 언어설정 preference 저장이 apply면 비동기, commit이면 동기인데 apply로 저장하는 경우 저장이 안된 채로 재시작됨.
            // 따라서 commit으로 저장하여 저장안되는 현상 해결

            System.exit(0)
        }
    }

    private fun setLocale(context: Context, language: String): Context {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return updateResources(
                context,
                language
            )
        }

        return updateResourcesLegacy(
            context,
            language
        )
    }

    private fun getPersistedData(context: Context, defaultLanguage: String): String {
        return getSelectedLanguage(context, defaultLanguage)
    }

    private fun persist(context: Context, language: String) {
        putSelectedLanguage(context, language)
    }

    private fun putSelectedLanguage(context: Context, language: String) {
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = pref.edit()
        editor.putString(C.Preference.SELECTED_LANGUAGE, language)
        editor.commit() //언어 재설정(SettingActivity) 시 apply() (비동기)로 호출하는 경우 preference에 저장이 안되어 commit()으로 호출
    }

    fun getSelectedLanguage(context: Context?, defaultLanguage: String): String {
        return if(context != null) {
            val pref = PreferenceManager.getDefaultSharedPreferences(context)
            pref.getString(C.Preference.SELECTED_LANGUAGE, defaultLanguage) ?: ""
        } else {
            ""
        }
    }

    private fun getLocale(config: Configuration): Locale {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return config.locales[0]
        } else {
            return config.locale
        }
    }

    @TargetApi(Build.VERSION_CODES.N)
    private fun updateResources(context: Context, language: String): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)

        val configuration = context.resources.configuration
        configuration.setLocale(locale)
        configuration.setLayoutDirection(locale)

        return context.createConfigurationContext(configuration)
    }

    @SuppressWarnings("deprecation")
    private fun updateResourcesLegacy(context: Context, language: String): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)

        val resources = context.resources

        val configuration = resources.configuration
        configuration.locale = locale
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLayoutDirection(locale)
        }

        resources.updateConfiguration(configuration, resources.displayMetrics)
        return context
    }
}