package th.ac.kku.smartkidney

import android.annotation.SuppressLint
import android.util.Log
import android.view.View

import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class ApiHandler {

    @SuppressLint("CheckResult")
    private fun getBloodPressure(id: String,week:Int?,year:Int?) {

        val observable = ApiService.loginApiCall().getBloodPressure(id,week,year)
        observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ getBloodPressure ->
                Log.wtf(Constant.TAG , getBloodPressure.toString())
                ApiObject.instant.bloodPressure = getBloodPressure
            }, { error ->
                Log.wtf(Constant.TAG,error.message.toString())
            }
            )
    }
    @SuppressLint("CheckResult")
    private fun getKidneyLev(id: String,week:Int?,year:Int?) {

        val observable = ApiService.loginApiCall().getKidneyLev(id,week,year)

        observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ getKidneyLev ->
                ApiObject.instant.kidneyLev = getKidneyLev
            }, { error ->
                println(error.message.toString())
            }
            )
    }
    @SuppressLint("CheckResult")
    private fun getBloodSugar(id: String,week:Int?,year:Int?) {
        val observable = ApiService.loginApiCall().getBloodSugar(id,week,year)
        observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ getBloodSugar ->
                ApiObject.instant.bloodSugar = getBloodSugar
                ApiObject.instant.notFound404 = false
            }, { error ->
                ApiObject.instant.notFound404 = true
            }
            )
    }
}