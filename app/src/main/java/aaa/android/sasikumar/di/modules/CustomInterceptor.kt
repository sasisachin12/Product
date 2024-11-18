package aaa.android.sasikumar.di.modules

import aaa.android.sasikumar.R
import aaa.android.sasikumar.utils.Util
import android.content.Context


import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CustomInterceptor @Inject constructor(@ApplicationContext private val context: Context) :
    Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        if (!Util.isNetworkAvailable(context))
            throw NoConnectivityException(context.getString(R.string.no_internet))

        val request = chain.request()
        val url = request.url
        val queryParams = url
            .newBuilder()
            // .addQueryParameter(AppConstant.KEY_API, BuildConfig.API_KEY)
            .build()
        val requestBuilder = request
            .newBuilder()
            .url(queryParams)
            .build()
        val response = chain.proceed(requestBuilder)
        return if (response.isSuccessful) response else throw UnknownException(response.message)
    }
}


class NoConnectivityException(override var message: String) : IOException()
class UnknownException(override var message: String) : IOException()