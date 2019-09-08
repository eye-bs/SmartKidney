package th.ac.kku.smartkidney

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment


class HealthFormActivity : AppCompatActivity() {

    lateinit var baseFragment: Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_health_form)

        baseFragment = BaseFragment.newInstance()

        if (savedInstanceState == null){
            supportFragmentManager
                .beginTransaction()
                .add(R.id.layout_fragment_container,baseFragment)
                .commit()
        }


    }


}
