package th.ac.kku.smartkidney

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_splash.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import android.content.pm.PackageManager
import android.content.pm.PackageInfo
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.os.Build
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


@Suppress("DEPRECATION")
@SuppressLint("SimpleDateFormat")
class SplashActivity : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null
    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
        ApiObject.instant.startDateQuery = Constant.formatOfGetbyDate.format(calendar.time)
        calendar.add(Calendar.DAY_OF_MONTH , 6)
        ApiObject.instant.endDateQuery = Constant.formatOfGetbyDate.format(calendar.time)
        ApiObject.instant.weekQuery = calendar.get(Calendar.WEEK_OF_YEAR)

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
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    ApiObject.instant.user = loginResponse.users
                    val id = loginResponse.users.id
                    val intent = Intent(this, HomeActivity::class.java)
                    val apiHandler = ApiHandler(this,splashProgressBar,intent)
                    apiHandler.comboGetBloodPressure(id)
                }

            }, { error ->
                println(error.message.toString())
                signOut()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
            )
    }

}
