package th.ac.kku.smartkidney

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.media.MediaPlayer
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
import com.google.zxing.client.result.VINResultParser
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
    var levelHabc1:Int? = null
    private lateinit var alnalytics: JSONObject
    private val soundArr = arrayOf(R.raw.lev1,R.raw.lev2,R.raw.lev3,R.raw.lev4,R.raw.lev5,R.raw.lev4,R.raw.lev4,R.raw.lev4)


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
                showDialogSuggest()
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
                    level = calcInput.calcBloodPressure(input1.toFloat(), input2.toFloat())!!
                }
                Constant.KIDNEY_FILTRATION_RATE -> {
                    level = ApiObject.instant.kidneyRange
                }
                Constant.BLOOD_SUGAR_LEV -> {
                    level = calcInput.calcGlucose(input1.toFloat())
                    if (level == 3){
                        imgBsHigh.visibility = View.VISIBLE
                    }
                    else if(level == 4){
                        imgBsVeryHigh.visibility = View.VISIBLE
                    }
                    if(input2 != ""){
                        levelHabc1 = calcInput.calchba1c(input1.toFloat(),input2.toFloat())
                    }
                }
            }
            soundHandle(level!!,name)
            analyzeObject = readJSON.getJSONObject(Constant.ANALYZE_DETAL_JSON, name)!!
            getAnalytics = analyzeObject.getJSONArray("analytics").getJSONObject(level)
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
                    resultButton.visibility = View.VISIBLE
                }else{
                    resultButton.text = "ปิด"
                }
            }

            if(levelHabc1 != null){
                val jSONhbalc = readJSON.getJSONObject(Constant.ANALYZE_DETAL_JSON, "HbA1c")!!
                alnalytics = jSONhbalc.getJSONArray("analytics").getJSONObject(levelHabc1!!)
                val hbA1cResult =  Html.fromHtml(alnalytics.getString("result"))
                analyzeTextview.append(hbA1cResult)
            }

        }else {
            setResult(Activity.RESULT_OK)
            finish()
        }
    }

    private fun showDialogHbalc() {
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.custom_dialog, null)
        val mBuilder = AlertDialog.Builder(this)
                .setView(mDialogView)

        mDialogView.dialogHeader.setTextColor(Color.parseColor(getAnalytics.getString("color")))
        mDialogView.dialogHeader.text = "คำแนะนำ ระดับน้ำตาลสะสม"

        val resources = this.resources
        val img =  alnalytics.getString("img")
        val resourceId = resources.getIdentifier(img, "drawable", this.packageName)
        val drawable = resources.getDrawable(resourceId)

        mDialogView.imageDialog.setImageDrawable(drawable)
        val mAlertDialog = mBuilder.create()

        mAlertDialog.setCancelable(false)
        mAlertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        mAlertDialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        var height = displayMetrics.heightPixels
        height -= height / 5

        mDialogView.dialogButton.setOnClickListener {
            mAlertDialog.dismiss()
            val intent = Intent()
            intent.putExtra("graphName",name)
            setResult(Activity.RESULT_OK , intent)
            finish()
        }
        mAlertDialog.show()
    }

    private fun showDialogSuggest() {
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

        mDialogView.dialogButton.setOnClickListener {
            mAlertDialog.dismiss()
            if (levelHabc1 != null){
                showDialogHbalc()
            }
            else{
                val intent = Intent()
                intent.putExtra("graphName",name)
                setResult(Activity.RESULT_OK , intent)
                finish()
            }


        }
        mAlertDialog.show()
    }

    fun soundHandle(lev:Int,name:String){
        var arr:Array<Int>? = null
        when (name) {
            Constant.BLOOD_PRESSURE -> arr = arrayOf(1,0,2,3,4,5)
            Constant.KIDNEY_FILTRATION_RATE -> arr = arrayOf(0,1,2,3,4,5,6)
            Constant.BLOOD_SUGAR_LEV -> arr = arrayOf(2,1,0,3,4,5,6)
        }
        if (arr != null){
            val happy = MediaPlayer.create(this,soundArr[arr[lev]])
            happy.start()
        }

    }

    override fun onBackPressed() {
        showDialogSuggest()
    }

}
