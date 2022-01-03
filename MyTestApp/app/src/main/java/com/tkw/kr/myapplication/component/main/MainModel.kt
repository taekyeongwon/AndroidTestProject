package com.tkw.kr.myapplication.component.main

import com.tkw.kr.myapplication.core.network.MainApiServer
import com.tkw.kr.myapplication.core.network.base.NetResult
import com.tkw.kr.myapplication.core.network.base.NetResultCallback
import io.reactivex.Single

interface MainModel {
    suspend fun getRepos(query: String): NetResult<GithubRepos>             //코루틴
    fun getRepos2(query: String, callback: NetResultCallback<GithubRepos>)  //콜백
//    fun getRepos3(query: String): Single<GithubRepos>                       //Single
}

class MainModelImpl: MainModel {
    override suspend fun getRepos(query: String): NetResult<GithubRepos> {
        return MainApiServer.parsingResponseSuspend {
            MainApiServer.API.getRepositories(query)
        }
    }

    override fun getRepos2(query: String, callback: NetResultCallback<GithubRepos>) {
        MainApiServer.parsingResponse(MainApiServer.API.getRepositories2(query), callback)
    }

//    override fun getRepos3(query: String): Single<GithubRepos> {
//        return MainApiServer.API.getRepositories2(query).toSingle()
//    }
}