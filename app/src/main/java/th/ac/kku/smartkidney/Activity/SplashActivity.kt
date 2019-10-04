package th.ac.kku.smartkidney

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import com.facebook.CallbackManager
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_splash.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class SplashActivity : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null
    private val df = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        if (ConnectivityHelper.isConnectedToNetwork(this)) {

            mAuth = FirebaseAuth.getInstance()

            if (mAuth!!.currentUser != null) {

                loginApiCall(mAuth!!.currentUser!!.email!!)

            } else {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }

        } else {

            val dialog = Dialog(this)
            dialog.setContentView(R.layout.connect_falied_dialog)
            dialog.setCancelable(false)

            val button1 = dialog.findViewById<TextView>(R.id.button_dialog)
            button1.setOnClickListener {
                dialog.cancel()
                val intent = Intent(this,SplashActivity::class.java)
                startActivity(intent)
                finish()
            }
            dialog.show()
        }
    }

    private fun signOut() {
        AuthUI.getInstance()
            .signOut(this)
            .addOnCompleteListener { }
    }
    @SuppressLint("CheckResult")
    private fun loginApiCall(email: String) {
        val observable = ApiService.loginApiCall().login(email)
        observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ loginResponse ->
                ApiObject.instant.firstLogin = loginResponse.firstLogin

                if (ApiObject.instant.firstLogin!!) {
                    signOut()
                } else {
                    ApiObject.instant.user = loginResponse.users
                    val id = loginResponse.users.id
                    getBloodPressure(id,null,null)
                }

            }, { error ->
                println(error.message.toString())
            }
            )
    }

    @SuppressLint("CheckResult")
    private fun getBloodPressure(id: String,week:Int?,year:Int?) {

        val observable = ApiService.loginApiCall().getBloodPressure(id,week,year)

        observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ getBloodPressure ->
                Log.wtf(Constant.TAG , "before size" + ApiObject.instant.bpHashByWeek.size)
                ApiObject.instant.bloodPressure = getBloodPressure

                val bpArrayList = ArrayList<BloodPressure>()
                val firstDateString = getBloodPressure[0].date
                val firstDate = df.parse(firstDateString)
                calendar.time = firstDate
                var  firstWeek = calendar.get(Calendar.WEEK_OF_YEAR)
                var stackDate = calendar.get(Calendar.DATE)

                for (i in getBloodPressure.indices){
                    val dateString = getBloodPressure[i].date
                    val date = df.parse(dateString)
                    calendar.time = date
                    val week = calendar.get(Calendar.WEEK_OF_YEAR)
                    val dateInMonth = calendar.get(Calendar.DATE)
                    if (dateInMonth > stackDate || i == getBloodPressure.size-1){
                        if(i ==  getBloodPressure.size-1){
                            bpArrayList.add(getBloodPressure[i])
                        }else{
                            bpArrayList.add(getBloodPressure[i-1])
                        }
                        stackDate = dateInMonth
                    }
                    if (week > firstWeek || i == getBloodPressure.size-1){
                        ApiObject.instant.bpHashByWeek[firstWeek] = bpArrayList
                        firstWeek = week
                    }
                }

                getKidneyLev(id,null,null)
                Log.wtf(Constant.TAG , "after size" + ApiObject.instant.bpHashByWeek.size)

            }, { error ->
                getKidneyLev(id,null,null)
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
                getBloodSugar(id,null,null)
            }, { error ->
                getBloodSugar(id,null,null)
                println(error.message.toString())
            }
            )
    }
    @SuppressLint("CheckResult")
    private fun getBloodSugar(id: String,week:Int?,year:Int?) {
        val intent = Intent(this, HomeActivity::class.java)
        val observable = ApiService.loginApiCall().getBloodSugar(id,week,year)
        observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ getBloodSugar ->
                splashProgressBar.visibility = View.GONE
                ApiObject.instant.bloodSugar = getBloodSugar

                startActivity(intent)
                finish()

            }, { error ->
                splashProgressBar.visibility = View.GONE
                startActivity(intent)
                finish()

            }
            )
    }
}
