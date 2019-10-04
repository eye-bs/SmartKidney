package th.ac.kku.smartkidney

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
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

        val getGraphName = intent.getStringExtra("graphName")
        for (i in nameFormArr.indices) {
            if (getGraphName == nameFormArr[i]) {
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

    private fun onNevBarOnclick(item: Int) {
        val item = nev_bar.menu.findItem(item)
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
        if (requestCode == 1000 && resultCode == Activity.RESULT_OK) {
        }
    }

    override fun onBackPressed() {
        val input = Intent(this, HomeActivity::class.java)
        startActivity(input)
        finish()
    }

    @SuppressLint("CheckResult")
    fun onGetApi(id: String, week: Int?, year: Int?) {
        val observable = ApiService.loginApiCall().getBloodPressure(id, week, year)
        observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ getBloodPressure ->
                ApiObject.instant.isNewData = true
                ApiObject.instant.bloodPressure = getBloodPressure
            }, { error ->
                ApiObject.instant.notFound404 = true
                println(error.message.toString())
            })

        val observable2 = ApiService.loginApiCall().getKidneyLev(id, week, year)
        observable2.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ getKidneyLev ->
                ApiObject.instant.isNewData = true
                ApiObject.instant.kidneyLev = getKidneyLev
            }, { error ->
                ApiObject.instant.notFound404 = true
                println(error.message.toString())
            })

        val observable3 = ApiService.loginApiCall().getBloodSugar(id, week, year)
        observable3.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ getBloodSugar ->
                ApiObject.instant.isNewData = true
                ApiObject.instant.bloodSugar = getBloodSugar

            }, { error ->
                ApiObject.instant.notFound404 = true
                println(error.message.toString())
            })

    }
}
