package com.tkw.kr.myapplication.util

import android.os.Environment
import android.os.Process
import android.util.Log
import androidx.multidex.BuildConfig
import com.tkw.kr.myapplication.MainApplication
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

object Logger {

    val TAG = MainApplication.application?.packageName ?: "MyApp"
    val FILE_LOGGING = false

    /**
     * Log Level Error
     */
    fun e(message: String) {
        if (BuildConfig.DEBUG) Log.e(TAG, message)
        if (FILE_LOGGING) makeLogFile("E", message)
    }

    /**
     * Log Level Warning
     */
    fun w(message: String) {
        if (BuildConfig.DEBUG) Log.w(TAG, message)
        if (FILE_LOGGING) makeLogFile("W", message)
    }

    /**
     * Log Level Information
     */
    fun i(message: String) {
        if (BuildConfig.DEBUG) Log.i(TAG, message)
        if (FILE_LOGGING) makeLogFile("I", message)
    }

    /**
     * Log Level Debug
     */
    fun d(message: String) {
        if (BuildConfig.DEBUG) Log.d(TAG, message)
        if (FILE_LOGGING) makeLogFile("D", message)
    }

    /**
     * Log Level Verbose
     */
    fun v(message: String) {
        if (BuildConfig.DEBUG) Log.v(TAG, message)
        if (FILE_LOGGING) makeLogFile("V", message)
    }

    private fun makeLogFile(logLevel: String, msg: String?) {
        var bfw: BufferedWriter? = null
        val builder = StringBuilder()
        val logPath = Environment.getExternalStorageDirectory().absolutePath+"/log"
        try {

            bfw = BufferedWriter(FileWriter(createFile(logPath), true))
            val simpleDataFormat = SimpleDateFormat("MM-dd HH:mm:ss.SSS", Locale.KOREA)
            val currentTime = Date()
            val dTime = simpleDataFormat.format(currentTime)
            val pid = Process.myPid()
            val tid = Process.getThreadPriority(Process.myTid())

            builder
                .append(dTime).append(": ")
                .append(pid).append("-").append(tid)
                .append("/").append(MainApplication.application?.packageName)
                .append(" ").append(logLevel)
                .append("/").append(TAG).append(": ")
                .append(msg)

            bfw.write(builder.toString())
            bfw.newLine()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            bfw?.close()
        }

    }

    private fun createFile(path: String): File {
        val file = File(path)
        val logFile = File(path, getTime())
        if (!file.exists()) file.mkdir()
        if (!logFile.exists()) logFile.createNewFile()

        return logFile
    }

    private fun getTime(): String {
        val now = System.currentTimeMillis()
        val date = Date(now)
        val sdf = SimpleDateFormat("yyyyMMdd_hh-mm", Locale.getDefault())
        val time = sdf.format(date)
        return "$time.txt"
    }
}