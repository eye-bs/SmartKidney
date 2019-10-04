package th.ac.kku.smartkidney


import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.*


interface SmartKidneyAPI {
    // user
    @FormUrlEncoded
    @POST("login")
    fun login(@Field("email") email: String): Observable<UserResponse>

    @GET("users/{uid}")
    fun getUserInfo(@Path("uid") uid:String): Observable<User>

    @FormUrlEncoded
    @POST("register")
    fun register(@Field("email")email: String,
                 @Field("name")name: String,
                 @Field("birthDate")birthDate: String,
                 @Field("gender")gender: String,
                 @Field("hospital")hospital: String)
            :Observable<User>

    @FormUrlEncoded
    @PATCH("users/{uid}")
    fun editUserInfo(@Field("email")email: String,
                       @Field("name")name: String,
                       @Field("birthDate")birthDate: String,
                       @Field("gender")gender: String,
                       @Field("hospital")hospital: String,
                       @Field("weight")weight: Int,
                       @Field("height")height: Int)
            : Observable<ResponseBody>

    // blood pressure
    @GET("bp/{uid}")
    fun getBloodPressure(@Path("uid") uid:String,
                         @Query("week") week: Int?,
                         @Query("year") year: Int?)
            : Observable<List<BloodPressure>>

    @FormUrlEncoded
    @POST("bp/{uid}")
    fun postBloodPressure(@Path("uid") uid:String,
                          @Field("systolic") systolic:Int,
                          @Field("diastolic") diastolic:Int)
            :Observable<BloodPressure>

    // blood sugar
    @GET("bs/{uid}")
    fun getBloodSugar(@Path("uid") uid:String,
                         @Query("week") week: Int?,
                         @Query("year") year: Int?)
            : Observable<List<BloodSugar>>

    @FormUrlEncoded
    @POST("bs/{uid}")
    fun postBloodSugar(@Path("uid") uid:String,
                          @Field("sugarLevel") systolic:Int,
                          @Field("hba1c") diastolic:Int)
            :Observable<BloodSugar>

    // glomerular infiltration
    @GET("git/{uid}")
    fun getKidneyLev(@Path("uid") uid:String,
                      @Query("week") week: Int?,
                      @Query("year") year: Int?)
            : Observable<List<KidneyLev>>

    @FormUrlEncoded
    @POST("git/{uid}")
    fun postKidneyLev(@Path("uid") uid:String,
                       @Field("cr") cr:Int)
            :Observable<KidneyLev>


}