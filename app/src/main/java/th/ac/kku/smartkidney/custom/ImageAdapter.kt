package th.ac.kku.smartkidney

import androidx.viewpager.widget.PagerAdapter
import android.view.ViewGroup
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import com.inthecheesefactory.thecheeselibrary.widget.AdjustableImageView


class ImageAdapter(val context:Context, private val imageArr: ArrayList<Drawable>) : PagerAdapter() {


    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun getCount(): Int {
      return imageArr.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val imageView = AdjustableImageView(context)
        val param = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT , LinearLayout.LayoutParams.WRAP_CONTENT)
        imageView.adjustViewBounds = true
        imageView.setImageDrawable(imageArr[position])
        container.addView(imageView , 0)
        return imageView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as ImageView)
    }

}