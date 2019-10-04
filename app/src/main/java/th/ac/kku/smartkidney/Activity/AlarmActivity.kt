package th.ac.kku.smartkidney

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.CalendarContract
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_alarm.*
import th.ac.kku.smartkidney.ReadCalendar.getEventHashMapArr
import th.ac.kku.smartkidney.ReadCalendar.readCalendar
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class AlarmActivity : AppCompatActivity() {

    private lateinit var hashMapEvent: ArrayList<HashMap<String,String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm)

        hashMapEvent = getEventHashMapArr()
        rootFutureAlarmLayout.removeAllViews()
        if (hashMapEvent.size != 0){
            noAlarmLayout.visibility = View.GONE
            createFutureEvent(hashMapEvent)
        }
        //--------------setOnClick-------------------
        alarmHomeBt.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
        addAlarmBt.setOnClickListener {
            val intent = Intent(this, AddAlarmActivity::class.java)
            startActivityForResult(intent, 800)
        }
        //--------------------------------------------
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 800 && resultCode == Activity.RESULT_OK) {
            noAlarmLayout.visibility = View.GONE
            hashMapEvent = readCalendar(this)
            rootFutureAlarmLayout.removeAllViews()
            createFutureEvent(hashMapEvent)
        }
    }

    override fun onBackPressed() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    @SuppressLint("SetTextI18n")
    @TargetApi(Build.VERSION_CODES.M)
    fun createFutureEvent(hashMapEvent: ArrayList<HashMap<String, String>>) {

        val timeFormat = SimpleDateFormat("EEEE 'ที่' d MMMM 'เวลา' HH:mm 'น.'", Locale.getDefault())
        val nextAlarm = hashMapEvent[0]
        nextAlarmTextView.text = "นัดครั้งถัดไป ${timeFormat.format(nextAlarm["begin"]!!.toLong())}"
        for (i in 1 until hashMapEvent.size) {

            val getDateLong = hashMapEvent[i]["begin"]!!.toLong()
            val stDate = timeFormat.format(getDateLong)

            // create view
            val linearLayoutHorizon = LinearLayout(this)
            val layoutNode = LinearLayout(this)
            val imageView = ImageView(this)
            val viewUpper = View(this)
            val viewLower = View(this)
            val textView = TextView(this)

            val paramForHorizon = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            val paramForNode = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT)
            val paramForView = LinearLayout.LayoutParams(2, 0, 1f)
            val paramForImage = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            val paramForText = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)

            paramForView.gravity = Gravity.CENTER
            paramForText.setMargins(20, 20, 20, 20)

            linearLayoutHorizon.orientation = LinearLayout.HORIZONTAL
            layoutNode.orientation = LinearLayout.VERTICAL
            viewUpper.setBackgroundColor(getColor(R.color.malibu))
            viewLower.setBackgroundColor(getColor(R.color.malibu))
            imageView.setImageDrawable(getDrawable(R.drawable.node_alarm_shape))
            textView.setPadding(20, 20, 20, 20)
            textView.elevation = 10f
            textView.background = getDrawable(R.drawable.white_card)
            textView.gravity = Gravity.CENTER
            textView.text = stDate

            linearLayoutHorizon.layoutParams = paramForHorizon
            layoutNode.layoutParams = paramForNode
            imageView.layoutParams = paramForImage
            viewUpper.layoutParams = paramForView
            viewLower.layoutParams = paramForView
            textView.layoutParams = paramForText

            layoutNode.addView(viewUpper)
            layoutNode.addView(imageView)
            layoutNode.addView(viewLower)

            linearLayoutHorizon.addView(layoutNode)
            linearLayoutHorizon.addView(textView)
            rootFutureAlarmLayout.addView(linearLayoutHorizon)

            linearLayoutHorizon.setOnClickListener{
                val builder = CalendarContract.CONTENT_URI.buildUpon()
                builder.appendPath("time")
                builder.appendPath(java.lang.Long.toString(getDateLong))
                intent = Intent(Intent.ACTION_VIEW, builder.build())
                startActivity(intent)
            }

        }
    }
}
