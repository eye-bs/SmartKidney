package th.ac.kku.smartkidney

import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.util.Log
import kotlinx.android.synthetic.main.activity_suggestion_today.*


class SuggestionTodayActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_suggestion_today)

        val bpArr = ApiObject.instant.bloodPressurePerDay
        val bsArr = ApiObject.instant.bloodSugarPerDay
        val girArr = ApiObject.instant.kidneyLevPerDay
        val calcInput = CalcInput(this)
        var level: Int?
        val imageArr = arrayListOf<Drawable>(getDrawable(R.drawable.healthed1_intro_kidney) , getDrawable(R.drawable.page2_introkidney) ,getDrawable(R.drawable.page3_introkidney))

        if (bpArr.isNotEmpty()){
           val lastBp = bpArr[bpArr.lastIndex]
            level = calcInput.calcBloodPressure(lastBp.systolic, lastBp.diastolic)!!
            Log.wtf(Constant.TAG , "BP lev $level")
        }
        if (bsArr.isNotEmpty()){
            val lastBs = bsArr[bsArr.lastIndex]
            level = calcInput.calcGlucose(lastBs.sugarLevel.toFloat(),lastBs.hba1c.toFloat())
            Log.wtf(Constant.TAG , "Bs lev $level")
        }
        if (girArr.isNotEmpty()){
            val lastgir = girArr[girArr.lastIndex]
            val calcKidney = calcInput.calcKidney(lastgir.cr.toFloat() , ApiObject.instant.age!! , ApiObject.instant.user!!.gender)
            level = ApiObject.instant.kidneyRange
            Log.wtf(Constant.TAG , "Bs lev $level")
        }

        suggestTodayBt.setOnClickListener {
            val intent = Intent(this,HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        val adapter = ImageAdapter(this,imageArr)
        viewPager.adapter = adapter

    }
}
