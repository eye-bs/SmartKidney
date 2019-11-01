package th.ac.kku.smartkidney

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.widget.DatePicker
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_add_alarm.*
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_splash.*
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.*

@Suppress("DEPRECATION")
class RegisterActivity : AppCompatActivity(),com.layernet.thaidatetimepicker.date.DatePickerDialog.OnDateSetListener {

    var toggleButtonMale = true
    var toggleButtonFemale = false
    lateinit var birthDate: String
    val mAuth = FirebaseAuth.getInstance().currentUser


    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)


        maleSelectorBt.setOnClickListener {

            if (!toggleButtonMale) {
                maleSelectorBt.setImageDrawable(getDrawable(R.drawable.male_gender_sign))
                femaleSelectorBt.setImageDrawable(getDrawable(R.drawable.female_sign_2))
                toggleButtonMale = true
                toggleButtonFemale = false
            }
        }
        femaleSelectorBt.setOnClickListener {
            if (!toggleButtonFemale) {
                femaleSelectorBt.setImageDrawable(getDrawable(R.drawable.female_sign))
                maleSelectorBt.setImageDrawable(getDrawable(R.drawable.male_gender_sign_2))
                toggleButtonMale = false
                toggleButtonFemale = true
            }
        }

        view_birthdate.setOnClickListener {
            selectBirthDate()
        }
        registerButton.setOnClickListener {
            when {
                TextUtils.isEmpty(birthday_edit_text.text) -> birthday_edit_text.error = getString(R.string.checkFill)
                TextUtils.isEmpty(hospital_edit_text.text) -> hospital_edit_text.error = getString(R.string.checkFill)
                else -> {
                    registerApi(mAuth!!.uid,mAuth.displayName!!,birthDate,selGender(),hospital_edit_text.text.toString())
                }
            }

        }
    }

    @SuppressLint("CheckResult")
    private fun registerApi(email: String, name: String, birthDate: String, gender: String, hospital: String) {
        registerProgressBar.visibility = View.VISIBLE
        registerButton.elevation = 0f
        val observable = ApiService.loginApiCall().register(email,name,birthDate,gender,hospital)
        observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ registerResponse ->
                ApiObject.instant.user = registerResponse
                registerProgressBar.visibility = View.INVISIBLE
                setResult(Activity.RESULT_OK)
                finish()

            }, { error ->
                println(error.message.toString())
            }
            )
    }

    fun selGender():String{
        return if(toggleButtonMale){
            "male"
        }else {
            "female"
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun selectBirthDate() {
        val now = Calendar.getInstance()
        val dpd = com.layernet.thaidatetimepicker.date.DatePickerDialog.newInstance(
            this,
            now.get(Calendar.YEAR),
            now.get(Calendar.MONTH),
            now.get(Calendar.DAY_OF_MONTH)
        )

        dpd.maxDate = now

        dpd.show(fragmentManager, "Datepickerdialog")
//        val c = Calendar.getInstance()
//        val year = c.get(Calendar.YEAR)
//        val month = c.get(Calendar.MONTH)
//        val day = c.get(Calendar.DAY_OF_MONTH)
//
//
//        val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
//
//            // Display Selected date in textbox
//            val getDate = "" + year + "-" + (monthOfYear + 1) + "-" + dayOfMonth + "T09:55:00"
//            val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
//            val toDate = parser.parse(getDate)
//            val formatter = SimpleDateFormat("dd MMMM", Locale.getDefault())
//            val formattedDate = formatter.format(toDate)
//            val birthDateApi = SimpleDateFormat("yyyy-MM-dd")
//            birthDate = birthDateApi.format(toDate)
//
//            var setYear = year
//            if (Locale.getDefault().displayCountry == "ไทย"){
//                setYear += 543
//            }
//            birthday_edit_text.setText("$formattedDate $setYear")
//        }, year, month, day)
//
//
//        dpd.show()
    }
    override fun onDateSet(
        view: com.layernet.thaidatetimepicker.date.DatePickerDialog?,
        year: Int,
        monthOfYear: Int,
        dayOfMonth: Int
    ) {
            val getDate = "" + year + "-" + (monthOfYear + 1) + "-" + dayOfMonth + "T09:55:00"
            val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            val toDate = parser.parse(getDate)
            val formatter = SimpleDateFormat("dd MMMM", Locale.getDefault())
            val formattedDate = formatter.format(toDate)
            val birthDateApi = SimpleDateFormat("yyyy-MM-dd")
            birthDate = birthDateApi.format(toDate)

            val setYear = year + 543

            birthday_edit_text.setText("$formattedDate $setYear")
        }

}
