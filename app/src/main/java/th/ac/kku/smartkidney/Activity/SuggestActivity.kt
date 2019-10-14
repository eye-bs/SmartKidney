package th.ac.kku.smartkidney

import android.annotation.TargetApi
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.inthecheesefactory.thecheeselibrary.widget.AdjustableImageView
import kotlinx.android.synthetic.main.activity_suggest.*

@Suppress("DEPRECATION")
class SuggestActivity : AppCompatActivity() {

    private var myViewPagerAdapter: MyViewPagerAdapter? = null
    lateinit var dots: Array<TextView>
    private var layouts: IntArray? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_suggest)

        if (Build.VERSION.SDK_INT >= 21) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }

        layouts = intArrayOf(R.drawable.suggest1_slide_into,R.drawable.suggest2_slide_into)

        addBottomDots(0)
        changeStatusBarColor()

        myViewPagerAdapter = MyViewPagerAdapter(this,2,slideViewLayout(2))
        view_pager.adapter = myViewPagerAdapter
        view_pager.addOnPageChangeListener(viewPagerPageChangeListener)

        btn_next.setOnClickListener {
            val current = getItem(+1)
            if (current < layouts!!.size) {
                // move to next screen
                view_pager.currentItem = current
            } else {
                launchHomeScreen()
            }
        }
    }

    private fun slideViewLayout(numPage: Int):ArrayList<LinearLayout>{
        val layoutArray =  ArrayList<LinearLayout>()

        for (i in 0 until numPage){
            val linearLayout = LinearLayout(this)
            val imageView = AdjustableImageView(this)
            val param = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
            imageView.adjustViewBounds = true
            if(i == 0){
                imageView.setImageDrawable(getDrawable(R.drawable.suggest1_slide_into))
            }else{
                imageView.setImageDrawable(getDrawable(R.drawable.suggest2_slide_into))
            }
            linearLayout.layoutParams = param
            imageView.layoutParams = param
            linearLayout.addView(imageView)
            layoutArray.add(linearLayout)
        }

        return layoutArray
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun addBottomDots(currentPage: Int) {
        dots = arrayOf(TextView(this), TextView(this))

        layoutDots.removeAllViews()
        for (i in dots.indices) {
            dots[i] = TextView(this)
            dots[i].text = Html.fromHtml("&#8226;")
            dots[i].textSize = 35f
            dots[i].setTextColor(getColor(R.color.dot_dark_screen3))
            layoutDots.addView(dots[i])
        }
        if (dots.isNotEmpty())
            dots[currentPage].setTextColor(getColor(R.color.dot_light_screen3))
    }

    private fun getItem(i: Int): Int {
        return view_pager.currentItem + i
    }

    private fun launchHomeScreen() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }

    private var viewPagerPageChangeListener: ViewPager.OnPageChangeListener = object : ViewPager.OnPageChangeListener {
        override fun onPageSelected(position: Int) {
            addBottomDots(position)
            if (position == layouts!!.size - 1) {
                btn_next.text = "เริ่มต้น"
            } else {
                btn_next.text = "ถัดไป"
            }
        }
        override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {}
        override fun onPageScrollStateChanged(arg0: Int) {}
    }

    private fun changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.TRANSPARENT
        }
    }

}

