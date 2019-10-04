package th.ac.kku.smartkidney

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.content.res.use
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
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.Float as Float1

@Suppress("NAME_SHADOWING")
class SetupChart(val jsonObject: JSONObject, val context: Context, val parentLayout: LinearLayout? ) {

    lateinit var arrChart: JSONArray

    fun setValueFromApi(){

    }

    @SuppressLint("SetTextI18n")
    fun createLayout() {

        arrChart = jsonObject.getJSONArray("graph")

        for (i in 0 until arrChart.length()) {

            val chartJSONObject = arrChart.getJSONObject(i)

            val linearLayout = LinearLayout(context)
            val textView = TextView(context)
            val lineChart = LineChart(context)
            val pieChart = PieChart(context)

            val paramsForLayout: LinearLayout.LayoutParams =
                if (jsonObject.getString("name").equals(Constant.WATER)) {
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

                val waterIn = 2000
                val waterPerDay = 2300

                textView.setTextColor(ContextCompat.getColor(context, R.color.lightSkyBlue))
                linearLayout.addView(textView)

                linearLayout.addView(
                    createTextView(
                        "$waterIn ml",
                        ContextCompat.getColor(context, R.color.dimGray),
                        20F
                    )
                )

                pieChart.layoutParams = paramsForChart
                PieChartSetUp(pieChart)
                linearLayout.addView(pieChart)

                linearLayout.addView(
                    createTextView(
                        "$waterPerDay ml",
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

            tableLayout.setPadding(10,10,10,10)
            paramsForTable.bottomMargin = 50

            paramsForExplainLayout.setMargins(20, 20, 20, 20)
            explainGraphLayout.layoutParams = paramsForExplainLayout
            tableLayout.layoutParams = paramsForTable

            for (i in 0 until explainChartTable.length()) {
                val getRow = explainChartTable.getJSONArray(i)
                val tableRow = TableRow(context)
                if(i==0){
                    tableRow.setPadding(15, 5, 5, 15)
                }else{
                    tableRow.setPadding(15, 5, 5, 5)
                    val colors = context.resources.obtainTypedArray(R.array.range_chart_arr).use { ta ->
                        IntArray(ta.length()) { ta.getColor(it, 0) }
                    }
                    tableRow.setBackgroundColor(colors[i-1])
                }
                for (j in 0 until getRow.length()) {
                    val textView = TextView(context)
                    val paramsForTextView = TableRow.LayoutParams( TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1f)

                    textView.text = getRow.getString(j)
                    textView.textSize = 14f
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
        setChart.xAxis.axisMaximum = 8f
        if (parentLayout != null){
            val weekdays = arrayListOf<String>("", "อา", "จ", "อ", "พ", "พฤ", "ศ", "ส", "")
            setChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
            setChart.xAxis.valueFormatter = object : IndexAxisValueFormatter(weekdays) {}
        }else{
            setChart.xAxis.isEnabled = false
        }


        val leftAxis = setChart.axisLeft
        leftAxis.axisMinimum = min
        leftAxis.axisMaximum = max

        setChart.axisLeft.setDrawLimitLinesBehindData(true)

        val rightAxis = setChart.axisRight
        rightAxis.isEnabled = false

        setChart.legend.isEnabled = false

        setChart.invalidate()

        setBackgroundChartColor(setChart, graphObject.getJSONArray("range"))

        setData(7, min, max, setChart)


    }

    @TargetApi(Build.VERSION_CODES.M)
    fun setData(count: Int, min: Float1, max: Float1, chart: LineChart) {

        val entries = ArrayList<Entry>()

        for (i in 1..count) {
            val yVal = (min.toInt()..max.toInt()).random()
            entries.add(Entry(i.toFloat(), yVal.toFloat()))
        }

        Collections.sort(entries, EntryXComparator())

        val set1 = LineDataSet(entries, "DataSet 1")

        set1.lineWidth = 2f
        set1.circleRadius = 3.5f
        set1.circleHoleRadius = 3.5f
        set1.color = Color.parseColor("#3B5998")
        set1.setCircleColor(Color.parseColor("#3B5998"))

        val data = LineData(set1)

        chart.data = data
    }

    fun setBackgroundChartColor(chart: LineChart, rangeArr: JSONArray) {

        val colors = intArrayOf(
            Color.rgb(181, 227, 240),
            Color.rgb(193, 227, 202),
            Color.rgb(246, 234, 179),
            Color.rgb(248, 212, 188),
            Color.rgb(239, 194, 210),
            Color.rgb(248, 178, 173)
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

    fun PieChartSetUp(chart: PieChart) {

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
        chart.setEntryLabelTextSize(12f)

        setDataPie(2, 50F, chart)
    }

    @TargetApi(Build.VERSION_CODES.M)
    fun setDataPie(count: Int, range: Float1, chart: PieChart) {

        val entries = ArrayList<PieEntry>()

        entries.add(PieEntry(70F, 70F))
        entries.add(PieEntry(30F, 30F))

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
