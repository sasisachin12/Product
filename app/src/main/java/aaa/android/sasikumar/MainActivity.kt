package aaa.android.sasikumar

import aaa.android.sasikumar.data.viewmodel.ProductViewModel
import aaa.android.sasikumar.ui.theme.AndroidProductTheme
import aaa.android.sasikumar.utils.Util.ConnectivityObserver
import aaa.android.sasikumar.utils.Util.NetworkConnectivityObserver
import aaa.android.sasikumar.utils.Util.NoInternetConnectionView
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: ProductViewModel by viewModels()
    private lateinit var connectivityObserver: ConnectivityObserver

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        connectivityObserver = NetworkConnectivityObserver(application = application)

        enableEdgeToEdge()
        setContent {
            AndroidProductTheme {
                val status by connectivityObserver.observe()
                    .collectAsState(initial = connectivityObserver.isConnected())
                when (status) {
                    ConnectivityObserver.Status.Available -> {

                        Scaffold(
                            topBar = {
                                TopAppBar(
                                    colors = TopAppBarDefaults.topAppBarColors(
                                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                                        titleContentColor = MaterialTheme.colorScheme.primary,
                                    ),
                                    title = {
                                        Text("School List")
                                    }
                                )
                            },
                        ) { innerPadding ->
                            AppNavHost(
                                rememberNavController(),
                                "SchoolList",
                                Modifier.padding(innerPadding)
                            )
                        }

                    }

                    ConnectivityObserver.Status.Unavailable, ConnectivityObserver.Status.Losing,
                    ConnectivityObserver.Status.Lost -> {
                        NoInternetConnectionView()

                    }
                }

            }
        }
    }

}











