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
    fun editUserInfo(@Path("uid") uid:String,
                     @Field("email")email: String?,
                       @Field("name")name: String?,
                       @Field("birthDate")birthDate: String?,
                       @Field("gender")gender: String?,
                       @Field("hospital")hospital: String?,
                       @Field("weight")weight: Int?,
                       @Field("height")height: Int?)
            : Observable<User>

    // blood pressure
    @GET("bp/{uid}")
    fun getBloodPressure(@Path("uid") uid:String,
                         @Query("start") start: String?,
                         @Query("end") end: String?,
                         @Query("date") date: String?)
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
                      @Query("start") start: String?,
                      @Query("end") end: String?,
                      @Query("date") date: String?)
            : Observable<List<BloodSugar>>

    @FormUrlEncoded
    @POST("bs/{uid}")
    fun postBloodSugar(@Path("uid") uid:String,
                          @Field("sugarLevel") systolic:Int,
                          @Field("hba1c") diastolic:Int)
            :Observable<BloodSugar>

    // glomerular infiltration
    @GET("gir/{uid}")
    fun getKidneyLev(@Path("uid") uid:String,
                     @Query("start") start: String?,
                     @Query("end") end: String?,
                     @Query("date") date: String?)
            : Observable<List<KidneyLev>>

    @FormUrlEncoded
    @POST("gir/{uid}")
    fun postKidneyLev(@Path("uid") uid:String,
                       @Field("cr") cr:Double,
                        @Field("egfr") egfr:Double)
            :Observable<KidneyLev>

    // water
    @GET("water/{uid}")
    fun getWaterPerDaye(@Path("uid") uid:String,
                     @Query("start") start: String?,
                     @Query("end") end: String?,
                        @Query("date") date: String?)
            : Observable<List<WaterPerDay>>

    @FormUrlEncoded
    @POST("water/{uid}")
    fun postWaterPerDay(@Path("uid") uid:String,
                      @Field("waterIn") waterIn:Int)
            :Observable<WaterPerDay>

    // bmi
    @GET("bmi/{uid}")
    fun getBMI(@Path("uid") uid:String,
                        @Query("start") start: String?,
                        @Query("end") end: String?,
               @Query("date") date: String?)
            : Observable<List<BMI>>

    @FormUrlEncoded
    @POST("bmi/{uid}")
    fun postBMI(@Path("uid") uid:String,
                        @Field("bmi") bmi:Double)
            :Observable<BMI>


}