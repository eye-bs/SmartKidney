package th.ac.kku.smartkidney

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.content.res.use
import com.ekn.gruzer.gaugelibrary.HalfGauge
import com.ekn.gruzer.gaugelibrary.Range
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.EntryXComparator
import com.github.mikephil.charting.utils.MPPointF
import org.json.JSONArray
import org.json.JSONObject
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.Float as Float1

@Suppress("NAME_SHADOWING", "DEPRECATION")
class SetupChart(
    private val jsonObject: JSONObject?,
    val context: Context,
    private val parentLayout: LinearLayout?,
    private val constantName: String
) {

    private lateinit var arrChart: JSONArray
    private val arrValueGraph1 = ArrayList<kotlin.Float>()
    private val arrValueGraph2 = ArrayList<kotlin.Float>()
    private var count: Int = 7
    private val hashBP = ApiObject.instant.bpHashByWeek[ApiObject.instant.weekQuery]
    private val hashBS = ApiObject.instant.bsHashByWeek[ApiObject.instant.weekQuery]
    private val hashGir = ApiObject.instant.girHashByWeek[ApiObject.instant.weekQuery]


    @SuppressLint("UseSparseArrays")
    fun isHasValue(): Boolean {
        arrValueGraph1.clear()
        arrValueGraph2.clear()
        var startDate: Int?
        var endDate: Int?

        if (parentLayout != null) {
            when {
                hashBP != null && constantName == Constant.BLOOD_PRESSURE -> {
                    val keys = arrayListOf<Int>()
                    for (k in hashBP.keys) {
                        keys.add(k)
                    }
                    startDate = Constant.formatOfDetail.parse(hashBP[keys[0]]!!.date).date
                    endDate =
                        Constant.formatOfDetail.parse(hashBP[keys[keys.lastIndex]]!!.date).date
                    count = endDate - startDate + 1
                    for (i in startDate..endDate) {
                        if (hashBP[i] == null) {
                            arrValueGraph1.add(0f)
                            arrValueGraph2.add(0f)
                        } else {
                            arrValueGraph1.add(hashBP[i]!!.systolic.toFloat())
                            arrValueGraph2.add(hashBP[i]!!.diastolic.toFloat())
                        }
                    }
                    return true
                }
                hashBS != null && constantName == Constant.BLOOD_SUGAR_LEV -> {
                    val keys = arrayListOf<Int>()
                    for (k in hashBS.keys) {
                        keys.add(k)
                    }
                    startDate = Constant.formatOfDetail.parse(hashBS[keys[0]]!!.date).date
                    endDate =
                        Constant.formatOfDetail.parse(hashBS[keys[keys.lastIndex]]!!.date).date
                    count = endDate - startDate + 1
                    for (i in startDate..endDate) {
                        if (hashBS[i] == null) {
                            arrValueGraph1.add(0f)
                            arrValueGraph2.add(0f)
                        } else {
                            arrValueGraph1.add(hashBS[i]!!.sugarLevel.toFloat())
                            arrValueGraph2.add(hashBS[i]!!.hba1c.toFloat())
                        }
                    }
                    return true
                }
                hashGir != null && constantName == Constant.KIDNEY_FILTRATION_RATE -> {
                    val keys = arrayListOf<Int>()
                    for (k in hashGir.keys) {
                        keys.add(k)
                    }
                    startDate = Constant.formatOfDetail.parse(hashGir[keys[0]]!!.date).date
                    endDate =
                        Constant.formatOfDetail.parse(hashGir[keys[keys.lastIndex]]!!.date).date
                    count = endDate - startDate + 1
                    for (i in startDate..endDate) {
                        if (hashGir[i] == null) {
                            arrValueGraph1.add(0f)
                            arrValueGraph2.add(0f)
                        } else {
                            arrValueGraph1.add(hashGir[i]!!.egfr.toFloat())
                            arrValueGraph2.add(hashGir[i]!!.cr.toFloat())
                        }
                    }
                    return true
                }
                constantName == Constant.WATER -> {
                    return true
                }
                constantName == Constant.BMI -> {

                    val bmiArr = ApiObject.instant.bmi
                    if (bmiArr.isNotEmpty()) {
                        count = bmiArr.size
                        for (i in 0 until bmiArr.size) {
                            arrValueGraph1.add(bmiArr[i].toFloat())
                        }
                    }

                    return true
                }
                else -> {
                    return false
                }
            }

        } else {
            when (constantName) {
                Constant.BLOOD_PRESSURE -> {
                    val bpPerDay = ApiObject.instant.bloodPressurePerDay
                    if (bpPerDay.isNotEmpty()) {
                        count = bpPerDay.size
                        for (i in bpPerDay.indices) {
                            arrValueGraph1.add(bpPerDay[i].systolic.toFloat())
                            arrValueGraph2.add(bpPerDay[i].diastolic.toFloat())
                        }
                    }
                    return true
                }
                Constant.BLOOD_SUGAR_LEV -> {
                    val bsPerDay = ApiObject.instant.bloodSugarPerDay
                    if (bsPerDay.isNotEmpty()) {
                        count = bsPerDay.size
                        for (i in bsPerDay.indices) {
                            arrValueGraph1.add(bsPerDay[i].sugarLevel.toFloat())
                            arrValueGraph2.add(bsPerDay[i].hba1c.toFloat())
                        }
                    }
                    return true
                }
                Constant.KIDNEY_FILTRATION_RATE -> {
                    val girPerDay = ApiObject.instant.kidneyLevPerDay
                    if (girPerDay.isNotEmpty()) {
                        count = girPerDay.size
                        for (i in girPerDay.indices) {
                            arrValueGraph1.add(girPerDay[i].egfr.toFloat())
                            arrValueGraph2.add(girPerDay[i].cr.toFloat())
                        }
                    }
                    return true
                }
                Constant.WATER -> {
                    return true
                }
                else -> return false
            }
        }

    }


    @SuppressLint("SetTextI18n")
    fun createLayout() {

        arrChart = jsonObject!!.getJSONArray("graph")

        for (i in 0 until arrChart.length()) {

            val chartJSONObject = arrChart.getJSONObject(i)

            val linearLayout = LinearLayout(context)
            val textView = TextView(context)
            val lineChart = LineChart(context)
            val pieChart = PieChart(context)

            val paramsForLayout: LinearLayout.LayoutParams =
                if (jsonObject.getString("name") == Constant.WATER) {
                    LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, 1500
                    )
                } else {
                    LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, 1000
                    )
                }

            val paramsForTextView = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            )

            val paramsForChart = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1F
            )


            paramsForTextView.setMargins(0, 50, 0, 0)
            paramsForLayout.bottomMargin = 60
            paramsForChart.setMargins(0, 0, 0, 10)

            val view = View(context)
            view.layoutParams =
                LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1F)

            linearLayout.layoutParams = paramsForLayout
            textView.layoutParams = paramsForTextView

            linearLayout.orientation = LinearLayout.VERTICAL
            linearLayout.background = ContextCompat.getDrawable(context, R.drawable.white_card)
            linearLayout.elevation = 10F
            linearLayout.setPadding(30, 0, 30, 50)

            textView.text = chartJSONObject.getString("name")
            textView.gravity = Gravity.CENTER
            textView.setTextColor(ContextCompat.getColor(context, R.color.dimGray))


            if (jsonObject.getString("name") == Constant.WATER) {


                textView.setTextColor(ContextCompat.getColor(context, R.color.lightSkyBlue))
                linearLayout.addView(textView)

                linearLayout.addView(
                    createTextView(
                        "${ApiObject.instant.waterIn} ml",
                        ContextCompat.getColor(context, R.color.dimGray),
                        20F
                    )
                )

                pieChart.layoutParams = paramsForChart
                pieChartSetUp(pieChart)
                linearLayout.addView(pieChart)

                linearLayout.addView(
                    createTextView(
                        "${ApiObject.instant.waterPerDay} ml",
                        ContextCompat.getColor(context, R.color.dimGray),
                        20F
                    )
                )
                linearLayout.addView(
                    createTextView(
                        chartJSONObject.getString("namePerDay"),
                        ContextCompat.getColor(context, R.color.lightSkyBlue),
                        16F
                    )
                )

            } else {

                if (jsonObject.getString("name") == Constant.BMI) {
                    val viewBMI = LayoutInflater.from(context).inflate(R.layout.bmi_layout, null)
                    val paramForBmi = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    paramForBmi.setMargins(0, 0, 0, 10)
                    val bmiGauge = viewBMI.findViewById<HalfGauge>(R.id.bmiGauge)

                    val range = Range()
                    range.color = Color.parseColor("#41BDC9")
                    range.from = 13.4
                    range.to = 18.4

                    val range2 = Range()
                    range2.color = Color.parseColor("#40D0AE")
                    range2.from = 18.5
                    range2.to = 22.9

                    val range3 = Range()
                    range3.color = Color.parseColor("#FFC569")
                    range3.from = 23.0
                    range3.to = 24.9

                    val range4 = Range()
                    range4.color = Color.parseColor("#F69271")
                    range4.from = 25.0
                    range4.to = 29.9

                    val range5 = Range()
                    range5.color = Color.parseColor("#FC7977")
                    range5.from = 30.0
                    range5.to = 35.0

                    //add color ranges to gauge

                    bmiGauge.addRange(range)
                    bmiGauge.addRange(range2)
                    bmiGauge.addRange(range3)
                    bmiGauge.addRange(range4)
                    bmiGauge.addRange(range5)

                    //set min max and current value
                    bmiGauge.minValue = 13.4
                    bmiGauge.maxValue = 35.0
                    bmiGauge.value = 0.0

                    viewBMI.findViewById<TextView>(R.id.bmiSavButton).setOnClickListener {
                        val df2 = DecimalFormat("#.#")
                        val weightEditText = viewBMI.findViewById<EditText>(R.id.weightEditText)
                        val heightEditText = viewBMI.findViewById<EditText>(R.id.heightEditText)

                        when {
                            TextUtils.isEmpty(weightEditText.text) -> weightEditText.error = context.getString(R.string.checkFill)
                            TextUtils.isEmpty(heightEditText.text) -> heightEditText.error = context.getString(R.string.checkFill)
                            else -> {
                                val weight = weightEditText.text.toString().toFloat()
                                val height = heightEditText.text.toString().toFloat() / 100
                                val bmi = weight / (height * height)
                                bmiGauge.value = df2.format(bmi).toDouble()

                                val apiHandler = ApiHandler(context, null, null)
                                val id = ApiObject.instant.user!!.id
                                apiHandler.postBmi(id, bmiGauge.value)
                            }
                        }


                    }
                    viewBMI.layoutParams = paramForBmi

                    parentLayout!!.addView(viewBMI)

                }
                lineChart.layoutParams = paramsForChart
                lineChartSetUp(lineChart, chartJSONObject)
                linearLayout.addView(textView)
                linearLayout.addView(lineChart)
            }

            parentLayout!!.addView(linearLayout)
        }
        //-----------add explain chart table-------------
        if (jsonObject.getString("name") != Constant.WATER) {
            val explainChartTable = jsonObject.getJSONArray("explainChartTable")
            val explainGraphLayout = LinearLayout(context)
            val tableLayout = TableLayout(context)

            val paramsForExplainLayout = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            val paramsForTable = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )

            explainGraphLayout.setPadding(20, 20, 20, 20)
            explainGraphLayout.orientation = LinearLayout.VERTICAL
            explainGraphLayout.elevation = 10f
            explainGraphLayout.background = context.getDrawable(R.drawable.white_card)

            tableLayout.setPadding(10, 10, 10, 10)
            paramsForTable.bottomMargin = 50

            paramsForExplainLayout.setMargins(20, 20, 20, 20)
            explainGraphLayout.layoutParams = paramsForExplainLayout
            tableLayout.layoutParams = paramsForTable

            for (i in 0 until explainChartTable.length()) {
                val getRow = explainChartTable.getJSONArray(i)
                val tableRow = TableRow(context)
                if (i == 0) {
                    tableRow.setPadding(15, 5, 5, 15)
                } else {
                    tableRow.setPadding(15, 5, 5, 5)
                    val colors =
                        context.resources.obtainTypedArray(R.array.range_chart_arr).use { ta ->
                            IntArray(ta.length()) { ta.getColor(it, 0) }
                        }
                    tableRow.setBackgroundColor(colors[i - 1])
                }
                for (j in 0 until getRow.length()) {
                    val textView = TextView(context)
                    val paramsForTextView = TableRow.LayoutParams(
                        TableRow.LayoutParams.WRAP_CONTENT,
                        TableRow.LayoutParams.WRAP_CONTENT,
                        1f
                    )

                    textView.text = getRow.getString(j)
                    textView.layoutParams = paramsForTextView
                    tableRow.addView(textView)

                }
                tableLayout.addView(tableRow)
            }
            explainGraphLayout.addView(tableLayout)
            parentLayout!!.addView(explainGraphLayout)
        }

    }

    private fun createTextView(text: String, color: Int, textSize: Float1): TextView {

        val params = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )

        var textView = TextView(context)
        textView.layoutParams = params
        textView.gravity = Gravity.CENTER
        textView.text = text
        textView.textSize = textSize
        textView.setTextColor(color)
        return textView
    }

    fun lineChartSetUp(setChart: LineChart, graphObject: JSONObject) {

        val min = graphObject.getInt("min").toFloat()
        val max = graphObject.getInt("max").toFloat()

        setChart.setDrawGridBackground(false)
        setChart.setBorderColor(Color.BLACK)

        setChart.description.isEnabled = false
        setChart.setTouchEnabled(true)
        setChart.isDragEnabled = true
        setChart.setScaleEnabled(false)
        setChart.setPinchZoom(true)

        setChart.xAxis.axisMinimum = 0f
        setChart.xAxis.axisMaximum = (count + 2).toFloat()
        if (graphObject.getString("name") != Constant.BMI) {
            if (parentLayout != null) {
                val weekdays = arrayListOf("", "อา", "จ", "อ", "พ", "พฤ", "ศ", "ส", "")
                setChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
                setChart.xAxis.valueFormatter = object : IndexAxisValueFormatter(weekdays) {}
            }
        }else  setChart.xAxis.isEnabled = false


        val leftAxis = setChart.axisLeft
        leftAxis.axisMinimum = min
        leftAxis.axisMaximum = max

        setChart.axisLeft.setDrawLimitLinesBehindData(true)

        val rightAxis = setChart.axisRight
        rightAxis.isEnabled = false

        setChart.legend.isEnabled = false

        setChart.invalidate()

        setBackgroundChartColor(setChart, graphObject.getJSONArray("range"))

        setData(count, setChart, graphObject.getString("name"))


    }

    @TargetApi(Build.VERSION_CODES.M)
    fun setData(count: Int, chart: LineChart, chartName: String) {

        val selData = if (chartName == "ความดันโลหิตตัวล่าง(mmHg)") {
            arrValueGraph2
        } else {
            arrValueGraph1
        }

        if (selData.size != 0) {
            val entries = ArrayList<Entry>()
            for (i in 1..count) {
                val yVal = selData.get(i - 1)
                if (yVal != 0f) {
                    entries.add(Entry(i.toFloat(), yVal))
                }
            }

            Collections.sort(entries, EntryXComparator())

            val set1 = LineDataSet(entries, "DataSet 1")

            set1.lineWidth = 2f
            set1.circleRadius = 3.5f
            set1.circleHoleRadius = 3.5f
            set1.color = Color.parseColor("#ffffff")
            set1.setCircleColor(Color.parseColor("#ffffff"))
            set1.valueTextSize = 16f
            set1.valueTypeface = ResourcesCompat.getFont(context, R.font.baijamjuree)

            val data = LineData(set1)
            chart.data = data
        }


    }

    private fun setBackgroundChartColor(chart: LineChart, rangeArr: JSONArray) {

        val colors = intArrayOf(
            Color.parseColor("#41BDC9"), // blue
            Color.parseColor("#40D0AE"),//green
            Color.parseColor("#FFC569"), //yellow
            Color.parseColor("#F69271"), //orange
            Color.parseColor("#FC7977"), // red
            Color.rgb(206, 46, 73)  // red wine
        )

        for (i in 0 until rangeArr.length()) {
            var range = rangeArr.getJSONObject(i)
            var min = range.getInt("min")
            var max = range.getInt("max")
            var metricLine = min.toFloat()
            for (j in min..max) {
                val llRange = LimitLine(metricLine, "")
                llRange.lineColor = colors[i]
                llRange.lineWidth = 10f
                chart.axisLeft.addLimitLine(llRange)
                metricLine += 1f
            }
        }
    }

    fun pieChartSetUp(chart: PieChart) {

        chart.setUsePercentValues(true)
        chart.description.isEnabled = false
        chart.setExtraOffsets(5f, 10f, 5f, 5f)

        chart.dragDecelerationFrictionCoef = 0.95f

        chart.isDrawHoleEnabled = true
        chart.setHoleColor(Color.WHITE)

        chart.setTransparentCircleColor(Color.WHITE)
        chart.setTransparentCircleAlpha(110)

        chart.holeRadius = 45f
        chart.transparentCircleRadius = 60f

        chart.setDrawCenterText(true)

        chart.rotationAngle = 270f
        // enable rotation of the chart by touch
        chart.isRotationEnabled = false
        chart.isHighlightPerTapEnabled = true

        // chart.setUnit(" €");
        // chart.setDrawUnitsInChart(true);

        chart.animateY(1400, Easing.EaseInOutQuad)
        // chart.spin(2000, 0, 360);

        val l = chart.legend
        l.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        l.orientation = Legend.LegendOrientation.VERTICAL
        l.setDrawInside(false)
        l.xEntrySpace = 7f
        l.yEntrySpace = 0f
        l.yOffset = 0f

        // entry label styling
        chart.setEntryLabelColor(Color.WHITE)
        chart.setEntryLabelTextSize(11f)

        setDataPie(chart)
    }

    @TargetApi(Build.VERSION_CODES.M)
    fun setDataPie(chart: PieChart) {

        val waterIn = ApiObject.instant.waterIn
        val waterPerDay = ApiObject.instant.waterPerDay

        if (waterPerDay != 0) {
            val waterInPercent = (waterIn * 100 / waterPerDay).toFloat()
            val waterPerDayPercent = (100 - waterInPercent)

            val entries = ArrayList<PieEntry>()

            entries.add(PieEntry(waterInPercent, waterInPercent)) // in
            entries.add(PieEntry(waterPerDayPercent, waterPerDayPercent)) // per day

            val dataSet = PieDataSet(entries, "")

            dataSet.setDrawIcons(false)

            dataSet.sliceSpace = 3f
            dataSet.iconsOffset = MPPointF(0f, 40f)
            dataSet.selectionShift = 5f

            // add a lot of colors

            val colors = ArrayList<Int>()
            colors.add(Color.parseColor("#93D0FE"))
            colors.add(Color.parseColor("#D6D6D6"))

            dataSet.colors = colors

            val data = PieData(dataSet)

            data.setValueFormatter(PercentFormatter(chart))
            data.setValueTextSize(0f)
            data.setValueTextColor(context.getColor(R.color.mariner))
            chart.data = data

            chart.highlightValues(null)

            chart.invalidate()
        }


    }
}
