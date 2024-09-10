package aaa.android.product

import aaa.android.product.data.ResponseUiState
import aaa.android.product.data.model.Item
import aaa.android.product.data.model.ProductItemItem
import aaa.android.product.data.viewmodel.ProductViewModel
import aaa.android.product.ui.theme.AndroidProductTheme
import aaa.android.product.utils.Util.isNetworkAvailable
import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import coil.compose.AsyncImage
import coil.request.ImageRequest
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.distinctUntilChanged

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: ProductViewModel by viewModels()
    private lateinit var connectivityObserver: ConnectivityObserver

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
                        DisplayListView(
                            viewModel::getProductLists
                        ) {
                            viewModel.productListLiveData
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


@Composable
fun DisplayListView(
    getProductList: suspend () -> Unit,
    bookItems: () -> LiveData<ResponseUiState<List<ProductItemItem>>>
) {

    LaunchedEffect(Unit) {
        getProductList()
    }
    val items by bookItems.invoke().observeAsState()

    when (items) {
        is ResponseUiState.Success<*> -> {

            val books = items?.data as List<ProductItemItem>
            if (books.isNotEmpty()) {
                val sortedList = sortingList(books)
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    items(
                        items = sortedList,
                        itemContent = {
                            BookListItem(bookItem = it)
                        })
                }


            }
        }

        is ResponseUiState.Error -> {
            val error = items?.message
            Text(text = error.toString(), style = typography.titleLarge)
            Log.e("ResponseUiState.Error", "DisplayListView: $error")
        }

        is ResponseUiState.Loading -> {
            IndeterminateCircularIndicator()
        }

        null -> {

        }

    }

}

@Composable
fun BookListItem(bookItem: ProductItemItem) {

    val sectionType = bookItem.sectionType
    Row {

        // PuppyImage(puppy)
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .align(Alignment.CenterVertically)
        ) {


            if (sectionType.isNotEmpty()) {
                Text(
                    text = sectionType,
                    style = typography.labelLarge
                )
            }

        }
    }

    LazyRow(
        contentPadding = PaddingValues(horizontal = 2.dp, vertical = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(
            items = bookItem.items,
            itemContent = {
                ProductItem(bookItem = it, sectionType)
            })
    }
}


@Composable
fun ProductItem(bookItem: Item, sectionType: String) {


    Column(
        modifier = Modifier
            .padding(2.dp)
            .fillMaxWidth()

    ) {
        val imageUrl = bookItem.image
        val subTitle = bookItem.title
        if (imageUrl.isNotEmpty()) {
            if (sectionType.lowercase() == "horizontalFreeScroll".lowercase()) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(imageUrl)
                        .build(),
                    contentDescription = "image",
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier
                        .height(124.dp)
                        .width(124.dp)
                )
            }
            if (sectionType.lowercase() == "splitBanner".lowercase()) {
                val configuration = LocalConfiguration.current
                val screenWidth = configuration.screenWidthDp.dp / 3
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(imageUrl)
                        .build(),
                    contentDescription = "image",
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier
                        .height(240.dp)
                        .width(screenWidth)

                )
            }
            if (sectionType.lowercase() == "banner".lowercase()) {
                val configuration = LocalConfiguration.current
                val screenWidth = configuration.screenWidthDp.dp
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(imageUrl)
                        .build(),
                    contentDescription = "image",
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .height(240.dp)
                        .width(screenWidth)


                )
            }

        }
        if (subTitle.isNotEmpty()) {
            BasicTextField(
                value = subTitle,
                textStyle = typography.labelMedium,
                modifier = Modifier.padding(8.dp),
                onValueChange = { },
                singleLine = true
            )
        }


    }

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

fun sortingList(productItemItems: List<ProductItemItem>): List<ProductItemItem> {

    val sortedList = mutableListOf<ProductItemItem>()
    sortedList.addAll(productItemItems.filter { it.sectionType.lowercase() == "horizontalFreeScroll".lowercase() })
    sortedList.addAll(productItemItems.filter { it.sectionType.lowercase() == "splitBanner".lowercase() })
    sortedList.addAll(productItemItems.filter { it.sectionType.lowercase() == "banner".lowercase() })

    return sortedList
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
                override fun onAvailable(network: android.net.Network) {
                    super.onAvailable(network)
                    launch { trySend(ConnectivityObserver.Status.Available) }
                }

                override fun onLosing(network: android.net.Network, maxMsToLive: Int) {
                    super.onLosing(network, maxMsToLive)
                    launch { trySend(ConnectivityObserver.Status.Losing) }
                }

                override fun onLost(network: android.net.Network) {
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


