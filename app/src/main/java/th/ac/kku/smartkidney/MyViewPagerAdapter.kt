package th.ac.kku.smartkidney

import android.content.Context
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.viewpager.widget.PagerAdapter
import kotlinx.android.synthetic.main.slide_layout.view.*

class MyViewPagerAdapter(val context: Context, private val getCount: Int, private val arrLayout: ArrayList<LinearLayout>) : PagerAdapter(){
    private var layoutInflater: LayoutInflater? = null

    fun MyViewPagerAdapter() {}

    override fun getCount(): Int {
        return getCount
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater!!.inflate(R.layout.slide_layout, container, false)
        view.rootViewSlide.addView(arrLayout[position])

        container.addView(view)
        return view
    }

    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return view === obj
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        val view = `object` as View
        container.removeView(view)
    }
}