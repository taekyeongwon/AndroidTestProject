package com.tkw.kr.myapplication.component.broad

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.*
import android.os.Build
import android.os.Bundle
import android.telecom.TelecomManager
import android.telephony.TelephonyManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.gson.Gson
import com.tkw.kr.myapplication.R
import com.tkw.kr.myapplication.core.network.MainApiServer
import com.tkw.kr.myapplication.core.network.base.BaseResponse
import com.tkw.kr.myapplication.core.network.base.NetResult
import com.tkw.kr.myapplication.core.network.base.NetResultCallback
import com.tkw.kr.myapplication.core.network.base.Status
import com.tkw.kr.myapplication.util.setOnSingleClickListener
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.SingleSubject
import kotlinx.android.synthetic.main.activity_broad.*

class BroadcastActivity: AppCompatActivity() {

    private lateinit var connectivityManager: ConnectivityManager
    private var singleNetwork: MutableLiveData<String> = MutableLiveData()
    private var singleTelephony: MutableLiveData<String> = MutableLiveData()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_broad)

        val builder = NetworkRequest.Builder()
        connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.registerNetworkCallback(builder.build(), networkCallback)

        val intentFilter = IntentFilter()
        intentFilter.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED)
        registerReceiver(callReceiver, intentFilter)
        singleNetwork.observe(this, Observer {
            tv_network_state.text = it
        })
        singleTelephony.observe(this, Observer {
            tv_telephone_state.text = it
        })

        btn_request.setOnSingleClickListener {
//            MainApiServer.parsingResponse(MainApiServer.API2.test(), object: NetResultCallback<BaseResponse> {
//                override fun onResponse(response: NetResult<BaseResponse>) {
//                    when(response.status) {
//                        Status.SUCCESS -> Log.d("retrofit", "success")
//                        Status.FAIL -> Log.d("retrofit", "fail " + response.throwable?.getStackTraceString())
//                    }
//                }
//            })
        }
    }

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            Log.d("network", "onAvailable")
            singleNetwork.postValue("onAvailable")
        }

        override fun onLosing(network: Network, maxMsToLive: Int) {
            super.onLosing(network, maxMsToLive)
            Log.d("network", "onLosing")
            singleNetwork.postValue("onLosing")
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            Log.d("network", "onLost")
            singleNetwork.postValue("onLosing")
        }

        override fun onUnavailable() {
            super.onUnavailable()
            Log.d("network", "onUnavailable")
            singleNetwork.postValue("onUnavailable")
        }

        override fun onCapabilitiesChanged(
            network: Network,
            networkCapabilities: NetworkCapabilities
        ) {
            super.onCapabilitiesChanged(network, networkCapabilities)
            Log.d("network", "onCapabilitiesChanged")
            singleNetwork.postValue("onCapabilitiesChanged")
        }

        override fun onLinkPropertiesChanged(network: Network, linkProperties: LinkProperties) {
            super.onLinkPropertiesChanged(network, linkProperties)
            Log.d("network", "onLinkPropertiesChanged")
            singleNetwork.postValue("onLinkPropertiesChanged")
        }

        override fun onBlockedStatusChanged(network: Network, blocked: Boolean) {
            super.onBlockedStatusChanged(network, blocked)
            Log.d("network", "onBlockedStatusChanged")
            singleNetwork.postValue("onBlockedStatusChanged")
        }
    }

    override fun onDestroy() {
        connectivityManager.unregisterNetworkCallback(networkCallback)
        unregisterReceiver(callReceiver)
        super.onDestroy()
    }

    private val callReceiver = object: BroadcastReceiver() {
        var phonestate: String? = ""
        private lateinit var connectivityManager: ConnectivityManager

        override fun onReceive(p0: Context?, p1: Intent?) {
            p1?.let {
                if(it.action.equals("android.intent.action.PHONE_STATE")) {
                    connectivityManager = p0?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                    val tm = p0?.getSystemService(Context.TELECOM_SERVICE) as TelecomManager

                    val extra = it.extras
                    extra?.let {
                        val state = it.getString(TelephonyManager.EXTRA_STATE)
                        if (state.equals(phonestate)) {
                            return
                        } else {
                            phonestate = state
                        }

                        when(state) {
                            TelephonyManager.EXTRA_STATE_RINGING -> Log.d("network", "telephony 통화벨 울리는 중")
                            TelephonyManager.EXTRA_STATE_OFFHOOK -> Log.d("network", "telephony 통화중")
                            TelephonyManager.EXTRA_STATE_IDLE -> Log.d("network", "telephony 통화 종료")
                            else -> Log.d("network", "telephony default")
                        }

                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            val activeNetwork = connectivityManager.activeNetwork
                            val capableNetwork = connectivityManager.getNetworkCapabilities(activeNetwork)
                            capableNetwork?.let {
                                if(it.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                                    Log.d("network", "telephony wifi, $state")
                                    singleTelephony.postValue("wifi, $state")
                                } else if(it.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                                    Log.d("network", "telephony cellular, $state")
                                    singleTelephony.postValue("cellular, $state")
                                } else {
                                    Log.d("network", "telephonynot connect, $state")
                                    singleTelephony.postValue("not connect, $state")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}