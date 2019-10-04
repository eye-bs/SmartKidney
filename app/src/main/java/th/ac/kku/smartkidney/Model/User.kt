package th.ac.kku.smartkidney

import com.google.gson.annotations.SerializedName

data class User(
        @SerializedName("_id") val id: String,
        @SerializedName("email") val email: String,
        @SerializedName("name") val name: String,
        @SerializedName("birthDate") val birthDate: String,
        @SerializedName("gender") val gender: String,
        @SerializedName("hospital") val hospital: String,
        @SerializedName("weight") val weight: Int,
        @SerializedName("height") val height: Int
)