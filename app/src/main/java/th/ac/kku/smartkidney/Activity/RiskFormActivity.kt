package th.ac.kku.smartkidney

import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_risk_form.*

class RiskFormActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_risk_form)

        createCheckBox()

        done_bt.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, SuggestActivity::class.java)
            startActivity(intent)
            finish()
        })
    }

    private fun createCheckBox() {

        val res: Resources = resources
        val questionList = res.getStringArray(R.array.risk_question)
        for ((count, i) in questionList.withIndex()) {
            var checkBox = CheckBox(this)
            checkBox.id = count
            checkBox.text = i.toString()
            checkbox_layout.addView(checkBox)
            checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
                //                val msg = "You have " + (if (isChecked) "checked" else "unchecked") + " this Check it Checkbox."
//                Toast.makeText(this@RiskFormActivity, msg, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onBackPressed() {
        val a = Intent(Intent.ACTION_MAIN)
        a.addCategory(Intent.CATEGORY_HOME)
        a.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(a)
        finish()
    }
}
