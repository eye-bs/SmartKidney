package th.ac.kku.smartkidney


import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class MyPagerAdapter : FragmentStatePagerAdapter{

    private var fragmentManager: FragmentManager? = null
    private var anInt: Int? = null

    constructor(fm: FragmentManager, anInt: Int?) : super(fm) {
        this.fragmentManager = fm
        this.anInt = anInt
    }


    override fun getItem(position: Int): Fragment {

        when(position){
            0 -> {
                return GraphFragment.newInstance(Constant.BLOOD_PRESSURE)
            }
            1 -> {
            return GraphFragment.newInstance(Constant.KIDNEY_FILTRATION_RATE)
            }
            2 -> {
                return GraphFragment.newInstance(Constant.BLOOD_SUGAR_LEV)
            }
            3 -> {
                return GraphFragment.newInstance(Constant.WATER)
            }
            else ->{
                return GraphFragment.newInstance(Constant.BLOOD_PRESSURE)
            }
        }
    }

    override fun getCount(): Int {
        return anInt!!
    }


}