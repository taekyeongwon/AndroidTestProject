package com.tkw.kr.myapplication.component.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tkw.kr.myapplication.base.BaseViewModel
import com.tkw.kr.myapplication.core.network.base.NetResult
import com.tkw.kr.myapplication.core.network.base.NetResultCallback
import com.tkw.kr.myapplication.core.network.base.Status
import com.tkw.kr.myapplication.core.network.error.AppError
import kotlinx.coroutines.launch

class MainViewModel(private val model: MainModel): BaseViewModel() {
}