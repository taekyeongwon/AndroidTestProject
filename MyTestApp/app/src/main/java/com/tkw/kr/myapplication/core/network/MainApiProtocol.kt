package com.tkw.kr.myapplication.core.network

import com.tkw.kr.myapplication.core.network.base.BaseApiProtocol

/**
 * POST @Body
 * PUT @Body
 * GET @Query
 * DELETE @Query
 *
 * @Multipart @Part part: MultipartBody.Part
 */
interface MainApiProtocol: BaseApiProtocol {
    /**
     * 테스트
     */
//    @GET("search/repositories")
//    override suspend fun getRepositories(@Query("q") query: String): Response<GithubRepos>
//
//    @GET("search/repositories")
//    override fun getRepositories2(@Query("q") query: String): Call<GithubRepos>
//
//    @GET("search/repositoriess")
//    override fun notFound(@Query("q") query: String): Call<GithubRepos>

}