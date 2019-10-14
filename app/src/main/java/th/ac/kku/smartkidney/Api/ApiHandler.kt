package th.ac.kku.smartkidney

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.github.mikephil.charting.charts.LineChart
import com.google.android.gms.common.api.Api
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_splash.*
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

@Suppress("DEPRECATION")
class ApiHandler(val context: Context, val progressBar: RelativeLayout?, val intent: Intent?){

    val calendar = Calendar.getInstance()
    val date = Constant.formatOfGetbyDate.format(calendar.time)
    val newActivity = context as Activity

    @SuppressLint("CheckResult")
    fun editUserInfo(id:String,email:String?,name:String?,birthDate:String?,gender:String?,hospital:String?,weight:Int?,height:Int?){
        progressBar!!.visibility = View.VISIBLE
        val observable = ApiService.loginApiCall().editUserInfo(id,email,name,birthDate, gender, hospital, weight, height)
        observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ editUserInfo ->
                progressBar!!.visibility = View.INVISIBLE
                getUsers(id)

            }, { error ->
                progressBar!!.visibility = View.INVISIBLE
                println(error.message.toString())

            }
            )
    }

    @SuppressLint("CheckResult")
    fun getUsers(id: String ) {
        val observable = ApiService.loginApiCall().getUserInfo(id)
        observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ getUserInfo ->
                ApiObject.instant.user = getUserInfo
            }, { error ->
                println(error.message.toString())
            })
    }

    @SuppressLint("CheckResult")
    fun getBloodPressure(id: String ) {
        val observable = ApiService.loginApiCall().getBloodPressure(id,ApiObject.instant.startDateQuery,ApiObject.instant.endDateQuery,null)

        observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ getBloodPressure ->
                ApiObject.instant.bloodPressure = getBloodPressure

                val hashHelpers = HashMap<Int , BloodPressure>()
                for (i in getBloodPressure.indices){
                    val dateString = getBloodPressure[i].date
                    val date = Constant.formatOfDetail.parse(dateString)
                    hashHelpers[date.date] = getBloodPressure[i]
                }
                ApiObject.instant.bpHashByWeek[ApiObject.instant.weekQuery!!] = hashHelpers

                //------Per day------------------
                val observablePerDay = ApiService.loginApiCall().getBloodPressure(id,null,null,date)
                observablePerDay.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ getBloodPressurePerDaye ->
                        ApiObject.instant.bloodPressurePerDay = getBloodPressurePerDaye

                        if (progressBar != null){
                            progressBar.visibility = View.GONE
                        }
                        if (intent != null){
                            context.startActivity(intent)
                            newActivity.finish()
                        }

                    },{ error ->
                        println(error.message.toString())
                    })

            }, { error ->
                if (progressBar != null){
                    progressBar.visibility = View.GONE
                }
                if (intent != null){
                    context.startActivity(intent)
                    newActivity.finish()
                }
                println(error.message.toString())
            })
    }
    @SuppressLint("CheckResult")
    fun getKidneyLev(id: String) {

        val observable = ApiService.loginApiCall().getKidneyLev(id,ApiObject.instant.startDateQuery,ApiObject.instant.endDateQuery,null)

        observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ getKidneyLev ->
                ApiObject.instant.kidneyLev = getKidneyLev

                val hashHelpers = HashMap<Int , KidneyLev>()
                for (i in getKidneyLev.indices){
                    val dateString = getKidneyLev[i].date
                    val date = Constant.formatOfDetail.parse(dateString)
                    hashHelpers[date.date] = getKidneyLev[i]
                }

                ApiObject.instant.girHashByWeek[ApiObject.instant.weekQuery!!] = hashHelpers

                //------Per day------------------
                val observablePerDay = ApiService.loginApiCall().getKidneyLev(id,null,null,date)
                observablePerDay.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ getKidneyLevPerDaye ->
                        ApiObject.instant.kidneyLevPerDay = getKidneyLevPerDaye

                        if (progressBar != null){
                            progressBar.visibility = View.GONE
                        }
                        if (intent != null){
                            context.startActivity(intent)
                            newActivity.finish()
                        }
                    },{ error ->
                        println(error.message.toString())
                    })
                //---------------------------------------


            }, { error ->
                if (progressBar != null){
                    progressBar.visibility = View.GONE
                }
                if (intent != null){
                    context.startActivity(intent)
                    newActivity.finish()
                }
                println(error.message.toString())
            }
            )
    }
    @SuppressLint("CheckResult")
    fun getBloodSugar(id: String) {
        val observable = ApiService.loginApiCall().getBloodSugar(id,ApiObject.instant.startDateQuery,ApiObject.instant.endDateQuery,null)
        observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ getBloodSugar ->
                ApiObject.instant.bloodSugar = getBloodSugar

                val hashHelpers = HashMap<Int , BloodSugar>()
                for (i in getBloodSugar.indices){
                    val dateString = getBloodSugar[i].date
                    val date = Constant.formatOfDetail.parse(dateString)
                    hashHelpers[date.date] = getBloodSugar[i]
                }
                ApiObject.instant.bsHashByWeek[ApiObject.instant.weekQuery!!] = hashHelpers

                //------Per day------------------
                val observablePerDay = ApiService.loginApiCall().getBloodSugar(id,null,null,date)
                observablePerDay.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ getBloodSugarPerDaye ->
                        ApiObject.instant.bloodSugarPerDay = getBloodSugarPerDaye

                        if (progressBar != null){
                            progressBar.visibility = View.GONE
                        }
                        if (intent != null){
                            context.startActivity(intent)
                            newActivity.finish()
                        }
                    },{ error ->
                        println(error.message.toString())
                    })
                //---------------------------------------

            }, { error ->
                println(error.message.toString())
                if (progressBar != null){
                    progressBar.visibility = View.GONE
                }
                if (intent != null){
                    context.startActivity(intent)
                    newActivity.finish()
                }

            }
            )
    }

    @SuppressLint("CheckResult")
    fun getWaterPerDay(id: String) {
        val observable = ApiService.loginApiCall().getWaterPerDaye(id,null,null,date)
        observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ getWaterPerDaye ->
                var waterIn = 0
                for (i in getWaterPerDaye.indices){
                    waterIn += getWaterPerDaye[i].waterIn
                }
                ApiObject.instant.waterIn = waterIn

                if (progressBar != null){
                    progressBar.visibility = View.GONE
                }
                if (intent != null){
                    context.startActivity(intent)
                    newActivity.finish()
                }

            }, { error ->
                println(error.message.toString())
                if (progressBar != null){
                    progressBar.visibility = View.GONE
                }
                if (intent != null){
                    context.startActivity(intent)
                    newActivity.finish()
                }

            }
            )
    }

    @SuppressLint("CheckResult")
    private fun getBmi(id: String) {
        val observable = ApiService.loginApiCall().getBMI(id,ApiObject.instant.startDateQuery,ApiObject.instant.endDateQuery,null)
        observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ getBmi ->

                if(getBmi.isNotEmpty()){
                    val bmiArr = ArrayList<Float>()
                    bmiArr.add(0f)
                    var lastBMI = getBmi.lastIndex
                    for (i in 0 until 7){
                        bmiArr.add((getBmi[lastBMI].bmi).toFloat())
                        if (lastBMI == 0){
                            bmiArr.add(0f)
                            ApiObject.instant.bmi = bmiArr
                            break
                        }
                        lastBMI--
                    }

                    val intent = Intent(context , HealthFormActivity::class.java)
                intent.putExtra("graphName" , Constant.BMI)
                context.startActivity(intent)

                }

                if (progressBar != null){
                    progressBar.visibility = View.GONE
                }


            }, { error ->
                println(error.message.toString())
                if (progressBar != null){
                    progressBar.visibility = View.GONE
                }
            }
            )
    }

    @SuppressLint("CheckResult")
    fun postBmi(id: String , bmi:Double){

        val observable = ApiService.loginApiCall().postBMI(id, bmi)
        observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ postBMI ->

                ApiObject.instant.isNewData = true
                getBmi(id)


            }, { error ->
                ApiObject.instant.notFound404 = true
                println(error.message.toString())
            })
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @SuppressLint("CheckResult")
    fun comboGetBloodPressure(id: String ) {

        val observable = ApiService.loginApiCall().getBloodPressure(id,ApiObject.instant.startDateQuery,ApiObject.instant.endDateQuery,null)

        observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ getBloodPressure ->
                ApiObject.instant.bloodPressure = getBloodPressure

                val hashHelpers = HashMap<Int , BloodPressure>()
                for (i in getBloodPressure.indices){
                    val dateString = getBloodPressure[i].date
                    val date = Constant.formatOfDetail.parse(dateString)
                    hashHelpers[date.date] = getBloodPressure[i]
                }
                ApiObject.instant.bpHashByWeek[ApiObject.instant.weekQuery!!] = hashHelpers

                //------Per day------------------
                val observablePerDay = ApiService.loginApiCall().getBloodPressure(id,null,null,date)
                observablePerDay.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ getBloodPressurePerDaye ->
                        ApiObject.instant.bloodPressurePerDay = getBloodPressurePerDaye
                        comboGetKidneyLev(id)
                    },{ error ->
                        println(error.message.toString())
                    })
                //---------------------------------------


            }, { error ->
                comboGetKidneyLev(id)
                println(error.message.toString())
            })
    }
    @SuppressLint("CheckResult")
    private fun comboGetKidneyLev(id: String) {

        val observable = ApiService.loginApiCall().getKidneyLev(id,ApiObject.instant.startDateQuery,ApiObject.instant.endDateQuery,null)

        observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ getKidneyLev ->
                ApiObject.instant.kidneyLev = getKidneyLev

                val hashHelpers = HashMap<Int , KidneyLev>()
                for (i in getKidneyLev.indices){
                    val dateString = getKidneyLev[i].date
                    val date = Constant.formatOfDetail.parse(dateString)
                    hashHelpers[date.date] = getKidneyLev[i]
                }

                ApiObject.instant.girHashByWeek[ApiObject.instant.weekQuery!!] = hashHelpers

                //------Per day------------------
                val observablePerDay = ApiService.loginApiCall().getKidneyLev(id,null,null,date)
                observablePerDay.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ getKidneyLevPerDaye ->
                        ApiObject.instant.kidneyLevPerDay = getKidneyLevPerDaye
                        comboGetBloodSugar(id)
                    },{ error ->
                        println(error.message.toString())
                    })
                //---------------------------------------


            }, { error ->
                comboGetBloodSugar(id)
                println(error.message.toString())
            }
            )
    }
    @SuppressLint("CheckResult")
    private fun comboGetBloodSugar(id: String) {
        val observable = ApiService.loginApiCall().getBloodSugar(id,ApiObject.instant.startDateQuery,ApiObject.instant.endDateQuery,null)
        observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ getBloodSugar ->
                ApiObject.instant.bloodSugar = getBloodSugar

                val hashHelpers = HashMap<Int , BloodSugar>()
                for (i in getBloodSugar.indices){
                    val dateString = getBloodSugar[i].date
                    val date = Constant.formatOfDetail.parse(dateString)
                    hashHelpers[date.date] = getBloodSugar[i]
                }
                ApiObject.instant.bsHashByWeek[ApiObject.instant.weekQuery!!] = hashHelpers

                //------Per day------------------
                val observablePerDay = ApiService.loginApiCall().getBloodSugar(id,null,null,date)
                observablePerDay.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ getBloodSugarPerDaye ->
                        ApiObject.instant.bloodSugarPerDay = getBloodSugarPerDaye
                        comboGetWaterPerDay(id)

                    },{ error ->
                        println(error.message.toString())
                    })
                //---------------------------------------

            }, { error ->
                comboGetWaterPerDay(id)
                println(error.message.toString())

            }
            )
    }

    @SuppressLint("CheckResult")
    private fun comboGetWaterPerDay(id: String) {
        val observable = ApiService.loginApiCall().getWaterPerDaye(id,null,null,date)
        observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ getWaterPerDaye ->
                var waterIn = 0
                for (i in getWaterPerDaye.indices){
                    waterIn += getWaterPerDaye[i].waterIn
                }

                ApiObject.instant.waterIn = waterIn
                comboGetBmi(id)

            }, { error ->
                comboGetBmi(id)
                println(error.message.toString())

            }
            )
    }

    @SuppressLint("CheckResult")
    private fun comboGetBmi(id: String) {
        val observable = ApiService.loginApiCall().getBMI(id,ApiObject.instant.startDateQuery,ApiObject.instant.endDateQuery,null)
        observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ getBmi ->

                if(getBmi.isNotEmpty()){
                    val bmiArr = ArrayList<Float>()
                    var lastBMI = getBmi.lastIndex
                    for (i in 0 until 7){
                        bmiArr.add((getBmi[lastBMI].bmi).toFloat())
                        if (lastBMI == 0){
                            ApiObject.instant.bmi = bmiArr
                            break
                        }
                        lastBMI--
                    }
                }
                if (progressBar != null){
                    progressBar.visibility = View.GONE
                }
                if(intent != null){
                    context.startActivity(intent)
                    newActivity.finish()
                }

            }, { error ->
                println(error.message.toString())
                if (progressBar != null){
                    progressBar.visibility = View.GONE
                }
                if (intent != null){
                    context.startActivity(intent)
                    newActivity.finish()
                }
            }
            )
    }

}