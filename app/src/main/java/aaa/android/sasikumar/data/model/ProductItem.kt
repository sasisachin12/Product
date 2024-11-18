package aaa.android.sasikumar.data.model



data class SchoolItem(
    val dbn: String?= null,
    val school_name: String?= null,
    val sat_critical_reading_avg_score: String?= null,
    val sat_math_avg_score: String?= null,
    val sat_writing_avg_score: String?= null
)

data class SchoolDetailItem(
    val dbn: String?= null,
    val school_name: String?= null,
    val num_of_sat_test_takers: String?= null,
    val sat_critical_reading_avg_score: String?= null,
    val sat_math_avg_score: String?= null,
    val sat_writing_avg_score: String?= null,
)