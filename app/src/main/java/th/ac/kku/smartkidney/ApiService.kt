package th.ac.kku.smartkidney

import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory


object ApiService {
    private val TAG = "--ApiService"

    fun loginApiCall() = Retrofit.Builder()
        .baseUrl(Constant.API_BASE_PATH)
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(ApiWorker.gsonConverter)
        .client(ApiWorker.client)
        .build()
        .create(SmartKidneyAPI::class.java)!!
}