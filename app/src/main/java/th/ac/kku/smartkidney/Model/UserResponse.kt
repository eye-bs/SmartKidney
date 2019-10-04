package th.ac.kku.smartkidney

import com.google.gson.annotations.SerializedName

data class UserResponse(
        @SerializedName("firstLogin") val firstLogin: Boolean,
        @SerializedName("user") val users: User

)