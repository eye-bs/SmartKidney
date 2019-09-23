package th.ac.kku.smartkidney

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_health_form.*
import android.widget.Toast
import android.view.MenuItem


class HealthFormActivity : AppCompatActivity() {

    lateinit var graphFragment: Fragment
    var stackMenuItem: Int = 0
    val nameFormArr = arrayOf(Constant.BLOOD_PRESSURE,Constant.KIDNEY_FILTRATION_RATE,Constant.BLOOD_SUGAR_LEV,Constant.WATER,Constant.BMI)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_health_form)

        setSupportActionBar(toolbar)
        supportActionBar?.title = ""
        nev_bar.itemIconTintList = null
        nev_bar.menu.findItem(R.id.pressure_nev).icon = getDrawable(R.drawable.pressure)

        val iconArr = arrayOf(R.drawable.pressure_2,R.drawable.kidney_3,R.drawable.glucosemeter_2,R.drawable.water_2,R.drawable.scale_2)
        val itemIdArr = arrayOf(R.id.pressure_nev,R.id.kidney_nev,R.id.glucose_nev,R.id.water_nev,R.id.bmi_nev)

        graphFragment = GraphFragment.newInstance(Constant.BLOOD_PRESSURE)
        if (savedInstanceState == null){
            supportFragmentManager
                .beginTransaction()
                .add(R.id.layout_fragment_container,graphFragment)
                .commit()
        }

        nev_bar.setOnNavigationItemSelectedListener{item ->
            if (item.itemId != R.id.bmi_nev){
                toolbar.menu.findItem(R.id.add_menu).isVisible = true
            }
            when (item.itemId) {
                R.id.pressure_nev -> {
                    item.icon = getDrawable(R.drawable.pressure)
                    nev_bar.menu.findItem(itemIdArr[stackMenuItem]).icon = getDrawable(iconArr[stackMenuItem])
                    stackMenuItem = 0
                    graphFragment = GraphFragment.newInstance(Constant.BLOOD_PRESSURE)
                }
                R.id.kidney_nev -> {
                    item.icon = getDrawable(R.drawable.kidney_2)
                    nev_bar.menu.findItem(itemIdArr[stackMenuItem]).icon = getDrawable(iconArr[stackMenuItem])
                    stackMenuItem = 1
                    graphFragment = GraphFragment.newInstance(Constant.KIDNEY_FILTRATION_RATE)
                }
                R.id.glucose_nev -> {
                    item.icon = getDrawable(R.drawable.glucosemeter)
                    nev_bar.menu.findItem(itemIdArr[stackMenuItem]).icon = getDrawable(iconArr[stackMenuItem])
                    stackMenuItem = 2
                    graphFragment = GraphFragment.newInstance(Constant.BLOOD_SUGAR_LEV)
                }
                R.id.water_nev -> {
                    item.icon = getDrawable(R.drawable.water)
                    nev_bar.menu.findItem(itemIdArr[stackMenuItem]).icon = getDrawable(iconArr[stackMenuItem])
                    stackMenuItem = 3
                    graphFragment = GraphFragment.newInstance(Constant.WATER)
                }
                R.id.bmi_nev -> {
                    item.icon = getDrawable(R.drawable.scale)
                   nev_bar.menu.findItem(itemIdArr[stackMenuItem]).icon = getDrawable(iconArr[stackMenuItem])
                    stackMenuItem = 4
                    toolbar.menu.findItem(R.id.add_menu).isVisible = false
                    graphFragment = GraphFragment.newInstance(Constant.BLOOD_PRESSURE)
                }
                else -> {
                    nev_bar.menu.findItem(itemIdArr[stackMenuItem]).icon = getDrawable(iconArr[stackMenuItem])
                    stackMenuItem = 0
                    graphFragment = GraphFragment.newInstance(Constant.BLOOD_PRESSURE)
                }
            }
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.layout_fragment_container,graphFragment)
                .commit()
            return@setOnNavigationItemSelectedListener true
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.add_menu) {
            val intent = Intent(this, AddFormActivity::class.java)
            intent.putExtra("form",nameFormArr[stackMenuItem])
            startActivityForResult(intent,1000)
        }else if (id == R.id.user_menu){
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1000 && resultCode == Activity.RESULT_OK){
            Log.wtf(Constant.TAG , "OK?")
        }
    }

    override fun onBackPressed() {
        val input = Intent(this,HomeActivity::class.java)
        startActivity(input)
        finish()
    }

}
