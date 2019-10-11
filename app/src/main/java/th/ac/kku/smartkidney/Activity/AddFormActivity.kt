package th.ac.kku.smartkidney

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.ImageViewCompat
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_add_form.*
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.choose_bottle_dialog.view.*
import kotlinx.android.synthetic.main.edit_weight_dialog.view.*
import org.json.JSONObject


@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS", "DEPRECATION")
class AddFormActivity : AppCompatActivity() {

    lateinit var getChartName: String
    var buttonBgId: Int? = null
    var egfr:Double? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_form)

        ApiObject.instant.isNewData = false
        ApiObject.instant.notFound404 = false

        getChartName = intent.getStringExtra("form")
        val jsonObject = ReadJSON(this).getJSONObject(Constant.GRAPH_DETAIL_JSON, getChartName)

        backFormBt.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
        saveAndAnalysisBt.setOnClickListener {
            when {
                TextUtils.isEmpty(form_edit_text1.text) -> form_edit_text1.error =
                    getString(R.string.checkFill)
                TextUtils.isEmpty(form_edit_text2.text) -> {
                    if(getChartName == Constant.BLOOD_PRESSURE){
                        form_edit_text2.error = getString(R.string.checkFill)
                    }else{
                        onSaveButtonClick()
                    }
                }
                else -> onSaveButtonClick()
            }

        }
        creatLayout(jsonObject!!)
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun creatLayout(jsonObject: JSONObject) {
        val jsonForm = jsonObject.getJSONArray("form")
        val jsonColor = jsonObject.getJSONArray("color")

        if (getChartName != Constant.WATER) {
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
                weightTextView.text = "น้ำหนัก ${ApiObject.instant.user!!.weight} kg"
                showDialogChooseCup()
                editWeightBt.setOnClickListener { showDialogChangeWeight() }

            }
        }

        textViewFormName.text = jsonObject.getString("name")
        textViewFormName.setTextColor(Color.parseColor(jsonColor.getString(0)))
        header_form.setTextColor(Color.parseColor(jsonColor.getString(0)))


        val editTextArr = arrayOf(form_text_input1, form_text_input2)

        if (jsonForm.length() == 1) {
            form_text_input2.visibility = View.GONE
        }
        for (i in 0 until jsonForm.length()) {
            editTextArr[i].hint = jsonForm.getString(i)
            editTextArr[i].boxStrokeColor = (Color.parseColor(jsonColor.getString(1)))
        }
    }

    private fun onSaveButtonClick() {
        val param1 =  form_edit_text1.text.toString()
        val param2 =  form_edit_text2.text.toString()
        val calcInput = CalcInput(this)
        egfr = calcInput.calcKidney(param1.toFloat() , ApiObject.instant.age!! , ApiObject.instant.user!!.gender)

        if (ConnectivityHelper.isConnectedToNetwork(this)) {
            onPostApi(getChartName , ApiObject.instant.user!!.id,param1 , param2)
        } else {
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.connect_falied_dialog)
            dialog.setCancelable(false)

            val button1 = dialog.findViewById<TextView>(R.id.button_dialog)
            button1.setOnClickListener {
                dialog.cancel()
            }
            dialog.show()
        }

        val intent = Intent(this, AnalyzeActivity::class.java)
        intent.putExtra("input1", param1)
        intent.putExtra("input2", param2)
        intent.putExtra("graphName", getChartName)
        intent.putExtra("buttonBG", buttonBgId.toString())
        startActivityForResult(intent, 500)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 500 && resultCode == Activity.RESULT_OK && data != null) {
            setResult(Activity.RESULT_OK , data)
            finish()
        }

    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun showDialogChooseCup() {
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.choose_bottle_dialog, null)
        val mBuilder = AlertDialog.Builder(this)
            .setView(mDialogView)

        var stackCup: ImageView? = null
        var cupSize: Int? = null

        val layoutArr = arrayOf(
            mDialogView.cup100Layout,
            mDialogView.cup200Layout,
            mDialogView.cup300Layout,
            mDialogView.cup400Layout,
            mDialogView.cup500Layout,
            mDialogView.cup600Layout
        )
        val imageArr = arrayOf(
            mDialogView.cup100Image,
            mDialogView.cup200Image,
            mDialogView.cup300Image,
            mDialogView.cup400Image,
            mDialogView.cup500Image,
            mDialogView.cup600Image
        )
        for (i in layoutArr.indices) {
            layoutArr[i].setOnClickListener {
                ImageViewCompat.setImageTintList(
                    imageArr[i],
                    ColorStateList.valueOf(getColor(R.color.cornflowerBlue))
                )
                cupSize = i + 1
                if (stackCup != null) {
                    ImageViewCompat.setImageTintList(
                        stackCup!!,
                        ColorStateList.valueOf(getColor(R.color.black))
                    )
                }
                if (stackCup != imageArr[i]) {
                    stackCup = imageArr[i]
                }
            }
        }
        val mAlertDialog = mBuilder.create()

        mAlertDialog.setCancelable(false)
        mAlertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        mAlertDialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        mDialogView.cancelDialogBt.setOnClickListener {
            mAlertDialog.dismiss()
        }
        mDialogView.doneDialogBt.setOnClickListener {
            if (cupSize != null) {
                form_edit_text1.setText("" + cupSize + "00")
                mAlertDialog.dismiss()
            }
        }

        mAlertDialog.show()
    }

    private fun showDialogChangeWeight() {

        val mDialogView = LayoutInflater.from(this).inflate(R.layout.edit_weight_dialog, null)
        val mBuilder = AlertDialog.Builder(this)
            .setView(mDialogView)
            .setTitle("แก้ไขน้ำหนัก")
        val mAlertDialog = mBuilder.create()

        mDialogView.saveWeightButton.setOnClickListener {
            mAlertDialog.dismiss()
            val weight = mDialogView.textInputEditText.text.toString()
            weightTextView.text = "น้ำหนัก $weight kg"

            val apiHandler = ApiHandler(this,null,null)
            val birthDate = ApiObject.instant.user!!.birthDate
            apiHandler.editUserInfo(ApiObject.instant.user!!.id,null,null,null,null,null,weight.toInt(),null)

        }
        mAlertDialog.show()
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }
    @SuppressLint("CheckResult")
    fun onPostApi(chartName: String, id: String, param1: String, param2: String) {

        val num2 = if(param2 == ""){0}else{param2.toInt()}

        ApiObject.instant.isNewData = true
        when (chartName) {
            Constant.BLOOD_PRESSURE -> {
                val observable = ApiService.loginApiCall().postBloodPressure(id, param1.toInt(), num2)
                observable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ postBloodPressure ->

                        val apiHandler = ApiHandler(this,null,null)
                        apiHandler.getBloodPressure(id)

                    }, { error ->
                        showDialogFailApi()
                        println(error.message.toString())
                    })
            }
            Constant.KIDNEY_FILTRATION_RATE -> {
                val observable = ApiService.loginApiCall().postKidneyLev(id, param1.toDouble(),egfr!!)
                observable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ postKidneyLev ->

                        val apiHandler = ApiHandler(this,null,null)
                        apiHandler.getKidneyLev(id)

                    }, { error ->
                        showDialogFailApi()
                        println(error.message.toString())
                    })
            }
            Constant.BLOOD_SUGAR_LEV -> {
                val observable = ApiService.loginApiCall().postBloodSugar(id, param1.toInt(),num2)
                observable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ postBloodSugar ->

                        val apiHandler = ApiHandler(this,null,null)
                        apiHandler.getBloodSugar(id)

                    }, { error ->
                        showDialogFailApi()
                        println(error.message.toString())
                    })
            }
            Constant.WATER -> {
                val observable = ApiService.loginApiCall().postWaterPerDay(id, param1.toInt())
                observable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ postWaterPerDay ->

                        val intent = Intent(this, HealthFormActivity::class.java)
                        intent.putExtra("graphName" , Constant.WATER)

                        val apiHandler = ApiHandler(this,null,intent)
                        apiHandler.getWaterPerDay(id)

                    }, { error ->
                        showDialogFailApi()
                        println(error.message.toString())
                    })
            }
        }
    }


    private fun showDialogFailApi() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("เกิดข้อผิดพลาด")
        builder.setMessage("ไม่สามารถบันทึกได้ กรุณาลองใหม่อีกครั้ง")
        builder.setPositiveButton("ปิด") { dialog, which -> dialog.cancel() }
        builder.show()
    }
}

