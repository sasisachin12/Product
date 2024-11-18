package aaa.android.sasikumar.data.viewmodel

import aaa.android.sasikumar.data.DataRepository
import aaa.android.sasikumar.data.ResponseUiState
import aaa.android.sasikumar.data.model.SchoolDetailItem
import aaa.android.sasikumar.data.model.SchoolItem
import aaa.android.sasikumar.di.modules.ApplicationScope
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val repository: DataRepository,
    @ApplicationScope private var ioScope: CoroutineScope
) : ViewModel() {
    private val _productLiveData = MutableLiveData<ResponseUiState<List<SchoolItem>>>()
    val productListLiveData: LiveData<ResponseUiState<List<SchoolItem>>> = _productLiveData

    private val _schoolDetailLiveData = MutableLiveData<ResponseUiState<List<SchoolDetailItem>>>()
    val schoolDetailLiveData: LiveData<ResponseUiState<List<SchoolDetailItem>>> = _schoolDetailLiveData


    suspend fun getProductLists() {


        _productLiveData.apply {
            postValue(ResponseUiState.Loading())
        }

        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            _productLiveData.apply {
                postValue(
                    exception.message?.let {
                        ResponseUiState.Error(it)
                    }
                )
            }
        }
        try {
            viewModelScope.launch(ioScope.coroutineContext + exceptionHandler) {
                val results = repository.getProductList()
                _productLiveData.apply {
                    postValue(ResponseUiState.Success(results))

                }
            }
        } catch (_: Exception) {


        }
    }
    suspend fun getSchoolDetailLists() {


        _schoolDetailLiveData.apply {
            postValue(ResponseUiState.Loading())
        }

        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            _schoolDetailLiveData.apply {
                postValue(
                    exception.message?.let {
                        ResponseUiState.Error(it)
                    }
                )
            }
        }
        try {
            viewModelScope.launch(ioScope.coroutineContext + exceptionHandler) {
                val results = repository.getSchoolDetailList()
                _schoolDetailLiveData.apply {
                    postValue(ResponseUiState.Success(results))

                }
            }
        } catch (_: Exception) {


        }
    }

}