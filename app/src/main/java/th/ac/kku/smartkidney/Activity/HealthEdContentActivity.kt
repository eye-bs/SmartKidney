package th.ac.kku.smartkidney

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_health_ed_content.*
import org.json.JSONObject


class HealthEdContentActivity : AppCompatActivity() {

    private val readJSON = ReadJSON(this)
    lateinit var analyzeObject: JSONObject
    lateinit var getAnalytics:JSONObject
    private val imageArr = arrayListOf<Drawable>()
    private var imgColor:String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_health_ed_content)

        val topic =  ApiObject.instant.healthEdPostion

        getImage(topic!!)


        healthEdContentLay.setBackgroundColor(Color.parseColor(imgColor))
        imageHealthEdContent.setImageDrawable(imageArr[0])
        if (imageArr.size == 2){
            buttonImageLay.visibility = View.VISIBLE
            bacImageBt.visibility = View.INVISIBLE
            bacImageBt.setOnClickListener {
                imageHealthEdContent.setImageDrawable(imageArr[0])
                bacImageBt.visibility = View.INVISIBLE
                nextImageBt.visibility = View.VISIBLE
            }
            nextImageBt.setOnClickListener{
                imageHealthEdContent.setImageDrawable(imageArr[1])
                nextImageBt.visibility = View.INVISIBLE
                bacImageBt.visibility = View.VISIBLE
            }
        }

        healthEdContentBt.setOnClickListener{
            val intent = Intent(this,HealtEdActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun getImage(level:Int){
        analyzeObject = readJSON.getJSONObject(Constant.HEALTHED_TOPIC_JSON, Constant.HEALTH_ED_TOPIC)!!
        getAnalytics = analyzeObject.getJSONArray("images").getJSONObject(level)
        val img =  getAnalytics.getJSONArray("img")

        for (i in 0 until img.length()){
            if (img.getString(i) != "") {
                imgColor =  getAnalytics.getString("img_color")
                val resources = this.resources
                val resourceId =
                    resources.getIdentifier(img.getString(i), "drawable", this.packageName)
                val drawable = resources.getDrawable(resourceId)
                imageArr.add(drawable)
            }
        }
    }

    override fun onBackPressed() {
        val intent = Intent(this, HealtEdActivity::class.java)
        startActivity(intent)
        finish()
    }
}
