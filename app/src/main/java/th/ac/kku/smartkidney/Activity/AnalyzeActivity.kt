package th.ac.kku.smartkidney

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
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
import com.inthecheesefactory.thecheeselibrary.widget.AdjustableImageView
import kotlinx.android.synthetic.main.activity_analyze.*
import kotlinx.android.synthetic.main.custom_dialog.view.*
import org.json.JSONObject
import java.util.*


@Suppress("DEPRECATION", "NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class AnalyzeActivity : AppCompatActivity() {

    private val readJSON = ReadJSON(this)
    private lateinit var analyzeObject: JSONObject
    private lateinit var getAnalytics: JSONObject
    private var buttonBG: Int? = null
    var name: String = ""
    private val imageArr = arrayListOf<Drawable>()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_analyze)

        val input1 = intent.getStringExtra("input1")
        val input2 = intent.getStringExtra("input2")
        name = intent.getStringExtra("graphName")
        buttonBG = intent.getStringExtra("buttonBG").toInt()

        setResultLayout(input1, input2, name)

        resultButton.setOnClickListener {
            if (getAnalytics.getString("suggest") != "") {
                showDialogSuggest(buttonBG!!)
            } else {
                val intent = Intent()
                intent.putExtra("graphName",name)
                setResult(Activity.RESULT_OK , intent)
                finish()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setResultLayout(input1: String, input2: String, name: String) {
        val calcInput = CalcInput(this)
        var level: Int? = null

        if(name != Constant.WATER){
            when (name) {
                Constant.BLOOD_PRESSURE -> {
                    level = calcInput.calcBloodPressure(input1.toInt(), input2.toInt())!!
                }
                Constant.KIDNEY_FILTRATION_RATE -> {
                    level = ApiObject.instant.kidneyRange
                }
                Constant.BLOOD_SUGAR_LEV -> {
                    level = calcInput.calcGlucose(input1.toFloat(), input2.toFloat())
                }
            }
            analyzeObject = readJSON.getJSONObject(Constant.ANALYZE_DETAL_JSON, name)!!
            getAnalytics = analyzeObject.getJSONArray("analytics").getJSONObject(level!!)
            val result =  Html.fromHtml(getAnalytics.getString("result"))
            resultHeader.setTextColor(Color.parseColor(getAnalytics.getString("color")))
            analyzeTextview.text = result
            headerAnalyze.setTextColor(Color.parseColor(getAnalytics.getString("color")))
            resultButton.background = getDrawable(buttonBG!!)

            val img =  getAnalytics.getJSONArray("img")
            for (i in 0 until img.length()){
                if (img.getString(i) != ""){
                    val resources = this.resources
                    val resourceId = resources.getIdentifier(img.getString(i), "drawable", this.packageName)
                    val drawable = resources.getDrawable(resourceId)
                    imageArr.add(drawable)
                }
            }

        }else {
            setResult(Activity.RESULT_OK)
            finish()
        }
    }

    private fun showDialogSuggest(buttonBG: Int) {
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.custom_dialog, null)
        val mBuilder = AlertDialog.Builder(this)
                .setView(mDialogView)
   //     mDialogView.contentDialog.text = Html.fromHtml(getAnalytics.getString("suggest"))
        mDialogView.dialogHeader.setTextColor(Color.parseColor(getAnalytics.getString("color")))
       // mDialogView.dialogButton.background = getDrawable(buttonBG)
        mDialogView.dialogHeader.text = "คำแนะนำ"
        mDialogView.imageDialog.setImageDrawable(imageArr[0])
        val mAlertDialog = mBuilder.create()

        mAlertDialog.setCancelable(false)
        mAlertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        mAlertDialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        var height = displayMetrics.heightPixels
        height -= height / 5
        val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height)
        //mDialogView.findViewById<ScrollView>(R.id.scrollDialog).layoutParams = params
       // mDialogView.dialogButton.text = "ปิด"

        mDialogView.dialogButton.setOnClickListener {
            mAlertDialog.dismiss()
            val intent = Intent()
            intent.putExtra("graphName",name)
            setResult(Activity.RESULT_OK , intent)
            finish()

        }
        mAlertDialog.show()
    }

    override fun onBackPressed() {
        showDialogSuggest(buttonBG!!)
    }

}
