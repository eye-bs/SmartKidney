package th.ac.kku.smartkidney

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_health_ed_content.*

class HealthEdContentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_health_ed_content)

        val topic =  ApiObject.instant.healthEdPostion
        val imageArr = arrayOf(R.drawable.healthed1,R.drawable.healthed2,R.drawable.healthed3,R.drawable.healthed4,R.drawable.healthed5,R.drawable.healthed6,R.drawable.healthed7)
        val colors = intArrayOf(
                Color.parseColor("#FFF0C9"),
                Color.parseColor("#CEEAFF"),
                Color.parseColor("#E7FDEA"),
                Color.parseColor("#FFFEFF"),
                Color.parseColor("#FFFCF1"),
                Color.parseColor("#C6EEFF"),
                Color.parseColor("#FFFFFF")
        )


        healthEdContentLay.setBackgroundColor(colors[topic!!])
        imageHealthEdContent.setImageDrawable(getDrawable(imageArr[topic]))

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
