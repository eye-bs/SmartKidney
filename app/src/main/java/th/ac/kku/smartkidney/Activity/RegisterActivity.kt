package th.ac.kku.smartkidney

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.widget.RelativeLayout
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        setMarginBg()
        registerButton.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, AgreementActivity::class.java)
            startActivity(intent)
        })
    }

    fun setMarginBg(){
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        //obj size 934 * 422
        var objWidth = 934
        var width = displayMetrics.widthPixels
        if (width > objWidth){
            width -= objWidth
        }else width = objWidth - width

        val param = objRegister.layoutParams as RelativeLayout.LayoutParams
        param.setMargins(0,0,0,width*-1)
        objRegister.layoutParams = param
    }
}
