package th.ac.kku.smartkidney

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.squareup.picasso.Picasso
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_home.*
import retrofit2.Retrofit
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

@Suppress("DEPRECATION", "IMPLICIT_CAST_TO_ANY")
class HomeActivity : AppCompatActivity(), OnChartValueSelectedListener {

    lateinit var chart: LineChart
    private lateinit var hashMapEvent: ArrayList<HashMap<String, String>>
    private lateinit var retrofit: Retrofit
    lateinit var mAuth: FirebaseAuth
    lateinit  var currentUser:FirebaseUser
    private val df = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private val calendar = Calendar.getInstance()

    @TargetApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        mAuth = FirebaseAuth.getInstance()
        currentUser = mAuth.currentUser!!

        setUserDetail()
        createChart()
        onButtonClick()

        if(ApiObject.instant.isNewData){
            homeProgressBar.visibility = View.VISIBLE
            if (ConnectivityHelper.isConnectedToNetwork(this)) {
                ApiObject.instant.isNewData = false
                getBloodPressure(ApiObject.instant.user!!.id,null,null)

            } else {

                val dialog = Dialog(this)
                dialog.setContentView(R.layout.connect_falied_dialog)
                dialog.setCancelable(false)

                val button1 = dialog.findViewById<TextView>(R.id.button_dialog)
                button1.setOnClickListener {
                    dialog.cancel()
                    val intent = Intent(this,HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                dialog.show()
            }
        }
    }

    @SuppressLint("CheckResult")
    private fun getBloodPressure(id: String,week:Int?,year:Int?) {

        val observable = ApiService.loginApiCall().getBloodPressure(id,week,year)

        observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ getBloodPressure ->

                ApiObject.instant.bloodPressure = getBloodPressure
                val bpArrayList = ArrayList<BloodPressure>()
                val firstDateString = getBloodPressure[0].date
                val firstDate = df.parse(firstDateString)
                calendar.time = firstDate
                var  firstWeek = calendar.get(Calendar.WEEK_OF_YEAR)
                var stackDate = calendar.get(Calendar.DATE)

                for (i in getBloodPressure.indices){
                    val dateString = getBloodPressure[i].date
                    val date = df.parse(dateString)
                    calendar.time = date
                    val week = calendar.get(Calendar.WEEK_OF_YEAR)
                    val dateInMonth = calendar.get(Calendar.DATE)
                    if (dateInMonth > stackDate || i == getBloodPressure.size-1){
                        if(i ==  getBloodPressure.size-1){
                            bpArrayList.add(getBloodPressure[i])
                        }else{
                            bpArrayList.add(getBloodPressure[i-1])
                        }
                        stackDate = dateInMonth
                    }
                    if (week > firstWeek || i == getBloodPressure.size-1){
                        ApiObject.instant.bpHashByWeek[firstWeek] = bpArrayList
                        firstWeek = week
                    }
                }
                val objHash = ApiObject.instant.bpHashByWeek[40]
                Log.wtf(Constant.TAG , "obj:: ${objHash.toString()}")
                getKidneyLev(id,week,year)
            }, { error ->
                getKidneyLev(id,week,year)
                Log.wtf(Constant.TAG,error.message.toString())
            }
            )
    }
    @SuppressLint("CheckResult")
    private fun getKidneyLev(id: String,week:Int?,year:Int?) {

        val observable = ApiService.loginApiCall().getKidneyLev(id,week,year)

        observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ getKidneyLev ->
                ApiObject.instant.kidneyLev = getKidneyLev
                getBloodSugar(id,week, year)
            }, { error ->
                println(error.message.toString())
                getBloodSugar(id,week, year)
            }
            )
    }
    @SuppressLint("CheckResult")
    private fun getBloodSugar(id: String,week:Int?,year:Int?) {
        val observable = ApiService.loginApiCall().getBloodSugar(id,week,year)
        observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ getBloodSugar ->
                homeProgressBar.visibility = View.INVISIBLE
                ApiObject.instant.bloodSugar = getBloodSugar
                ApiObject.instant.notFound404 = false
            }, { error ->
                homeProgressBar.visibility = View.INVISIBLE
                ApiObject.instant.notFound404 = true
            }
            )
    }

    private fun setUserDetail(){
        val userObject = ApiObject.instant.user
        Picasso.with(this)
            .load(currentUser.photoUrl.toString())
            .into(profileImage)
        textName.text = userObject!!.name

        // gender
        val genderEng = userObject!!.gender
        textGender.text = if (genderEng == "male"){"เพศ ชาย"}else{"เพศ หญิง"}
        //Age
        val formatter = SimpleDateFormat("dd/MM/yyyy")
        val birthDate = formatter.parse(userObject!!.birthDate)
        calendar.time = birthDate
        val birthYear = calendar.get(Calendar.YEAR)
        val birthMonth = calendar.get(Calendar.MONTH)
        val toDay = formatter.parse(formatter.format(Date(System.currentTimeMillis())))
        calendar.time = toDay
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH)
        val age = if(birthMonth>=currentMonth){currentYear - birthYear - 1}else{currentYear - birthYear}


        textOld.text = "อายุ $age"

        Log.wtf(Constant.TAG,"birth:$birthYear  current:$currentYear")

        hashMapEvent = ReadCalendar.readCalendar(this)
        val timeFormat = SimpleDateFormat("'วันที่' d MMMM 'เวลา' HH:mm 'น.'", Locale.getDefault())
        if (hashMapEvent.size != 0){
            val nextAlarm = hashMapEvent[0]
            nextAlarmHomeTextView.text =
                "นัดพบแพทย์ ${timeFormat.format(nextAlarm["begin"]!!.toLong())}"
        }else{
            nextAlarmHomeTextView.text = "ยังไม่มีวันนัดพบแพทย์"
        }

    }

    private fun createChart(){
        val constantArr = arrayOf(
            Constant.BLOOD_SUGAR_LEV,
            Constant.KIDNEY_FILTRATION_RATE,
            Constant.BLOOD_PRESSURE)

        val chartIdArr = arrayOf(sugarChart,kidneyChart,pressureUpperChart, pressureLowerChart)
        val readjson = ReadJSON(this)
        for (i in constantArr.indices) {
            val obj = readjson.getJSONObject(Constant.GRAPH_DETAIL_JSON, constantArr[i])
            val arrChart = obj!!.getJSONArray("graph")
            val chartJSONObject = arrChart.getJSONObject(0)
            val setupChart = SetupChart(obj, this, null)
            when (i) {
                2 -> {
                    val chartJSONObject = arrChart.getJSONObject(1)
                    setupChart.lineChartSetUp(chartIdArr[i + 1] as LineChart, chartJSONObject)
                }
            }
            setupChart.lineChartSetUp(chartIdArr[i] , chartJSONObject)
        }
        val waterChartJson = readjson.getJSONObject(Constant.GRAPH_DETAIL_JSON, Constant.WATER)
        val setupChart = SetupChart(waterChartJson!!, this, null)
        setupChart.PieChartSetUp(waterChart)
    }

    override fun onNothingSelected() {}

    override fun onValueSelected(e: Entry?, h: Highlight?) {
        Log.i(
            "VAL SELECTED",
            "Value: " + e?.y + ", xIndex: " + e?.x
                    + ", DataSet index: " + h?.dataSetIndex
        )
    }

    override fun onBackPressed() {
        val a = Intent(Intent.ACTION_MAIN)
        a.addCategory(Intent.CATEGORY_HOME)
        a.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(a)
        finish()
    }

    private fun signOut() {
        AuthUI.getInstance()
            .signOut(this)
            .addOnCompleteListener { }
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
        waterChart.setOnClickListener {
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
        settingProfileBt.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            signOut()
            val intent = Intent(this , LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

}
