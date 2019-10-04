package th.ac.kku.smartkidney

import com.google.gson.annotations.SerializedName

data class BloodSugar(
        @SerializedName("date") val date: String,
        @SerializedName("sugarLevel") val sugarLevel: Int,
        @SerializedName("hba1c") val hba1c: Int
)