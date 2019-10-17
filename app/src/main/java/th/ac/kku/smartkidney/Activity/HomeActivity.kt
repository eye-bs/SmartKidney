package th.ac.kku.smartkidney

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.google.android.gms.common.api.Api
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.squareup.picasso.Picasso
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_suggestion_today.*
import retrofit2.Retrofit
import java.io.ByteArrayOutputStream
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

@Suppress("DEPRECATION", "IMPLICIT_CAST_TO_ANY", "NAME_SHADOWING")
class HomeActivity : AppCompatActivity(), OnChartValueSelectedListener {

    private lateinit var hashMapEvent: ArrayList<HashMap<String, String>>
    lateinit var mAuth: FirebaseAuth
    lateinit  var currentUser:FirebaseUser
    private val calendar = Calendar.getInstance()
    lateinit var mDatabaseHelper:DatabaseHelper

    @TargetApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        mAuth = FirebaseAuth.getInstance()
        currentUser = mAuth.currentUser!!
        mDatabaseHelper = DatabaseHelper(this)
        ApiObject.instant.weekQuery = ApiObject.instant.currentWeek

        setUserDetail()
        createChart()
        onButtonClick()

    }

    private fun setUserDetail(){
        val userObject = ApiObject.instant.user
       try{
           val data = mDatabaseHelper.getImgData(Constant.NAME_ATT)
           var imgV: ByteArray? = null
           while (data!!.moveToNext()) {
               // itemID = data.getInt(0);
               imgV = data.getBlob(0)
           }

           val bitmap = BitmapFactory.decodeByteArray(imgV, 0, imgV!!.size)
           profileImage.setImageBitmap(bitmap)

       }catch (e:Exception){

           if(userObject!!.gender == "male"){
               profileImage.setImageDrawable(getDrawable(R.drawable.male))

           }else{
               profileImage.setImageDrawable(getDrawable(R.drawable.female))
           }

           val bitmapp = (profileImage.drawable as BitmapDrawable).bitmap
           val stream = ByteArrayOutputStream()
           bitmapp.compress(Bitmap.CompressFormat.PNG, 100, stream)
           val imgByteArr =  stream.toByteArray()
           mDatabaseHelper.addData(Constant.NAME_ATT, imgByteArr)
       }

//            Picasso.with(this)
//                .load(currentUser.photoUrl.toString())
//                .into(profileImage)


        textName.text = userObject!!.name

        // gender
        val genderEng = userObject.gender
        textGender.text = if (genderEng == "male"){"เพศ ชาย"}else{"เพศ หญิง"}
        //Age
        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX")
        val birthDate = formatter.parse(userObject.birthDate)
        calendar.time = birthDate
        val birthYear = calendar.get(Calendar.YEAR)
        val birthMonth = calendar.get(Calendar.MONTH)
        val toDay = formatter.parse(formatter.format(Date(System.currentTimeMillis())))
        calendar.time = toDay
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH)
        val age = if(birthMonth>=currentMonth){currentYear - birthYear - 1}else{currentYear - birthYear}
        ApiObject.instant.age = age

        textOld.text = "อายุ $age"

        hashMapEvent = ReadCalendar.readCalendar(this)
        val timeFormat = SimpleDateFormat("'วันที่' d MMMM 'เวลา' HH:mm 'น.'", Locale.getDefault())
        if (hashMapEvent.size != 0){
            val nextAlarm = hashMapEvent[0]
            nextAlarmHomeTextView.text =
                "นัดพบแพทย์ ${timeFormat.format(nextAlarm["begin"]!!.toLong())}"
        }else{
            nextAlarmHomeTextView.text = "ยังไม่มีวันนัดพบแพทย์"
        }

        val weight = ApiObject.instant.user!!.weight
        if(weight != 0){
           val waterPerDay = weight * 2.2 * 30 / 2
            ApiObject.instant.waterPerDay = waterPerDay.toInt()
        }

    }

    private fun createChart(){
        val constantArr = arrayOf(
            Constant.BLOOD_SUGAR_LEV,
            Constant.KIDNEY_FILTRATION_RATE,
            Constant.BLOOD_PRESSURE)

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        var height = displayMetrics.heightPixels / 4

        val paramForChartLay = LinearLayout.LayoutParams(0,height,1f)
        paramForChartLay.setMargins(0,0,18,0)
        upperBpHomeLay.layoutParams = paramForChartLay
        lowerBpHomeLay.layoutParams = paramForChartLay
        gitHomeLay.layoutParams = paramForChartLay
        bsHomeLay.layoutParams = paramForChartLay
        waterHomeLay.layoutParams = paramForChartLay

        val chartIdArr = arrayOf(sugarChart,kidneyChart,pressureUpperChart, pressureLowerChart)
        val readjson = ReadJSON(this)
        for (i in constantArr.indices) {
            val obj = readjson.getJSONObject(Constant.GRAPH_DETAIL_JSON, constantArr[i])
            val arrChart = obj!!.getJSONArray("graph")
            val chartJSONObject = arrChart.getJSONObject(0)
            val setupChart = SetupChart(obj, this, null,constantArr[i])
            val hasValue = setupChart.isHasValue()
            if(hasValue){
                when (i) {
                    2 -> {
                        val chartJSONObject1 = arrChart.getJSONObject(1)
                        setupChart.lineChartSetUp(chartIdArr[i + 1] as LineChart, chartJSONObject1)
                    }
                }
                setupChart.lineChartSetUp(chartIdArr[i] , chartJSONObject)
            }
        }
        val waterChartJson = readjson.getJSONObject(Constant.GRAPH_DETAIL_JSON, Constant.WATER)
        val setupChart = SetupChart(waterChartJson!!, this, null ,Constant.WATER)
        setupChart.isHasValue()
        setupChart.pieChartSetUp(waterChart)
    }

    override fun onNothingSelected() {}

    override fun onValueSelected(e: Entry?, h: Highlight?) {

    }

    override fun onBackPressed() {
        val a = Intent(Intent.ACTION_MAIN)
        a.addCategory(Intent.CATEGORY_HOME)
        a.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(a)
        finish()
    }

    fun onButtonClick(){
        val intent = Intent(this, HealthFormActivity::class.java)
        pressureUpperChart.setOnClickListener {
            intent.putExtra("graphName" , Constant.BLOOD_PRESSURE)
            startActivity(intent)
        }
        pressureLowerChart.setOnClickListener {
            intent.putExtra("graphName" , Constant.BLOOD_PRESSURE)
            startActivity(intent)
        }
        kidneyChart.setOnClickListener {
            intent.putExtra("graphName" , Constant.KIDNEY_FILTRATION_RATE)
            startActivity(intent)
        }
        sugarChart.setOnClickListener {
            intent.putExtra("graphName" , Constant.BLOOD_SUGAR_LEV)
            startActivity(intent)
        }
        waterChartHomeLay.setOnClickListener {
            intent.putExtra("graphName" , Constant.WATER)
            startActivity(intent)
        }
        alarmLayout.setOnClickListener {
            val intent = Intent(this, AlarmActivity::class.java)
            startActivity(intent)
            finish()
        }

        healthFormLayout.setOnClickListener {
            val intent = Intent(this, HealthFormActivity::class.java)
            startActivity(intent)
            finish()
        }
        healthEdLayout.setOnClickListener {
            val intent = Intent(this, HealtEdActivity::class.java)
            startActivity(intent)
            finish()
        }
        dailyRecommentLayout.setOnClickListener {
            val intent = Intent(this, SuggestionTodayActivity::class.java)
            startActivity(intent)
            finish()
        }

        settingProfileBt.setOnClickListener {
            val intent = Intent(this, EditProfileActivity::class.java)
            startActivity(intent)
            finish()
//            FirebaseAuth.getInstance().signOut()
//            signOut()
//            val intent = Intent(this , LoginActivity::class.java)
//            startActivity(intent)
//            finish()
        }
    }

}
