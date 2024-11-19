package aaa.android.sasikumar


import aaa.android.sasikumar.ui.SchoolDetailsScreen
import aaa.android.sasikumar.ui.SchoolList
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument


@Composable
fun AppNavHost(navHostController: NavHostController, startDestination: String, modifier: Modifier) {
    NavHost(
        navController = navHostController,
        startDestination = startDestination,
        modifier = modifier.padding(8.dp)
    ) {
        composable(route = "SchoolList") {
            SchoolList(navHostController)
        }
        composable(  route = "SchoolDetails?schoolId={schoolId}",
            arguments = listOf(
                navArgument(
                    name = "schoolId"
                ) {
                    type = NavType.StringType
                    defaultValue = ""
                },
            )) {
            val schoolId =  it.arguments?.getString("schoolId")
            SchoolDetailsScreen(navHostController, schoolId.toString())
        }
    }
}