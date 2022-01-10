package com.tkw.kr.myapplication.component.github

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.tkw.kr.myapplication.R
import com.tkw.kr.myapplication.base.BaseView
import com.tkw.kr.myapplication.core.factory.MyProviderFactory
import kotlinx.android.synthetic.main.activity_github.*

class GithubActivity: BaseView<GithubViewModel>() {
    override val layoutResourceId: Int
        get() = R.layout.activity_github
    override lateinit var viewModel: GithubViewModel

    override fun initView() {
        viewModel = ViewModelProvider(this, MyProviderFactory()).get(GithubViewModel::class.java)
        viewModel.getRepositoriesCallback("tkw")
    }

    override fun initObserver() {
        viewModel.githubRepoData.observe(this, Observer {
            if(it.items != null) {
                tv_repo_name.text = it.items[0].name
            }
        })
    }

    override fun initListener() {

    }
}