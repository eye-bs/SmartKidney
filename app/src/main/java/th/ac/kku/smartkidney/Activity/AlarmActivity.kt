package th.ac.kku.smartkidney

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_alarm.*
import kotlinx.android.synthetic.main.activity_home.*

class AlarmActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm)

        alarmHomeBt.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, AlarmActivity::class.java)
            startActivity(intent)
        })
    }
}
