package com.tkw.kr.myapplication.util

import android.content.Context
import androidx.preference.PreferenceManager
import com.tkw.kr.myapplication.MainApplication
import com.tkw.kr.myapplication.R
import com.tkw.kr.myapplication.core.constant.C

object PreferenceUtil {
    fun putString(key: String, value: String?) {
        MainApplication.application?.let {
            val preferences = it.getSharedPreferences(it.getString(R.string.preference), Context.MODE_PRIVATE)
            val edit = preferences.edit()
            edit.putString(key, value)
            edit.apply()
        }

    }

    fun putStringSync(key: String, value: String?) {
        MainApplication.application?.let {
            val preferences = it.getSharedPreferences(it.getString(R.string.preference), Context.MODE_PRIVATE)
            val edit = preferences.edit()
            edit.putString(key, value)
            edit.commit()
        }
    }

    fun putSelectedLanguage(context: Context, language: String) {
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = pref.edit()
        editor.putString(C.Preference.SELECTED_LANGUAGE, language)
        editor.commit() //언어 재설정 시 apply() (비동기)로 호출하는 경우 preference에 저장이 안되어 commit()으로 호출
    }

    fun putInt(key: String, value: Int?) {
        MainApplication.application?.let {
            val preferences = it.getSharedPreferences(it.getString(R.string.preference), Context.MODE_PRIVATE)
            val edit = preferences.edit()
            edit.putInt(key, value ?: 0)
            edit.commit()
        }
    }

    fun getSelectedLanguage(context: Context?, defaultLanguage: String): String {
        return if(context != null) {
            val pref = PreferenceManager.getDefaultSharedPreferences(context)
            pref.getString(C.Preference.SELECTED_LANGUAGE, defaultLanguage) ?: ""
        } else {
            ""
        }
    }

    fun getString(key: String) : String =
        MainApplication.application?.let {
            val preference = it.getSharedPreferences(it.getString(R.string.preference), Context.MODE_PRIVATE)
            preference.getString(key, "")
        } ?: ""

    fun getString(key: String, default: String) : String =
        MainApplication.application?.let {
            val preference = it.getSharedPreferences(it.getString(R.string.preference), Context.MODE_PRIVATE)
            preference.getString(key, default)
        } ?: default

    fun getInt(key: String): Int =
            MainApplication.application?.let {
                val preference = it.getSharedPreferences(it.getString(R.string.preference), Context.MODE_PRIVATE)
                preference.getInt(key, 0)
            } ?: 0
    fun putBoolean(key: String, value: Boolean) {
        MainApplication.application?.let {
            val preferences = it.getSharedPreferences(it.getString(R.string.preference), Context.MODE_PRIVATE)
            val edit = preferences.edit()
            edit.putBoolean(key, value)
            edit.apply()
        }

    }

    fun putBooleanSync(key: String, value: Boolean) {
        MainApplication.application?.let {
            val preferences = it.getSharedPreferences(it.getString(R.string.preference), Context.MODE_PRIVATE)
            val edit = preferences.edit()
            edit.putBoolean(key, value)
            edit.commit()
        }
    }

    fun getBoolean(key: String, default: Boolean) : Boolean =
        MainApplication.application?.let {
            val preference = it.getSharedPreferences(it.getString(R.string.preference), Context.MODE_PRIVATE)
            preference.getBoolean(key, default)
        } ?: default
}