package th.ac.kku.smartkidney

import com.google.gson.annotations.SerializedName

data class BMI(
        @SerializedName("date") val date: String,
        @SerializedName("bmi") val bmi: Double
)