package th.ac.kku.smartkidney

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.viewpager.widget.ViewPager
import com.inthecheesefactory.thecheeselibrary.widget.AdjustableImageView
import kotlinx.android.synthetic.main.activity_health_ed_content.*
import kotlinx.android.synthetic.main.activity_suggestion_today.*
import org.json.JSONObject

@Suppress("DEPRECATION")
class SuggestionTodayActivity : AppCompatActivity() {

    private val readJSON = ReadJSON(this)
    lateinit var analyzeObject: JSONObject
    lateinit var getAnalytics:JSONObject
    private val imageArr = arrayListOf<Drawable>()
    private val consArr = arrayListOf<String>()
    private var imgColor = arrayListOf<String>()
    private var count = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_suggestion_today)

        val bpArr = ApiObject.instant.bloodPressurePerDay
        val bsArr = ApiObject.instant.bloodSugarPerDay
        val girArr = ApiObject.instant.kidneyLevPerDay
        val calcInput = CalcInput(this)
        var level: Int?

        if(bpArr.isEmpty() && bsArr.isEmpty() && girArr.isEmpty()){
            noDataTodayTv.visibility = View.VISIBLE
        }else{
              noDataTodayTv.visibility = View.GONE

            if (bpArr.isNotEmpty()){
                val lastBp = bpArr[bpArr.lastIndex]
                level = calcInput.calcBloodPressure((lastBp.systolic).toFloat(), (lastBp.diastolic).toFloat())!!
                getImage(Constant.BLOOD_PRESSURE , level)
                consArr.add(Constant.BLOOD_PRESSURE)
            }
            if (bsArr.isNotEmpty()){
                val lastBs = bsArr[bsArr.lastIndex]
                level = calcInput.calcGlucose(lastBs.sugarLevel.toFloat())
                getImage(Constant.BLOOD_SUGAR_LEV , level)
                consArr.add(Constant.BLOOD_SUGAR_LEV)
            }
            if (girArr.isNotEmpty()){
                val lastgir = girArr[girArr.lastIndex]
                val calcKidney = calcInput.calcKidney(lastgir.cr.toFloat() , ApiObject.instant.age!! , ApiObject.instant.user!!.gender)
                level = ApiObject.instant.kidneyRange
                getImage(Constant.KIDNEY_FILTRATION_RATE , level!!)
                consArr.add(Constant.KIDNEY_FILTRATION_RATE)
            }

            if (imageArr.size >= 2){
                buttonImageTodayLay.visibility = View.VISIBLE
                photoViewToday.setImageDrawable(imageArr[count])
                todayContentLay.setBackgroundColor(Color.parseColor(imgColor[count]))
                headerSuggest.text = consArr[count]
                todayBackImageBt.visibility = View.INVISIBLE
                todayBackImageBt.setOnClickListener {
                    count--
                    todayNextImageBt.visibility = View.VISIBLE
                    photoViewToday.setImageDrawable(imageArr[count])
                    headerSuggest.text = consArr[count]
                    todayContentLay.setBackgroundColor(Color.parseColor(imgColor[count]))

                    if (count <= 0){
                        todayBackImageBt.visibility = View.INVISIBLE
                    }
                }
                todayNextImageBt.setOnClickListener {
                    count++
                    todayBackImageBt.visibility = View.VISIBLE
                    photoViewToday.setImageDrawable(imageArr[count])
                    headerSuggest.text = consArr[count]
                    todayContentLay.setBackgroundColor(Color.parseColor(imgColor[count]))

                    if (count == imageArr.size-1){
                        todayNextImageBt.visibility = View.INVISIBLE
                    }

                }

            }else if(imageArr.size == 1){
                photoViewToday.setImageDrawable(imageArr[0])
                headerSuggest.text = consArr[0]
                todayContentLay.setBackgroundColor(Color.parseColor(imgColor[0]))

            }

       }

        suggestTodayBt.setOnClickListener {
            val intent = Intent(this,HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    fun getImage(name:String , level:Int){
        analyzeObject = readJSON.getJSONObject(Constant.ANALYZE_DETAL_JSON, name)!!
        getAnalytics = analyzeObject.getJSONArray("analytics").getJSONObject(level)
        val img =  getAnalytics.getJSONArray("img")


        for (i in 0 until img.length()){
            if (img.getString(i) != "") {
                imgColor.add(getAnalytics.getString("img_color"))
                val resources = this.resources
                val resourceId =
                    resources.getIdentifier(img.getString(i), "drawable", this.packageName)
                val drawable = resources.getDrawable(resourceId)
                imageArr.add(drawable)
            }
        }
    }
    override fun onBackPressed() {
        val intent = Intent(this,HomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}
