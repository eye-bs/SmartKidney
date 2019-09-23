package th.ac.kku.smartkidney

import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        setMarginBg()

        textSingUp.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        })
    }

    fun setMarginBg() {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        //obs size 1214 * 619
        var width = displayMetrics.widthPixels
        if (width > 1214) {
            width -= 1214
        } else width = 1214 - width

        val param = objLogin.layoutParams as RelativeLayout.LayoutParams
        param.setMargins(0, width * -1, 0, 0)
        objLogin.layoutParams = param
    }
}
