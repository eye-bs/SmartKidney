package th.ac.kku.smartkidney

import com.google.gson.annotations.SerializedName

data class BloodPressure(
        @SerializedName("date") val date: String,
        @SerializedName("systolic") val systolic: Int,
        @SerializedName("diastolic") val diastolic: Int
)