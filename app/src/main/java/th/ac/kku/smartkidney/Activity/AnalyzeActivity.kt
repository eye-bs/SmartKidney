package th.ac.kku.smartkidney

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Html
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_analyze.*
import kotlinx.android.synthetic.main.custom_dialog.view.*
import org.json.JSONObject
import java.util.*


@Suppress("DEPRECATION")
class AnalyzeActivity : AppCompatActivity() {

    val readJSON = ReadJSON(this)
    lateinit var analyzeObject: JSONObject
    lateinit var getAnalytics: JSONObject
    var buttonBG: Int? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_analyze)

        val input1 = intent.getStringExtra("input1")
        val input2 = intent.getStringExtra("input2")
        val name = intent.getStringExtra("name")
        buttonBG = intent.getStringExtra("buttonBG").toInt()

        setResultLayout(input1, input2, name)

        backAnalyzeBt.setOnClickListener {
            showDialogRisk(buttonBG!!)

        }
    }

    @SuppressLint("SetTextI18n")
    private fun setResultLayout(input1: String, input2: String, name: String) {
        val calcInput = CalcInput(this)
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR) + 543
        val month = c.get(Calendar.MONTH)
        val monthThai = resources.getStringArray(R.array.month_th)
        val day = c.get(Calendar.DAY_OF_MONTH)
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minite = c.get(Calendar.MINUTE)
        var level: Int? = null

        saveDateTextView.text = "บันทึกวันที่ $day ${monthThai[month]} $year"
        saveTimeTextview.text = "เวลา $hour:$minite น."


        when (name) {
            Constant.BLOOD_PRESSURE -> {
                level = calcInput.calcBloodPressure(input1.toInt(), input2.toInt())!!
                inputTextView1.text = "ความดันตัวบน $input1 mmHg"
                inputTextView2.text = "ความดันตัวล่าง $input2 mmHg"
            }
            Constant.KIDNEY_FILTRATION_RATE -> {
                level = calcInput.calcKidney(input1.toFloat(), 22, "female")
                inputTextView1.text = "ค่า Cr $input1 mg/dL"
                inputTextView2.visibility = View.GONE
            }
            Constant.BLOOD_SUGAR_LEV -> {
                level = calcInput.calcGlucose(input1.toFloat(), input2.toFloat())
                inputTextView1.text = "ค่าระดับน้ำตาล $input1 mg/dL"
                inputTextView2.text = "ค่าระดับน้ำตาลสะสม $input2 HbA1c/dL"
            }
        }

        analyzeObject = readJSON.getJSONObject(Constant.ANALYZE_DETAL_JSON, name)!!
        getAnalytics = analyzeObject.getJSONArray("analytics").getJSONObject(level!!)
        analyzeTextview.text = Html.fromHtml(getAnalytics.getString("result"))
    }

    private fun showDialogRisk(buttonBG: Int) {
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.custom_dialog, null)
        val mBuilder = AlertDialog.Builder(this)
                .setView(mDialogView)
        mDialogView.contentDialog.text = Html.fromHtml(getAnalytics.getString("risk"))
        mDialogView.dialogHeader.setTextColor(Color.parseColor(getAnalytics.getString("color")))
        mDialogView.dialogButton.background = getDrawable(buttonBG)
        val mAlertDialog = mBuilder.create()

        mAlertDialog.setCancelable(false)
        mAlertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        mAlertDialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        if (getAnalytics.getString("suggest") == "") {
            mDialogView.dialogButton.text = "ปิด"
        }

        mDialogView.dialogButton.setOnClickListener {
            mAlertDialog.dismiss()
            if (getAnalytics.getString("suggest") != "") {
                showDialogSuggest(buttonBG)
            } else {
                setResult(Activity.RESULT_OK)
                finish()
            }
        }
        mAlertDialog.show()
    }

    private fun showDialogSuggest(buttonBG: Int) {
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.custom_dialog, null)
        val mBuilder = AlertDialog.Builder(this)
                .setView(mDialogView)
        mDialogView.contentDialog.text = Html.fromHtml(getAnalytics.getString("suggest"))
        mDialogView.dialogHeader.setTextColor(Color.parseColor(getAnalytics.getString("color")))
        mDialogView.dialogButton.background = getDrawable(buttonBG)
        mDialogView.dialogHeader.text = "คำแนะนำ"
        val mAlertDialog = mBuilder.create()

        mAlertDialog.setCancelable(false)
        mAlertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        mAlertDialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        var height = displayMetrics.heightPixels
        height -= height / 5
        val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height)
        mDialogView.findViewById<ScrollView>(R.id.scrollDialog).layoutParams = params
        mDialogView.dialogButton.text = "ปิด"

        mDialogView.dialogButton.setOnClickListener {
            mAlertDialog.dismiss()
            setResult(Activity.RESULT_OK)
            finish()

        }
        mAlertDialog.show()
    }

    override fun onBackPressed() {
        showDialogRisk(buttonBG!!)
    }


}
