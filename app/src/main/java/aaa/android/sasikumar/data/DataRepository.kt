package aaa.android.sasikumar.data

import aaa.android.sasikumar.data.model.SchoolDetailItem
import aaa.android.sasikumar.data.model.SchoolItem
import aaa.android.sasikumar.di.ApiService
import aaa.android.sasikumar.di.modules.ApplicationScope
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext
import javax.inject.Inject

@ActivityScoped
class DataRepository @Inject constructor(
    private val apiService: ApiService,
    @ApplicationScope private val ioScope: CoroutineScope
) {


    suspend fun getProductList():List<SchoolItem>{
        return withContext(ioScope.coroutineContext) {
            apiService.getProductList()
        }
    }
    suspend fun getSchoolDetailList():List<SchoolDetailItem>{
        return withContext(ioScope.coroutineContext) {
            apiService.getSchoolDetailList()
        }
    }
}