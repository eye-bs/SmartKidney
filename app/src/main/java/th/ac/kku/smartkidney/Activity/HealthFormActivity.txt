package th.ac.kku.smartkidney

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_health_form.*
import kotlinx.android.synthetic.main.custom_dialog.view.*
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class HealthFormActivity : AppCompatActivity() {


    lateinit var graphFragment: Fragment
    private var stackMenuItem: Int = 0
    private var weekBPArr = ApiObject.instant.weekKeysbp
    private var weekBsArr = ApiObject.instant.weekKeysbs
    private var weekGirArr = ApiObject.instant.weekKeysgir
    private var weekWaterArr = ApiObject.instant.weekKeyswater
    private var count = 0
    private var stackWeek = 0
    private val calendar = Calendar.getInstance()
    private val parser = SimpleDateFormat("dd/MM/yyyy")
    private var isWater = false
    private var currentWater = 0
    private val nameFormArr = arrayOf(
        Constant.BLOOD_PRESSURE,
        Constant.KIDNEY_FILTRATION_RATE,
        Constant.BLOOD_SUGAR_LEV,
        Constant.WATER,
        Constant.BMI
    )
    private val iconArrGray = arrayOf(
        R.drawable.pressure_2,
        R.drawable.kidney_3,
        R.drawable.glucosemeter_2,
        R.drawable.water_2,
        R.drawable.scale_2
    )
    private val iconArrColor = arrayOf(
        R.drawable.pressure,
        R.drawable.kidney_2,
        R.drawable.glucosemeter,
        R.drawable.water,
        R.drawable.scale
    )
    private val itemIdArr = arrayOf(
        R.id.pressure_nev,
        R.id.kidney_nev,
        R.id.glucose_nev,
        R.id.water_nev,
        R.id.bmi_nev
    )
    private val imageWaterAnalyze = arrayOf(
            R.drawable.water_lev1,
            R.drawable.water_lev2,
            R.drawable.water_lev3
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_health_form)

        setSupportActionBar(toolbar)
        supportActionBar?.title = ""
        nev_bar.itemIconTintList = null
        nev_bar.menu.findItem(R.id.pressure_nev).icon = getDrawable(R.drawable.pressure)

        val getGraphName = intent.getStringExtra("graphName")

        healthFormHomeBt.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        graphFragment = GraphFragment.newInstance(Constant.BLOOD_PRESSURE)
        if (savedInstanceState == null) {
            onWeekSelect(weekBPArr)
            supportFragmentManager
                .beginTransaction()
                .add(R.id.layout_fragment_container, graphFragment)
                .commit()
        }
        nev_bar.setOnNavigationItemSelectedListener { item ->
            onNevBarOnclick(item.itemId)
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.layout_fragment_container, graphFragment)
                .commit()
            return@setOnNavigationItemSelectedListener true
        }

        if(getGraphName != ""){
            for (i in nameFormArr.indices) {
                if (getGraphName == nameFormArr[i]) {

                    nev_bar.menu.findItem(itemIdArr[i]).isChecked = true
                    onNevBarOnclick(itemIdArr[i])

                    graphFragment = GraphFragment.newInstance(getGraphName)
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.layout_fragment_container, graphFragment)
                        .commit()
                }
            }
        }

        addFormBt.setOnClickListener {
            val intent = Intent(this, AddFormActivity::class.java)
            intent.putExtra("form", nameFormArr[stackMenuItem])
            startActivityForResult(intent, 1000)
        }

        analyzeWaterPerDay.setOnClickListener {
            val waterIn = ApiObject.instant.waterInDay[currentWater]
            if (waterIn != null){
                val waterPerDay = ApiObject.instant.waterPerDay
                val perCent:Float = ((waterIn*100)/waterPerDay).toFloat()
                val lev = when{
                    perCent < 100 -> 0
                    perCent == 100.toFloat() -> 1
                    else -> 2
                }

                soundHandle(lev)

                val mDialogView = LayoutInflater.from(this).inflate(R.layout.custom_dialog, null)
                val mBuilder = AlertDialog.Builder(this)
                    .setView(mDialogView)
                mDialogView.dialogHeader.text = "คำแนะนำ"
                mDialogView.imageDialog.setImageDrawable(getDrawable(imageWaterAnalyze[lev]))
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
                }
                mAlertDialog.show()
            }
        }

    }

    private fun setWeekLable(weekOfYear:Int){
        if (isWater){
            ApiObject.instant.weekQuery = weekOfYear
            currentWater = weekOfYear
            val ddMMYYY = ApiObject.instant.ddMMyyWater[weekOfYear]
            val formater =  SimpleDateFormat("d MMMM yyyy")
            val dateToString = formater.format(ddMMYYY)
            weekTextView.text = dateToString
        }else{
            calendar.set(Calendar.WEEK_OF_YEAR, weekOfYear)
            calendar.set(Calendar.DAY_OF_WEEK , Calendar.SUNDAY)
            val firstDate = parser.format(calendar.time)
            calendar.add(Calendar.DAY_OF_WEEK , 6)
            val endDate = parser.format(calendar.time)
            weekTextView.text = "$firstDate - $endDate"
        }
    }

    private fun onWeekSelect(arr:ArrayList<Int>){

        count = arr.lastIndex


        if (count < 0){
            selWeekLay.visibility = View.GONE
        }else{
            stackWeek = arr[arr.lastIndex]
            selWeekLay.visibility = View.VISIBLE
            weekRightBt.visibility = View.INVISIBLE
            setWeekLable(stackWeek)
        }


        if (arr.isNotEmpty()){
            Log.wtf(Constant.TAG , "arr = $arr || arr[0] = ${arr[0]} || ApiObject.instant.thisDay ${ApiObject.instant.thisDay}")

            weekLeftBt.visibility = View.VISIBLE
            if(arr[0] == ApiObject.instant.currentWeek || arr[0] == ApiObject.instant.thisDay){
                weekRightBt.visibility = View.INVISIBLE
                weekLeftBt.visibility = View.INVISIBLE
            }else{
                weekLeftBt.setOnClickListener{
                    try{
                        count--
                        weekRightBt.visibility = View.VISIBLE
                        if (count == 0){
                            weekLeftBt.visibility = View.INVISIBLE
                        }else weekLeftBt.visibility = View.VISIBLE

                        ApiObject.instant.weekQuery = arr[count]
                        stackWeek = arr[count]

                        graphFragment = GraphFragment.newInstance(nameFormArr[stackMenuItem])
                        supportFragmentManager
                                .beginTransaction()
                                .replace(R.id.layout_fragment_container, graphFragment)
                                .commit()

                        setWeekLable(stackWeek)
                    }catch (e: Exception){ }
                }
                weekRightBt.setOnClickListener{
                    count++
                    weekLeftBt.visibility = View.VISIBLE
                    if (count == arr.lastIndex){
                        weekRightBt.visibility = View.INVISIBLE
                    }else weekRightBt.visibility = View.VISIBLE

                    ApiObject.instant.weekQuery = arr[count]
                    stackWeek = arr[count]

                    graphFragment = GraphFragment.newInstance(nameFormArr[stackMenuItem])
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.layout_fragment_container, graphFragment)
                        .commit()

                    setWeekLable(stackWeek)
                }
            }
        }else{
            weekRightBt.visibility = View.INVISIBLE
            weekLeftBt.visibility = View.INVISIBLE
        }
    }

    private fun onNevBarOnclick(getitem: Int) {
        ApiObject.instant.weekQuery = ApiObject.instant.currentWeek
        val item = nev_bar.menu.findItem(getitem)
        if (item == nev_bar.menu.findItem(R.id.bmi_nev)) {
            addFormBt.visibility = View.INVISIBLE
        } else {
            addFormBt.visibility = View.VISIBLE
        }
        for (i in itemIdArr.indices) {
            if (item.itemId == itemIdArr[i]) {
                item.icon = getDrawable(iconArrColor[i])
                when (item) {
                    nev_bar.menu.findItem(R.id.bmi_nev) -> {
                        selWeekLay.visibility = View.GONE
                        analyzeWaterPerDay.visibility = View.INVISIBLE}
//                    nev_bar.menu.findItem(R.id.water_nev) -> {
//                        selWeekLay.visibility = View.GONE
//                        analyzeWaterPerDay.visibility = View.VISIBLE
//                    }
                    else -> {
                        selWeekLay.visibility = View.VISIBLE
                        analyzeWaterPerDay.visibility = View.INVISIBLE
                        when (i) {
                            0 -> {
                                isWater = false
                                onWeekSelect(weekBPArr)
                            }
                            1 -> {
                                isWater = false
                                onWeekSelect(weekGirArr)
                            }
                            2 -> {
                                isWater = false
                                onWeekSelect(weekBsArr)
                            }
                            3 -> {
                                isWater = true
                                analyzeWaterPerDay.visibility = View.VISIBLE
                                onWeekSelect(weekWaterArr)
                            }
                        }
                    }
                }

                if (stackMenuItem != i) {
                    nev_bar.menu.findItem(itemIdArr[stackMenuItem]).icon =
                        getDrawable(iconArrGray[stackMenuItem])
                    stackMenuItem = i
                }
                graphFragment = GraphFragment.newInstance(nameFormArr[i])
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1000 && resultCode == Activity.RESULT_OK && data != null) {
            val intent = Intent(this, HealthFormActivity::class.java)
            intent.putExtra("graphName", data.getStringExtra("graphName"))
            startActivity(intent)

        }
    }

    override fun onBackPressed() {
        val input = Intent(this, HomeActivity::class.java)
        startActivity(input)
        finish()
    }

    fun soundHandle(lev:Int){
        val soundArr = arrayOf(R.raw.lev3,R.raw.lev1,R.raw.lev4)
            val media = MediaPlayer.create(this,soundArr[lev])
        media.start()

    }
}
