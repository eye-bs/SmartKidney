package th.ac.kku.smartkidney

import android.annotation.TargetApi
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_healt_ed.*
import android.util.Log
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import android.content.ActivityNotFoundException
import android.R.id
import android.graphics.Color
import android.net.Uri


@Suppress("DEPRECATION")
class HealtEdActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_healt_ed)



        createTopicView()

        healthEdHomeBt.setOnClickListener{
            val intent = Intent(this,HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    fun createTopicView(){
        val readJSON = ReadJSON(this)
        val topicObj = readJSON.getJSONObject(Constant.HEALTHED_TOPIC_JSON,Constant.HEALTH_ED_TOPIC)
        val topicArr = topicObj!!.getJSONArray("topic")
        var count = 0
        val textViewArr = arrayListOf<TextView>()


        val colors = intArrayOf(
            Color.parseColor("#B5E3F0"), // blue
            Color.parseColor("#C1E3CA"),//green
            Color.parseColor("#F7EAB3"), //yellow
            Color.parseColor("#F8D4BC"), //orange
            Color.parseColor("#EFC2D2"), // red
            Color.parseColor("#EFC2D2")  // red wine
        )

        for (i in 0 until 4){
            val linearLayoutRow = LinearLayout(this)
            val paramRow = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            linearLayoutRow.orientation = LinearLayout.HORIZONTAL
            linearLayoutRow.layoutParams = paramRow

            for (j  in 0 until 2){
//                if (i == 3 && j == 1){
//                    val view = View(this)
//                    val paramView = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT,1f)
//                    view.layoutParams = paramView
//                    linearLayoutRow.addView(view)
//                    break
//                }
                val textView = TextView(this)
                val paramTextView = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT,1f)

                textView.text = Html.fromHtml(topicArr.getString(count))
                textView.setBackgroundColor(colors[i])
                textView.gravity = Gravity.CENTER
                textView.elevation = 5f
                textView.setPadding(30,100,30,0)

                paramTextView.setMargins(20,30,20,20)

                textView.layoutParams = paramTextView
                linearLayoutRow.addView(textView)

                textViewArr.add(textView)

                count++

            }
            rootViewHealthEdTopic.addView(linearLayoutRow)
        }
        for (k in textViewArr.indices){
            textViewArr[k].setOnClickListener {
                if (k == textViewArr.lastIndex){
                    val id = "1eRVGFxfgd8"
                    val appIntent = Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:$id"))
                    val webIntent = Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://www.youtube.com/watch?v=$id"))
                    try {
                        this.startActivity(appIntent)
                    } catch (ex: ActivityNotFoundException) {
                        this.startActivity(webIntent)
                    }
                }else{
                    ApiObject.instant.healthEdPostion = k
                    val intent = Intent(this,HealthEdContentActivity::class.java)
                    startActivity(intent)
                    finish()
                }

            }
        }
    }

    override fun onBackPressed() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}
