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
import kotlinx.android.synthetic.main.activity_suggestion_today.*
import org.json.JSONObject

@Suppress("DEPRECATION")
class SuggestionTodayActivity : AppCompatActivity() {

    private val readJSON = ReadJSON(this)
    lateinit var analyzeObject: JSONObject
    lateinit var getAnalytics:JSONObject
    private val imageArr = arrayListOf<Drawable>()
    val consArr = arrayListOf<String>()
    private var myViewPagerAdapter: MyViewPagerAdapter? = null
    private var imgColor:String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_suggestion_today)

        val bpArr = ApiObject.instant.bloodPressurePerDay
        val bsArr = ApiObject.instant.bloodSugarPerDay
        val girArr = ApiObject.instant.kidneyLevPerDay
        val calcInput = CalcInput(this)
        var level: Int?

      // val imageArr = arrayListOf<Drawable>(getDrawable(R.drawable.healthed1_intro_kidney) , getDrawable(R.drawable.page2_introkidney) ,getDrawable(R.drawable.page3_introkidney))

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

            myViewPagerAdapter = MyViewPagerAdapter(this,imageArr.size,slideViewLayout(imageArr.size))
            viewPager.adapter = myViewPagerAdapter
            viewPager.addOnPageChangeListener(viewPagerPageChangeListener)
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
       imgColor =  getAnalytics.getString("img_color")
        for (i in 0 until img.length()){
            if (img.getString(i) != "") {
                val resources = this.resources
                val resourceId =
                    resources.getIdentifier(img.getString(i), "drawable", this.packageName)
                val drawable = resources.getDrawable(resourceId)
                imageArr.add(drawable)
            }
        }
    }


    private fun slideViewLayout(numPage: Int):ArrayList<LinearLayout>{
        val layoutArray =  ArrayList<LinearLayout>()

        for (i in 0 until numPage){
            val linearLayout = LinearLayout(this)
            val imageView = AdjustableImageView(this)
            val param = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
            imageView.adjustViewBounds = true
            imageView.setImageDrawable(imageArr[i])
            linearLayout.layoutParams = param
            imageView.layoutParams = param
            linearLayout.setBackgroundColor(Color.parseColor(imgColor))
            linearLayout.addView(imageView)
            layoutArray.add(linearLayout)
        }
        return layoutArray
    }

    private var viewPagerPageChangeListener: ViewPager.OnPageChangeListener = object : ViewPager.OnPageChangeListener {
        override fun onPageSelected(position: Int) {
            headerSuggest.text = consArr[position]
        }
        override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {}
        override fun onPageScrollStateChanged(arg0: Int) {}
    }

    override fun onBackPressed() {
        val intent = Intent(this,HomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}
