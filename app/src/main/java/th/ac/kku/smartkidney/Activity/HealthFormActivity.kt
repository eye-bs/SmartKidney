package th.ac.kku.smartkidney

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_health_form.*


class HealthFormActivity : AppCompatActivity() {

    lateinit var graphFragment: Fragment
    var stackMenuItem: Int = 0
    private val nameFormArr = arrayOf(
        Constant.BLOOD_PRESSURE,
        Constant.KIDNEY_FILTRATION_RATE,
        Constant.BLOOD_SUGAR_LEV,
        Constant.WATER,
        Constant.BMI
    )
    private val iconArrGray = arrayOf(
        R.drawable.pressure_2,
        R.drawable.kidney_3,
        R.drawable.glucosemeter_2,
        R.drawable.water_2,
        R.drawable.scale_2
    )
    private val iconArrColor = arrayOf(
        R.drawable.pressure,
        R.drawable.kidney_2,
        R.drawable.glucosemeter,
        R.drawable.water,
        R.drawable.scale
    )
    private val itemIdArr = arrayOf(
        R.id.pressure_nev,
        R.id.kidney_nev,
        R.id.glucose_nev,
        R.id.water_nev,
        R.id.bmi_nev
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_health_form)

        setSupportActionBar(toolbar)
        supportActionBar?.title = ""
        nev_bar.itemIconTintList = null
        nev_bar.menu.findItem(R.id.pressure_nev).icon = getDrawable(R.drawable.pressure)

        val getGraphName = intent.getStringExtra("graphName")

        healthFormHomeBt.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        graphFragment = GraphFragment.newInstance(Constant.BLOOD_PRESSURE)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.layout_fragment_container, graphFragment)
                .commit()
        }
        nev_bar.setOnNavigationItemSelectedListener { item ->
            onNevBarOnclick(item.itemId)
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.layout_fragment_container, graphFragment)
                .commit()
            return@setOnNavigationItemSelectedListener true
        }


        for (i in nameFormArr.indices) {
            if (getGraphName == nameFormArr[i]) {
                graphFragment = GraphFragment.newInstance(getGraphName)
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.layout_fragment_container, graphFragment)
                    .commit()

                nev_bar.menu.findItem(itemIdArr[i]).isChecked = true
                onNevBarOnclick(itemIdArr[i])
            }
        }

        addFormBt.setOnClickListener {
            val intent = Intent(this, AddFormActivity::class.java)
            intent.putExtra("form", nameFormArr[stackMenuItem])
            startActivityForResult(intent, 1000)
        }

    }

    private fun onNevBarOnclick(getitem: Int) {
        val item = nev_bar.menu.findItem(getitem)
        if (item == nev_bar.menu.findItem(R.id.bmi_nev)) {
            addFormBt.visibility = View.INVISIBLE
        } else {
            addFormBt.visibility = View.VISIBLE
        }
        for (i in itemIdArr.indices) {
            if (item.itemId == itemIdArr[i]) {
                item.icon = getDrawable(iconArrColor[i])
                if (stackMenuItem != i) {
                    nev_bar.menu.findItem(itemIdArr[stackMenuItem]).icon =
                        getDrawable(iconArrGray[stackMenuItem])
                    stackMenuItem = i
                }
                graphFragment = GraphFragment.newInstance(nameFormArr[i])
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1000 && resultCode == Activity.RESULT_OK && data != null) {


            val intent = Intent(this, HealthFormActivity::class.java)
            intent.putExtra("graphName", data.getStringExtra("graphName"))
            startActivity(intent)

        }
    }

    override fun onBackPressed() {
        val input = Intent(this, HomeActivity::class.java)
        startActivity(input)
        finish()
    }
}
