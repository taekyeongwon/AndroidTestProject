package com.tkw.kr.myapplication.component.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tkw.kr.myapplication.base.BaseViewModel
import com.tkw.kr.myapplication.core.network.base.NetResult
import com.tkw.kr.myapplication.core.network.base.NetResultCallback
import com.tkw.kr.myapplication.core.network.base.Status
import kotlinx.coroutines.launch

class MainViewModel(private val model: MainModel): BaseViewModel() {
    private val _githubRepoData = MutableLiveData<GithubRepos>()
    val githubRepoData: LiveData<GithubRepos> get() = _githubRepoData

    fun getRepositories(query: String) {
        viewModelScope.launch {
            _progressFlag.postValue(true)
            val response = model.getRepos(query)
            _progressFlag.postValue(false)
            when(response.status) {
                Status.SUCCESS -> _githubRepoData.postValue(response.data)
                Status.FAIL -> _alertFlag.postValue(response.throwable)
            }
        }
    }

    fun getRepositories2(query: String) {
        _progressFlag.postValue(true)
        model.getRepos2(query, object: NetResultCallback<GithubRepos> {
            override fun onResponse(response: NetResult<GithubRepos>) {
                _progressFlag.postValue(false)
                when(response.status) {
                    Status.SUCCESS -> _githubRepoData.postValue(response.data)
                    Status.FAIL -> _alertFlag.postValue(response.throwable)
                }
            }
        })
    }
}