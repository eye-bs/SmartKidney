package th.ac.kku.smartkidney

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat.startActivity
import com.google.android.gms.common.api.Api
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

@Suppress("DEPRECATION")
class ApiHandler(val context: Context, val progressBar: RelativeLayout?, val intent: Intent?) {

    val calendar = Calendar.getInstance()
    val date = Constant.formatOfGetbyDate.format(calendar.time)
    val newActivity = context as Activity

    @SuppressLint("CheckResult")
    fun editUserInfo(id: String, email: String?, name: String?, birthDate: String?, gender: String?, hospital: String?, weight: Int?, height: Int?) {
        if (progressBar != null) {
            progressBar.visibility = View.VISIBLE
        }
        val observable = ApiService.loginApiCall().editUserInfo(id, email, name, birthDate, gender, hospital, weight, height)
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ editUserInfo ->

                    getUsers(id)

                }, { error ->
                    getUsers(id)

                   Log.wtf(Constant.TAG , error.message)
                })
    }

    @SuppressLint("CheckResult")
    fun getUsers(id: String) {
        val observable = ApiService.loginApiCall().getUserInfo(id)
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ getUserInfo ->

                    ApiObject.instant.user = getUserInfo

                    if (progressBar != null) { progressBar.visibility = View.INVISIBLE }
                    if (intent != null){
                        intent.putExtra("graphName", Constant.BMI).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                        context.startActivity(intent)
                    }

                }, { error ->
                    if (progressBar != null) {
                        progressBar.visibility = View.INVISIBLE
                        val intent = Intent(context, HomeActivity::class.java)
                        context.startActivity(intent)
                        newActivity.finish()
                    }
                    Log.wtf(Constant.TAG , error.message)
                })
    }

    @SuppressLint("CheckResult")
    fun getWaterPerDay(id: String) {
        val observable = ApiService.loginApiCall().getWaterPerDaye(id, null, null, date)
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ getWaterPerDaye ->
                    var waterIn = 0
                    for (i in getWaterPerDaye.indices) {
                        waterIn += getWaterPerDaye[i].waterIn
                    }
                    ApiObject.instant.waterIn = waterIn

                    if (progressBar != null) {
                        progressBar.visibility = View.GONE
                    }
                    if (intent != null) {
                        context.startActivity(intent)
                        newActivity.finish()
                    }

                }, { error ->
                    println(error.message.toString())
                    if (progressBar != null) {
                        progressBar.visibility = View.GONE
                    }
                    if (intent != null) {
                        context.startActivity(intent)
                        newActivity.finish()
                    }

                }
                )
    }

    @SuppressLint("CheckResult")
    fun postBmi(id: String, bmi: Double) {

        val observable = ApiService.loginApiCall().postBMI(id, bmi)
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ postBMI ->

                    ApiObject.instant.isNewData = true

                    val bmiArr = ApiObject.instant.bmi
                    bmiArr.removeAt(0)
                    bmiArr.add((postBMI.bmi).toFloat())

                }, { error ->
                    ApiObject.instant.notFound404 = true
                    println(error.message.toString())
                })
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @SuppressLint("CheckResult")
    fun comboGetBloodPressure(id: String) {

        val observable = ApiService.loginApiCall().getBloodPressure(id, null, null, null)

        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ getBloodPressure ->


                    //---------------------------------/TEST------------------!!!!!!!!!

                    val hashAll = HashMap<Int, HashMap<Int, BloodPressure>>()
                    val hashWeek = HashMap<Int, BloodPressure>()
                    var stackWeek = HashMap<Int, Int>()

                    for (i in getBloodPressure.indices) {
                        val testDateString = getBloodPressure[i].date
                        val testDate = Constant.formatOfDetail.parse(testDateString)
                        calendar.time = testDate
                        val week = calendar.get(Calendar.WEEK_OF_YEAR)
                        hashWeek[testDate.date] = getBloodPressure[i]
                        stackWeek[week] = week
                    }


                    val keysWeekOfYear = ArrayList<Int>()
                    for (k in stackWeek.keys) {
                        keysWeekOfYear.add(k)
                    }
                    keysWeekOfYear.sort()
                    ApiObject.instant.weekKeysbp = keysWeekOfYear
                    for (i in keysWeekOfYear.indices) {
                        calendar.set(Calendar.WEEK_OF_YEAR, keysWeekOfYear[i])
                        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
                        val hashWeekHelper = HashMap<Int, BloodPressure>()
                        for (j in 1..7) {
                            val pointer = calendar.get(Calendar.DATE)
                            if (hashWeek[pointer] != null) {
                                val data = hashWeek[pointer]
                                hashWeekHelper[pointer] = data!!
                            }
                            calendar.add(Calendar.DATE, 1)
                        }
                        hashAll[keysWeekOfYear[i]] = hashWeekHelper
                    }
                    ApiObject.instant.bloodPressure = hashAll

//---------------------------------/TEST------------------!!!!!!!!!

//                val hashHelpers = HashMap<Int , BloodPressure>()
//                for (i in getBloodPressure.indices){
//                    val dateString = getBloodPressure[i].date
//                    val date = Constant.formatOfDetail.parse(dateString)
//                    hashHelpers[date.date] = getBloodPressure[i]
//                }
//
//                ApiObject.instant.bpHashByWeek[ApiObject.instant.weekQuery!!] = hashHelpers

                    //------Per day------------------
                    val observablePerDay = ApiService.loginApiCall().getBloodPressure(id, null, null, date)
                    observablePerDay.subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({ getBloodPressurePerDaye ->

                                for (i in getBloodPressurePerDaye.indices){
                                    ApiObject.instant.bloodPressurePerDay.add(getBloodPressurePerDaye[i])
                                }
                                comboGetKidneyLev(id)
                            }, { error ->
                                comboGetKidneyLev(id)
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

        val observable = ApiService.loginApiCall().getKidneyLev(id, null, null, null)

        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ getKidneyLev ->
                    //---------------------------------/TEST------------------!!!!!!!!!
                    val hashAll = HashMap<Int, HashMap<Int, KidneyLev>>()
                    val hashWeek = HashMap<Int, KidneyLev>()
                    var stackWeek = HashMap<Int, Int>()

                    for (i in getKidneyLev.indices) {
                        val testDateString = getKidneyLev[i].date
                        val testDate = Constant.formatOfDetail.parse(testDateString)
                        calendar.time = testDate
                        val week = calendar.get(Calendar.WEEK_OF_YEAR)
                        hashWeek[testDate.date] = getKidneyLev[i]
                        stackWeek[week] = week
                    }

                    val keysWeekOfYear = ArrayList<Int>()
                    for (k in stackWeek.keys) {
                        keysWeekOfYear.add(k)
                    }
                    keysWeekOfYear.sort()
                    ApiObject.instant.weekKeysgir = keysWeekOfYear
                    for (i in keysWeekOfYear.indices) {
                        calendar.set(Calendar.WEEK_OF_YEAR, keysWeekOfYear[i])
                        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
                        val hashWeekHelper = HashMap<Int, KidneyLev>()
                        for (j in 1..7) {
                            val pointer = calendar.get(Calendar.DATE)
                            if (hashWeek[pointer] != null) {
                                val data = hashWeek[pointer]
                                hashWeekHelper[pointer] = data!!
                            }
                            calendar.add(Calendar.DATE, 1)
                        }
                        hashAll[keysWeekOfYear[i]] = hashWeekHelper
                    }
                    ApiObject.instant.kidneyLev = hashAll

//---------------------------------/TEST------------------!!!!!!!!!

                    //------Per day------------------
                    val observablePerDay = ApiService.loginApiCall().getKidneyLev(id, null, null, date)
                    observablePerDay.subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({ getKidneyLevPerDaye ->
                                for (i in getKidneyLevPerDaye.indices){
                                    ApiObject.instant.kidneyLevPerDay.add(getKidneyLevPerDaye[i])
                                }

                                comboGetBloodSugar(id)
                            }, { error ->
                                comboGetBloodSugar(id)
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
        val observable = ApiService.loginApiCall().getBloodSugar(id, null, null, null)
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ getBloodSugar ->

                    //---------------------------------/TEST------------------!!!!!!!!!
                    val hashAll = HashMap<Int, HashMap<Int, BloodSugar>>()
                    val hashWeek = HashMap<Int, BloodSugar>()
                    var stackWeek = HashMap<Int, Int>()

                    for (i in getBloodSugar.indices) {
                        val testDateString = getBloodSugar[i].date
                        val testDate = Constant.formatOfDetail.parse(testDateString)
                        calendar.time = testDate
                        val week = calendar.get(Calendar.WEEK_OF_YEAR)
                        hashWeek[testDate.date] = getBloodSugar[i]
                        stackWeek[week] = week
                    }

                    val keysWeekOfYear = ArrayList<Int>()
                    for (k in stackWeek.keys) {
                        keysWeekOfYear.add(k)
                    }
                    keysWeekOfYear.sort()
                    ApiObject.instant.weekKeysbs = keysWeekOfYear
                    for (i in keysWeekOfYear.indices) {
                        calendar.set(Calendar.WEEK_OF_YEAR, keysWeekOfYear[i])
                        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
                        val hashWeekHelper = HashMap<Int, BloodSugar>()
                        for (j in 1..7) {
                            val pointer = calendar.get(Calendar.DATE)
                            if (hashWeek[pointer] != null) {
                                val data = hashWeek[pointer]
                                hashWeekHelper[pointer] = data!!
                            }
                            calendar.add(Calendar.DATE, 1)
                        }
                        hashAll[keysWeekOfYear[i]] = hashWeekHelper
                    }
                    ApiObject.instant.bloodSugar = hashAll

//---------------------------------/TEST------------------!!!!!!!!!
                    //------Per day------------------
                    val observablePerDay = ApiService.loginApiCall().getBloodSugar(id, null, null, date)
                    observablePerDay.subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({ getBloodSugarPerDaye ->
                                for (i in getBloodSugarPerDaye.indices){
                                    ApiObject.instant.bloodSugarPerDay.add(getBloodSugarPerDaye[i])
                                }
                                comboGetWaterPerDay(id)

                            }, { error ->
                                comboGetWaterPerDay(id)
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
        val observable = ApiService.loginApiCall().getWaterPerDaye(id, null, null, date)
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ getWaterPerDaye ->
                    var waterIn = 0
                    for (i in getWaterPerDaye.indices) {
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
        val observable = ApiService.loginApiCall().getBMI(id, ApiObject.instant.startDateQuery, ApiObject.instant.endDateQuery, null)
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ getBmi ->

                    if (getBmi.isNotEmpty()) {
                        val bmiArr = ArrayList<Float>()
                        bmiArr.add(0f)
                        var lastBMI = getBmi.lastIndex - 8
                        for (i in 0 until 7) {
                            if (lastBMI >= 0) {
                                bmiArr.add((getBmi[lastBMI].bmi).toFloat())
                            }
                            lastBMI++
                        }
                        ApiObject.instant.bmi = bmiArr
                    }
                    if (progressBar != null) {
                        progressBar.visibility = View.GONE
                    }
                    if (intent != null) {
                        context.startActivity(intent)
                        newActivity.finish()
                    }

                }, { error ->
                    println(error.message.toString())
                    if (progressBar != null) {
                        progressBar.visibility = View.GONE
                    }
                    if (intent != null) {
                        context.startActivity(intent)
                        newActivity.finish()
                    }
                }
                )
    }

}