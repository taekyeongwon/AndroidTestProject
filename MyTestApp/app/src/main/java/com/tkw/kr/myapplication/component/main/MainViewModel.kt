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
    private val _githubRepoData = MutableLiveData<GithubRepos>()
    val githubRepoData: LiveData<GithubRepos> get() = _githubRepoData

    fun getRepositoriesCoroutine(query: String) {
        viewModelScope.launch {
            _progressFlag.postValue(true)
            val response = model.getReposCoroutine(query)
            _progressFlag.postValue(false)
            when(response.status) {
                Status.SUCCESS -> _githubRepoData.postValue(response.data)
                Status.FAIL -> _alertFlag.postValue(response.throwable)
            }
        }
    }

    fun getRepositoriesCallback(query: String) {
        _progressFlag.postValue(true)
        model.getReposCallback(query, object: NetResultCallback<GithubRepos> {
            override fun onResponse(response: NetResult<GithubRepos>) {
                _progressFlag.postValue(false)
                when(response.status) {
                    Status.SUCCESS -> _githubRepoData.postValue(response.data)
                    Status.FAIL -> _alertFlag.postValue(response.throwable)
                }
            }
        })
    }

    fun getRepositoriesSingle(query: String) {
        _progressFlag.postValue(true)
        addDisposable(model.getReposSingle(query)
            .doFinally {
                _progressFlag.postValue(false)
            }
            .subscribe(
                {
                    _githubRepoData.postValue(it)
                }, {
                    if(it is AppError.Base) {
                        _alertFlag.postValue(it)
                    }
                }))
    }
}