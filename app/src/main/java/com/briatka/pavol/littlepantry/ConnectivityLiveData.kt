package com.briatka.pavol.littlepantry

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkInfo
import android.net.NetworkRequest
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import javax.inject.Inject

/**Class to observe the changes in network connection - whenever the user is
 * disconnected he'll be notified to check their connection in order to
 * proceed with a network requests*/

class ConnectivityLiveData @VisibleForTesting internal constructor(
    private val connectivityManager: ConnectivityManager ) : LiveData<Boolean>() {

    @RequiresPermission(android.Manifest.permission.ACCESS_NETWORK_STATE)
    @Inject constructor(application: Application) : this(application.getSystemService(Context.CONNECTIVITY_SERVICE)
    as ConnectivityManager)

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {

        override fun onAvailable(network: Network?) {
            postValue(true)
        }

        override fun onLost(network: Network?) {
            postValue(false)
        }
    }

    override fun onActive() {
        super.onActive()

        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
        postValue(activeNetwork?.isConnectedOrConnecting == true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager.registerDefaultNetworkCallback(networkCallback)
        } else {
            val builder = NetworkRequest.Builder()
            connectivityManager.registerNetworkCallback(builder.build(), networkCallback)
        }
    }

    override fun onInactive() {
        super.onInactive()
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }
}