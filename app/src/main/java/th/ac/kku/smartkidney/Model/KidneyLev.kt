package th.ac.kku.smartkidney

import com.google.gson.annotations.SerializedName

data class KidneyLev(
        @SerializedName("date") val date: String,
        @SerializedName("cr") val cr: Int
)