package th.ac.kku.smartkidney

import android.annotation.TargetApi
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_add_form.*
import kotlinx.android.synthetic.main.custom_dialog.view.*
import kotlinx.android.synthetic.main.edit_weight_dialog.view.*
import org.json.JSONObject


class AddFormActivity : AppCompatActivity() {

    lateinit var getChartName:String
    var buttonBgId: Int? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_form)

        getChartName = intent.getStringExtra("form")
        val jsonObject = ReadJSON(this).getJSONObject(Constant.GRAPH_DETAIL_JSON,getChartName)

        backFormBt.setOnClickListener{
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
        saveAndAnalysisBt.setOnClickListener {
            onSaveButtonClick()
        }
        creatLayout(jsonObject!!)
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun creatLayout(jsonObject: JSONObject){
        val jsonForm = jsonObject.getJSONArray("form")
        val jsonColor = jsonObject.getJSONArray("color")

        if (getChartName != Constant.WATER){
            layoutDetailForWater.visibility = View.GONE
        }
        when (getChartName) {
            Constant.BLOOD_PRESSURE -> {
                saveAndAnalysisBt.background = getDrawable(R.drawable.gradient_pressure_fab)
                buttonBgId = R.drawable.gradient_pressure_fab
            }
            Constant.KIDNEY_FILTRATION_RATE -> {
                saveAndAnalysisBt.background = getDrawable(R.drawable.gradient_kidney_fab)
                buttonBgId = R.drawable.gradient_kidney_fab
            }
            Constant.BLOOD_SUGAR_LEV -> {
                saveAndAnalysisBt.background = getDrawable(R.drawable.gradient_glucose_fab)
                buttonBgId = R.drawable.gradient_glucose_fab
            }
            Constant.WATER -> {
                saveAndAnalysisBt.background = getDrawable(R.drawable.gradient_water_fab)
                buttonBgId = R.drawable.gradient_water_fab
                editWeightBt.setOnClickListener{showDialogSuggest()}

            }
        }

            textViewFormName.text = jsonObject.getString("name")
            textViewFormName.setTextColor(Color.parseColor(jsonColor.getString(0)))
            header_form.setTextColor(Color.parseColor(jsonColor.getString(0)))


            val editTextArr = arrayOf(form_text_input1,form_text_input2)

            if(jsonForm.length() == 1){
                form_text_input2.visibility = View.GONE
            }
            for (i in 0 until jsonForm.length()){
                editTextArr[i].hint = jsonForm.getString(i)
                editTextArr[i].boxStrokeColor = (Color.parseColor(jsonColor.getString(1)))
            }
    }
    private fun onSaveButtonClick(){
        val intent = Intent(this, AnalyzeActivity::class.java)
        intent.putExtra("input1", form_edit_text1.text.toString())
        intent.putExtra("input2", form_edit_text2.text.toString())
        intent.putExtra("name" , getChartName)
        intent.putExtra("buttonBG", buttonBgId.toString())
        startActivityForResult(intent,500)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 500 && resultCode == Activity.RESULT_OK){
            setResult(Activity.RESULT_OK)
            finish()
        }

    }

    private fun showDialogSuggest(){

        val mDialogView = LayoutInflater.from(this).inflate(R.layout.edit_weight_dialog, null)
        val mBuilder = AlertDialog.Builder(this)
                .setView(mDialogView)
                .setTitle("แก้ไขน้ำหนัก")
        val  mAlertDialog = mBuilder.create()

        mDialogView.saveWeightButton.setOnClickListener {
            mAlertDialog.dismiss()
            val weight = mDialogView.textInputEditText.text.toString()
            weightTextView.text = "น้ำหนัก $weight kg"
        }
        mAlertDialog.show()
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }
}

