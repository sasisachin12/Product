package aaa.android.sasikumar.data

sealed class ResponseUiState<T>(val data: T? = null, val message: String? = null) {
    class Loading<T> : ResponseUiState<T>()
    data class Success<T>(val resultsData: T) : ResponseUiState<T>(resultsData)
    class Error<T>(message: String, data: T? = null) : ResponseUiState<T>(data, message)
}
