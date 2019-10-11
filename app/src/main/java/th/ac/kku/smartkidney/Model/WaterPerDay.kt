package th.ac.kku.smartkidney

import com.google.gson.annotations.SerializedName

data class WaterPerDay(
        @SerializedName("date") val date: String,
        @SerializedName("waterIn") val waterIn: Int
)