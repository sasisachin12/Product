package aaa.android.product.data.viewmodel

import aaa.android.product.data.DataRepository
import aaa.android.product.data.ResponseUiState
import aaa.android.product.data.model.ProductItemItem
import aaa.android.product.di.modules.ApplicationScope
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
    private val _productLiveData = MutableLiveData<ResponseUiState<List<ProductItemItem>>>()
    val productListLiveData: LiveData<ResponseUiState<List<ProductItemItem>>> = _productLiveData


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

}