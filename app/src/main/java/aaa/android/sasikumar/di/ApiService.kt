package aaa.android.sasikumar.di

import aaa.android.sasikumar.data.model.SchoolDetailItem
import aaa.android.sasikumar.data.model.SchoolItem
import aaa.android.sasikumar.utils.AppConst.GET_SCHOOL_LIST
import aaa.android.sasikumar.utils.AppConst.GET_SCHOOL_DETAIL_LIST
import retrofit2.http.GET

interface ApiService {

    @GET(GET_SCHOOL_LIST)
    suspend fun getProductList(): List<SchoolItem>


    @GET(GET_SCHOOL_DETAIL_LIST)
    suspend fun getSchoolDetailList(): List<SchoolDetailItem>
}