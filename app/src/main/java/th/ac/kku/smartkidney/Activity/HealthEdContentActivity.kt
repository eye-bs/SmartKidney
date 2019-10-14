package th.ac.kku.smartkidney

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_health_ed_content.*

class HealthEdContentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_health_ed_content)

        val readJSON = ReadJSON(this)
        val topicObj = readJSON.getJSONObject(Constant.HEALTHED_TOPIC_JSON,Constant.HEALTH_ED_TOPIC)
        val topicArr = topicObj!!.getJSONArray("topic")
        val topic =  ApiObject.instant.healthEdPostion
        Log.wtf(Constant.TAG , "topic $topic")
        val imageArr = arrayOf(R.drawable.healthed1,R.drawable.healthed2,R.drawable.healthed3,R.drawable.healthed4,R.drawable.healthed5,R.drawable.healthed6,R.drawable.healthed7)

        imageHealthEdContent.setImageDrawable(getDrawable(imageArr[topic!!]))

        healthEdContentBt.setOnClickListener{
            val intent = Intent(this,HealtEdActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    override fun onBackPressed() {
        val intent = Intent(this, HealtEdActivity::class.java)
        startActivity(intent)
        finish()
    }
}
