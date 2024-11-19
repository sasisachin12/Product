package aaa.android.sasikumar.ui

import aaa.android.sasikumar.data.ResponseUiState
import aaa.android.sasikumar.data.model.SchoolItem
import aaa.android.sasikumar.data.viewmodel.ProductViewModel
import aaa.android.sasikumar.utils.Util.IndeterminateCircularIndicator
import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LiveData
import androidx.navigation.NavHostController

@Composable
fun SchoolList(
    navHostController: NavHostController,
    viewModel: ProductViewModel = hiltViewModel<ProductViewModel>()
) {
    DisplayListView(
        navHostController,
        viewModel::getProductLists, {
            viewModel.productListLiveData
        }, Modifier.padding(6.dp)
    )
}


@Composable
fun DisplayListView(
    navHostController: NavHostController,
    getProductList: suspend () -> Unit,
    bookItems: () -> LiveData<ResponseUiState<List<SchoolItem>>>,
    modifier: Modifier
) {

    LaunchedEffect(Unit) {
        getProductList()
    }
    val items by bookItems.invoke().observeAsState()

    when (items) {
        is ResponseUiState.Success<*> -> {

            val books = items?.data as List<SchoolItem>
            if (books.isNotEmpty()) {
                //val sortedList = books.
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    items(
                        items = books,
                        itemContent = {
                            BookListItem(bookItem = it, navHostController)
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
fun BookListItem(bookItem: SchoolItem, navHostController: NavHostController) {
    val schoolData = bookItem.school_name
    Row(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth()
    ) {
        val subTitle = schoolData.toString()
        if (subTitle.isNotEmpty()) {
            TextButton(
                onClick = {
                    navHostController.navigate(
                        "SchoolDetails?schoolId=${bookItem.dbn}"
                    )
                }) {
                Text(subTitle)
            }
        }


    }


}











