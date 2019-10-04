package th.ac.kku.smartkidney

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_healt_ed.*

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

        for (i in 0 until 4){
            val linearLayoutRow = LinearLayout(this)
            val paramRow = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            linearLayoutRow.orientation = LinearLayout.HORIZONTAL
            linearLayoutRow.layoutParams = paramRow

            for (j  in 0 until 2){
                if (i == 3 && j == 1){
                    val view = View(this)
                    val paramView = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT,1f)
                    view.layoutParams = paramView
                    linearLayoutRow.addView(view)
                    break
                }
                val textView = TextView(this)
                val paramTextView = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT,1f)

                textView.text = Html.fromHtml(topicArr.getString(count))
                textView.background = getDrawable(R.drawable.white_card)
                textView.gravity = Gravity.CENTER
                textView.elevation = 5f
                textView.setPadding(30,100,30,0)

                paramTextView.setMargins(20,30,20,20)

                textView.layoutParams = paramTextView
                linearLayoutRow.addView(textView)
                count++
            }
            rootViewHealthEdTopic.addView(linearLayoutRow)
        }
    }
}
