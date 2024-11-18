package aaa.android.sasikumar.utils

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

object Util {

    fun isNetworkAvailable(@ApplicationContext context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                    return true
                }
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                    return true
                }
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                    return true
                }
            }
        }
        return false
    }

    @Composable
    fun IndeterminateCircularIndicator() {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            CircularProgressIndicator(
                modifier = Modifier.width(64.dp),
                color = MaterialTheme.colorScheme.secondary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
        }
    }

    @Composable
    fun NoInternetConnectionView() {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            BasicTextField(
                value = "Please check your Internet Connection",
                textStyle = typography.titleLarge,
                modifier = Modifier.padding(24.dp),
                onValueChange = { }
            )
        }
    }


    @ExperimentalCoroutinesApi
    class NetworkConnectivityObserver(
        application: Application
    ) : ConnectivityObserver {

        private val connectivityManager =
            application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        override fun observe(): Flow<ConnectivityObserver.Status> {
            return callbackFlow {
                val callback = object : ConnectivityManager.NetworkCallback() {
                    override fun onAvailable(network: Network) {
                        super.onAvailable(network)
                        launch { trySend(ConnectivityObserver.Status.Available) }
                    }

                    override fun onLosing(network: Network, maxMsToLive: Int) {
                        super.onLosing(network, maxMsToLive)
                        launch { trySend(ConnectivityObserver.Status.Losing) }
                    }

                    override fun onLost(network: Network) {
                        super.onLost(network)
                        launch { trySend(ConnectivityObserver.Status.Lost) }
                    }

                    override fun onUnavailable() {
                        super.onUnavailable()
                        launch { send(ConnectivityObserver.Status.Unavailable) }

                    }
                }
                connectivityManager.registerDefaultNetworkCallback(callback)
                awaitClose {
                    connectivityManager.unregisterNetworkCallback(callback)
                }
            }.distinctUntilChanged()
        }

        override fun isConnected(): ConnectivityObserver.Status {
            return if (connectivityManager.activeNetwork != null
                &&
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork) != null
            )
                ConnectivityObserver.Status.Available
            else
                ConnectivityObserver.Status.Unavailable
        }
    }

    interface ConnectivityObserver {

        fun observe(): Flow<Status>

        fun isConnected(): Status

        enum class Status(val message: String) {
            Available("Back online"),
            Unavailable("No internet connection"),
            Losing("Internet connection lost"),
            Lost("No internet connection")
        }
    }
}