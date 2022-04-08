package com.tkw.kr.myapplication.component.map

import androidx.lifecycle.ViewModelProvider
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.tkw.kr.myapplication.R
import com.tkw.kr.myapplication.base.BaseView
import com.tkw.kr.myapplication.core.factory.MyProviderFactory

class NaverMapActivity: BaseView<MapViewModel>(), OnMapReadyCallback {
    override val layoutResourceId: Int
        get() = R.layout.activity_navermap
    override lateinit var viewModel: MapViewModel

    override fun initView() {
        viewModel = ViewModelProvider(this, MyProviderFactory()).get(MapViewModel::class.java)
        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.map_fragment) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.map_fragment, it).commit()
            }

        mapFragment.getMapAsync(this)
    }

    override fun initObserver() {

    }

    override fun initListener() {

    }

    override fun onMapReady(p0: NaverMap) {
        val cameraUpdate = CameraUpdate.scrollTo(LatLng(37.541, 126.986))
        p0.moveCamera(cameraUpdate)
        p0.moveCamera(CameraUpdate.zoomTo(10.0))
    }
}