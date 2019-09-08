package th.ac.kku.smartkidney

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_agreement.*
import kotlinx.android.synthetic.main.activity_register.*

class AgreementActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agreement)

        acceptButton.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, RiskFormActivity::class.java)
            startActivity(intent)
        })
    }
}
