package th.ac.kku.smartkidney

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_dev_detail.*

class DevDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dev_detail)

        devHomeBt.setOnClickListener {
            val intent = Intent(this,HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    override fun onBackPressed() {
        val intent = Intent(this,HomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}
