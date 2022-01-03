package com.tkw.kr.myapplication.core.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tkw.kr.myapplication.component.main.MainModelImpl
import com.tkw.kr.myapplication.component.main.MainViewModel

class MyProviderFactory: ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when(modelClass) {
            MainViewModel::class.java -> MainViewModel(MainModelImpl()) as T
            else -> throw IllegalArgumentException("Unknown Class")
        }
    }
}