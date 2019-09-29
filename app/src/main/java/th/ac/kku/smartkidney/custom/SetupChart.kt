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
import androidx.core.view.marginBottom
import androidx.core.view.marginTop
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
import kotlin.Float as Float1

@Suppress("NAME_SHADOWING")
class SetupChart(val jsonObject: JSONObject, val context: Context, val parentLayout: LinearLayout) {

    lateinit var arrChart: JSONArray

    @SuppressLint("SetTextI18n")
    fun createLayout() {

        arrChart = jsonObject.getJSONArray("graph")

        for (i in 0 until arrChart.length()) {

            val chartJSONObject = arrChart.getJSONObject(i)

            val linearLayout = LinearLayout(context)
            val textView = TextView(context)
            val lineChart = LineChart(context)
            val pieChart = PieChart(context)

            val paramsForLayout: LinearLayout.LayoutParams = if (jsonObject.getString("name").equals(Constant.WATER)) {
                LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, 1200
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
            view.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1F)

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

            parentLayout.addView(linearLayout)
        }
        //-----------add explain chart table-------------
        val explainChartTable = jsonObject.getJSONArray("explainChartTable")
        val explainGraphLayout = LinearLayout(context)
        val tableLayout = TableLayout(context)

        val paramsForExplainLayout = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        val paramsForTable = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)

        explainGraphLayout.setPadding(20,20,20,20)
        explainGraphLayout.orientation = LinearLayout.VERTICAL
        explainGraphLayout.elevation = 10f
        explainGraphLayout.background = context.getDrawable(R.drawable.white_card)

        paramsForExplainLayout.setMargins(20,20,20,20)
        explainGraphLayout.layoutParams = paramsForExplainLayout
        tableLayout.layoutParams = paramsForTable

        for(i in 0 until explainChartTable.length()){
            val getRow = explainChartTable.getJSONArray(i)
            val tableRow = TableRow(context)
            tableRow.setPadding(5,5,5,5)
            for (j in 0 until getRow.length()){
                val textView = TextView(context)
                var paramsForTextView:LinearLayout.LayoutParams = if(i == 0 && j == 2){
                    LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT,1f)
                }else{
                    LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT,2f)
                }
                Log.wtf(Constant.TAG, getRow.getString(j))
                textView.text = getRow.getString(j)
                textView.gravity = Gravity.CENTER
                textView.layoutParams = paramsForTextView
                tableRow.addView(textView)

            }
            tableLayout.addView(tableRow)
        }
        explainGraphLayout.addView(tableLayout)
        parentLayout.addView(explainGraphLayout)

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

    private fun lineChartSetUp(setChart: LineChart, graphObject: JSONObject) {

        val min = graphObject.getInt("min").toFloat()
        val max = graphObject.getInt("max").toFloat()

        setChart.setDrawGridBackground(false)
        setChart.setBorderColor(Color.BLACK)

        setChart.description.isEnabled = false
        setChart.setTouchEnabled(true)
        setChart.isDragEnabled = true
        setChart.setScaleEnabled(false)
        setChart.setPinchZoom(true)

        val weekdays = arrayListOf<String>("", "อา", "จ", "อ", "พ", "พฤ", "ศ", "ส", "")

        setChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        setChart.xAxis.axisMinimum = 0f
        setChart.xAxis.axisMaximum = 8f
        setChart.xAxis.valueFormatter = object : IndexAxisValueFormatter(weekdays) {}

        val leftAxis = setChart.axisLeft
        leftAxis.axisMinimum = min
        leftAxis.axisMaximum = max

        setChart.axisLeft.setDrawLimitLinesBehindData(true)

        val rightAxis = setChart.axisRight
        rightAxis.isEnabled = false

        setChart.legend.isEnabled = false

        setChart.invalidate()

        setBackgroundChartColor(setChart, graphObject.getJSONArray("range"))

        setData(context, 7, min, max, setChart)


    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun setData(context: Context, count: Int, min: Float1, max: Float1, chart: LineChart) {

        val entries = ArrayList<Entry>()

        val colors = ArrayList<Int>()

        for (i in 1..count) {
            val yVal = (min.toInt()..max.toInt()).random()
            entries.add(Entry(i.toFloat(), yVal.toFloat()))

//            if ( yVal <= 50 )
//                colors.add( context.getColor( R.color.lightSkyBlue ) )
//            else if (yVal in 51..100)
//                colors.add( context.getColor( R.color.hippie_green ) )
//            else if (yVal in 101..199)
//                colors.add( context.getColor( R.color.carnation_pink ) )
//            else if (yVal in 200..299)
//                colors.add( context.getColor( R.color.red_orange ) )
//            else
//                colors.add( context.getColor( R.color.paleVioletRed ) )

        }

        Collections.sort(entries, EntryXComparator())

        val set1 = LineDataSet(entries, "DataSet 1")

        set1.lineWidth = 2f
        set1.circleRadius = 5f
        set1.circleHoleRadius = 5f
        set1.color = Color.parseColor("#3B5998")
        set1.setCircleColor(Color.parseColor("#3B5998"))

        val data = LineData(set1)

        chart.data = data
    }

    private fun setBackgroundChartColor(chart: LineChart, rangeArr: JSONArray) {

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

        chart.holeRadius = 58f
        chart.transparentCircleRadius = 61f

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

    private fun setDataPie(count: Int, range: Float1, chart: PieChart) {

        val entries = ArrayList<PieEntry>()

        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.
//        for (i in 0 until count) {
//            entries.add(
//                PieEntry(
//                   70F,
//                    30F
//                )
//            )
//        }

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
        //dataSet.setSelectionShift(0f);

        val data = PieData(dataSet)
        data.setValueFormatter(PercentFormatter(chart))
        data.setValueTextSize(11f)
        data.setValueTextColor(Color.WHITE)
        chart.data = data

        // undo all highlights
        chart.highlightValues(null)

        chart.invalidate()
    }
}
