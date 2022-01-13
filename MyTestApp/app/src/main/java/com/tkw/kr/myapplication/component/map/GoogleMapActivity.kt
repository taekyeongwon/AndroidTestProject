package com.tkw.kr.myapplication.component.map

import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.tkw.kr.myapplication.R
import com.tkw.kr.myapplication.base.BaseView
import com.tkw.kr.myapplication.core.factory.MyProviderFactory

class GoogleMapActivity: BaseView<MapViewModel>(), OnMapReadyCallback {
    override val layoutResourceId: Int
        get() = R.layout.activity_googlemap
    override lateinit var viewModel: MapViewModel

    private lateinit var mMap: GoogleMap

    override fun initView() {
        viewModel = ViewModelProvider(this, MyProviderFactory()).get(MapViewModel::class.java)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun initObserver() {

    }

    override fun initListener() {

    }

    override fun onMapReady(p0: GoogleMap) {
        mMap = p0
        mMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(37.541, 126.986)))
        mMap.moveCamera(CameraUpdateFactory.zoomTo(10.0f))
    }
}