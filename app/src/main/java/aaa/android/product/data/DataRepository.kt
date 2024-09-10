package aaa.android.product.data

import aaa.android.product.data.model.ProductItemItem
import aaa.android.product.di.ApiService
import aaa.android.product.di.modules.ApplicationScope
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext
import javax.inject.Inject

@ActivityScoped
class DataRepository @Inject constructor(
    private val apiService: ApiService,
    @ApplicationScope private val ioScope: CoroutineScope
) {


    suspend fun getProductList():List<ProductItemItem>{
        return withContext(ioScope.coroutineContext) {
            apiService.getProductList()
        }
    }
}