package th.ac.kku.smartkidney

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.fragment_base.*
import android.graphics.PorterDuff
import android.webkit.WebChromeClient
import androidx.core.content.ContextCompat




class BaseFragment : Fragment() {

    lateinit var textTabbar: Array<String>
    lateinit var icons: Array<Int>

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        textTabbar = arrayOf(getString(R.string.blood_pressure)
                ,getString(R.string.glomerular)
                ,getString(R.string.blood_sugar)
                ,getString(R.string.water_per_day)
                ,getString(R.string.bmi_th),"")
        icons = arrayOf(R.drawable.pressure
                ,R.drawable.kidney_2
                ,R.drawable.glucosemeter
                ,R.drawable.water
                ,R.drawable.scale
                ,R.drawable.user)


        createTabLayout()
        createViewPager()

    }
    private fun createTabLayout(){

        for(i in 0 until icons.size){
            if(i == 0){
                tabLayout.addTab(tabLayout.newTab().setText(textTabbar[0]).setIcon(icons[i]))
                val tabIconColor = ContextCompat.getColor(context!!, R.color.illusion)
                tabLayout.getTabAt(0)!!.icon!!.setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN)
            }
            else {
                tabLayout.addTab(tabLayout.newTab().setText("").setIcon(icons[i]))
            }
        }
        tabLayout.tabGravity = TabLayout.GRAVITY_FILL
        tabLayout.tabMode = TabLayout.MODE_SCROLLABLE
    }

    private fun createViewPager(){
        val myPagerAdapter = MyPagerAdapter(activity!!.supportFragmentManager, tabLayout.tabCount)
        viewPager.adapter = myPagerAdapter
        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
                override fun onTabSelected(tab: TabLayout.Tab) {
                    viewPager.currentItem = tab.position
                    val tabIconColor = ContextCompat.getColor(context!!, R.color.illusion)
                    tab.icon!!.setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN)
                    tab.text = textTabbar[tab.position]

                    if (tab.position == 5) {

                        val intent = Intent(getActivity(), HomeActivity::class.java)
                        getActivity()!!.startActivity(intent)
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab) {
                    val tabIconColor = ContextCompat.getColor(context!!, R.color.dimGray)
                    tab.icon!!.setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN)
                    tab.text = ""
                }

                override fun onTabReselected(tab: TabLayout.Tab) {

                }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_base, container, false)
    }


    companion object {
        @JvmStatic
        fun newInstance() =
            BaseFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }

    fun setupTabLayout(tabLayout: TabLayout) {

//        val tab = LayoutInflater.from(context).inflate(R.layout.custom_tab, null) as LinearLayout
//        val text = tab.findViewById<TextView>(R.id.tvTabCustom)
//        val img = tab.findViewById<ImageView>(R.id.imgTabCustom)
//
//        text.text = getText(R.string.blood_pressure)
//        img.setImageResource(R.drawable.pressure)
//
//      //  tab.text = getText(R.string.blood_pressure)
//     //   tab.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.pressure, 0, 0)
//        tabLayout.getTabAt(0)!!.customView = tab
    }
}
