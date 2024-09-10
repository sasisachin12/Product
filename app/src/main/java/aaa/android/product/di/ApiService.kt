package aaa.android.product.di

import aaa.android.product.data.model.ProductItemItem
import aaa.android.product.utils.AppConst.GET_PRODUCT_LIST
import retrofit2.http.GET

interface ApiService {

    @GET(GET_PRODUCT_LIST)
    suspend fun getProductList(): List<ProductItemItem>
}