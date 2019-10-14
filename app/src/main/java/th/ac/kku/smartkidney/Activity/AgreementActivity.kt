package th.ac.kku.smartkidney

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_agreement.*
import kotlinx.android.synthetic.main.activity_register.*

class AgreementActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agreement)

        if (Build.VERSION.SDK_INT >= 21) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
        acceptButton.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, RiskFormActivity::class.java)
            startActivity(intent)
        })
    }
}
