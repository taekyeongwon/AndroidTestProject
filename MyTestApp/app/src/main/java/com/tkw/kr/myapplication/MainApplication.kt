package com.tkw.kr.myapplication

import android.content.Context
import android.os.Process
import androidx.multidex.MultiDexApplication
import com.tkw.kr.myapplication.core.helper.LocaleHelper
import com.tkw.kr.myapplication.util.Logger
import java.io.PrintWriter
import java.io.StringWriter
import java.lang.ref.WeakReference

class MainApplication: MultiDexApplication() {
    companion object {
        private var _application: WeakReference<MainApplication>? = null

        val application: MainApplication?
            get() {
                if (_application != null) {
                    return _application!!.get()
                }
                return null
            }

        fun getString(stringResId: Int): String {
            return application?.getString(stringResId) ?: ""
        }
    }

    override fun onCreate() {
        super.onCreate()
        _application = WeakReference(this)
        setUncaughtExceptionHandler()
    }

    private fun setUncaughtExceptionHandler() {
        val defaultHandler: Thread.UncaughtExceptionHandler? = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            Logger.e("${thread.name} + : + ${getStackTrace(throwable)}")

            defaultHandler?.uncaughtException(thread, throwable)
            Process.killProcess(Process.myPid())
            System.exit(0)
        }
    }

    private fun getStackTrace(e: Throwable): String {
        val result = StringWriter()
        val printWriter = PrintWriter(result)

        var th: Throwable? = e
        while(th != null) {
            th.printStackTrace(printWriter)
            th = th.cause
        }

        val stackTraceAsString = result.toString()
        printWriter.close()

        return stackTraceAsString
    }

    override fun attachBaseContext(base: Context) {
        val lang = LocaleHelper.getSelectedLanguage(base, "")
        super.attachBaseContext(LocaleHelper.getUpdateContext(base, lang))
    }
}