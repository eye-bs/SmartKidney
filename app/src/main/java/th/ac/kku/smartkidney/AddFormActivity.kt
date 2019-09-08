package th.ac.kku.smartkidney

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.graphics.Color
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_add_form.*
import org.json.JSONObject


class AddFormActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_form)

        val intent = intent
      //  val getChartName = intent.getStringExtra("chartName")
        val getChartName = Constant.BLOOD_PRESSURE
        val jsonObject = ReadJSON(this).getJSONObject(Constant.GRAPH_DETAIL_JSON,getChartName)

        creatLayout(jsonObject!!)

    }

    fun creatLayout(jsonObject: JSONObject){
        val textView = TextView(this)
        val btSave = TextView(this)

        val paramForTextView = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT)
        val paramForBtSave = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT)

        val jsonForm = jsonObject.getJSONArray("form")

        textView.text = jsonObject.getString("name")
        textView.gravity = Gravity.CENTER
        textView.layoutParams = paramForTextView
        contentForm.addView(textView)

        for (i in 0 until jsonForm.length()){
            val textInput = com.google.android.material.textfield.TextInputLayout(this,null,R.style.TextInputLayoutAppearance_register)
            val editText = com.google.android.material.textfield.TextInputEditText(this)

            val paramForInputLayout = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT)

            textInput.isErrorEnabled = true
            paramForInputLayout.setMargins(60,25,50,0)

            editText.hint = jsonForm.getString(i)

            textInput.layoutParams = paramForInputLayout
            editText.layoutParams = paramForInputLayout

            textInput.addView(editText)
            contentForm.addView(textInput)
        }

        paramForBtSave.gravity = Gravity.CENTER
        paramForBtSave.setMargins(0,40,0,0)
        btSave.background = getDrawable(R.drawable.register_bt_bg)
        btSave.setPadding(20,80,20,8)
        btSave.text = "บันทึก + แปลผล"
        btSave.setTextColor(Color.WHITE)
        btSave.textSize = 20F
        btSave.elevation = 5F
        btSave.layoutParams = paramForBtSave

        contentForm.addView(btSave)


    }
}
