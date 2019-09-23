package th.ac.kku.smartkidney

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_register.*
import java.text.SimpleDateFormat
import java.util.*

class RegisterActivity : AppCompatActivity() {

    var toggleButtonMale = false
    var toggleButtonFemale = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        setMarginBg()

        maleSelectorBt.setOnClickListener(View.OnClickListener {
            Log.wtf(Constant.TAG, "get:: ${maleSelectorBt.drawable} \n img:: ${getDrawable(R.drawable.male_gender_sign_2)}")
            if (toggleButtonMale == false) {
                maleSelectorBt.setImageDrawable(getDrawable(R.drawable.male_gender_sign))
                femaleSelectorBt.setImageDrawable(getDrawable(R.drawable.female_sign_2))
                toggleButtonMale = true
                toggleButtonFemale = false
            }
        })
        femaleSelectorBt.setOnClickListener(View.OnClickListener {
            if (toggleButtonFemale == false) {
                femaleSelectorBt.setImageDrawable(getDrawable(R.drawable.female_sign))
                maleSelectorBt.setImageDrawable(getDrawable(R.drawable.male_gender_sign_2))
                toggleButtonMale = false
                toggleButtonFemale = true
            }
        })

        view_birthdate.setOnClickListener(View.OnClickListener {
            selectBirthDate()
        })

        registerButton.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, AgreementActivity::class.java)
            startActivity(intent)
        })
    }

    fun setMarginBg() {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        //obj size 934 * 422
        var objWidth = 934
        var width = displayMetrics.widthPixels
        if (width > objWidth) {
            width -= objWidth
        } else width = objWidth - width

        val param = objRegister.layoutParams as RelativeLayout.LayoutParams
        param.setMargins(0, 0, 0, width * -1)
        objRegister.layoutParams = param
    }

    @SuppressLint("SimpleDateFormat")
    fun selectBirthDate() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)


        val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

            // Display Selected date in textbox
            val getDate = "" + year + "-" + (monthOfYear + 1) + "-" + dayOfMonth + "T09:55:00"
            val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            val formatter = SimpleDateFormat("dd.MM.yyyy")
            val formattedDate = formatter.format(parser.parse(getDate))

            birthday_edit_text.setText(formattedDate)
        }, year, month, day)

        dpd.show()
    }

}
