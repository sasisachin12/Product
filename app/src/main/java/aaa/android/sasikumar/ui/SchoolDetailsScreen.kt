package aaa.android.sasikumar.ui

import aaa.android.sasikumar.data.ResponseUiState
import aaa.android.sasikumar.data.model.SchoolDetailItem
import aaa.android.sasikumar.data.viewmodel.ProductViewModel
import aaa.android.sasikumar.utils.Util.IndeterminateCircularIndicator
import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LiveData
import androidx.navigation.NavHostController

@Composable
fun SchoolDetailsScreen(
    navHostController: NavHostController,
    schoolId: String,
    viewModel: ProductViewModel = hiltViewModel<ProductViewModel>()
) {
    DisplaySchoolDetailView(
        viewModel::getSchoolDetailLists,
        {
            viewModel.schoolDetailLiveData
        },
        schoolId
    )
}

@Composable
fun DisplaySchoolDetailView(
    getProductList: suspend () -> Unit,
    bookItems: () -> LiveData<ResponseUiState<List<SchoolDetailItem>>>,
    dbn: String
) {

    LaunchedEffect(Unit) {
        getProductList()
    }
    val items by bookItems.invoke().observeAsState()

    when (items) {
        is ResponseUiState.Success<*> -> {

            val books = items?.data as List<SchoolDetailItem>
            if (books.isNotEmpty()) {
                val sortedList = books.filter { it.dbn == dbn }
                LazyColumn(
                    contentPadding = PaddingValues(32.dp)
                ) {
                    items(sortedList) { bookItem ->
                        val schoolData = bookItem.school_name.toString()
                        val satTestTakers = bookItem.num_of_sat_test_takers
                        val criticalReadingAvgScore = bookItem.sat_critical_reading_avg_score
                        val satMathAvgScore = bookItem.sat_math_avg_score
                        val satWritingAvgScore = bookItem.sat_writing_avg_score
                        if (schoolData.isNotEmpty()) {
                            Text(
                                text = schoolData,
                                style = typography.titleLarge
                            )

                        }
                        Text(
                            text = "SAT Test Takers :$satTestTakers",
                            style = typography.titleSmall,

                            )
                        Text(
                            text = "Critical Reading Avg Score : $criticalReadingAvgScore",
                            style = typography.titleSmall,

                            )
                        Text(
                            text = "Sat Math Avg Score : $satMathAvgScore",
                            style = typography.titleSmall,

                            )
                        Text(
                            text = "Sat Writing Avg Score : $satWritingAvgScore",
                            style = typography.titleSmall,

                            )

                    }
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