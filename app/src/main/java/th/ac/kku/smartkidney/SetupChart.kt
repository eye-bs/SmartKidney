package th.ac.kku.smartkidney

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.EntryXComparator
import com.github.mikephil.charting.utils.MPPointF
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

class SetupChart(val jsonObject: JSONObject, val context: Context , val parentLayout: LinearLayout){

    val colors = arrayOf("#00C1C1","#13D967","#FCCA01","#E64242","#EA5388","#660000")
    lateinit var arrChart: JSONArray
    
    @SuppressLint("SetTextI18n")
    fun createLayout(){

        val rangeName = jsonObject.getJSONArray("rangeName")
        arrChart = jsonObject.getJSONArray("graph")



        for (i in 0 until arrChart.length()) {

            var chartJSONObject = arrChart.getJSONObject(i)

            val linearLayout = LinearLayout(context)
            val textView = TextView(context)
            val lineChart = LineChart(context)
            val pieChart = PieChart(context)
            val rangeLayout = LinearLayout(context)

           var paramsForLayout: LinearLayout.LayoutParams = if(jsonObject.getString("name").equals(Constant.WATER)){
               LinearLayout.LayoutParams(
                   LinearLayout.LayoutParams.MATCH_PARENT,  1200
               )
           }else{
               LinearLayout.LayoutParams(
                   LinearLayout.LayoutParams.MATCH_PARENT,  1000
               )
           }

            var paramsForTextView = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            )

            var paramsForChart = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1F
            )

            var paramsForRate = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            paramsForTextView.setMargins(0, 50, 0, 0)
            paramsForLayout.setMargins(50,50,50,50)
            paramsForRate.setMargins(0, 50, 50, 20)
            paramsForChart.setMargins(0,0,0,10)

            val view = View(context)
            view.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1F)

            linearLayout.layoutParams = paramsForLayout
            textView.layoutParams = paramsForTextView
            rangeLayout.layoutParams = paramsForRate



            linearLayout.orientation = LinearLayout.VERTICAL
            linearLayout.background = ContextCompat.getDrawable(context, R.drawable.white_card)
            linearLayout.elevation = 10F
            rangeLayout.orientation = LinearLayout.HORIZONTAL

            rangeLayout.addView(view)

            textView.text = chartJSONObject.getString("name")
            textView.gravity = Gravity.CENTER
            textView.setTextColor(ContextCompat.getColor(context,R.color.dimGray))

            for (i in 0 until rangeName.length()) {
                var rangeText = TextView(context)
                rangeText.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                rangeText.text = rangeName.getString(i)
                rangeText.textSize = 12f
                rangeText.setTextColor(Color.parseColor(colors[i]))

                rangeLayout.addView(rangeText)
            }



            if(jsonObject.getString("name").equals(Constant.WATER)){

                val waterIn = 2000
                val waterPerDay = 2300

                textView.setTextColor(ContextCompat.getColor(context,R.color.lightSkyBlue))
                linearLayout.addView(textView)

                linearLayout.addView(createTextView("$waterIn ml",ContextCompat.getColor(context,R.color.dimGray),20F))

                pieChart.layoutParams = paramsForChart
                PieChartSetUp(pieChart)
                linearLayout.addView(pieChart)

                linearLayout.addView(createTextView("$waterPerDay ml",ContextCompat.getColor(context,R.color.dimGray),20F))
                linearLayout.addView(createTextView(chartJSONObject.getString("namePerDay"),ContextCompat.getColor(context,R.color.lightSkyBlue),16F))



            }else{
                lineChart.layoutParams = paramsForChart
                lineChartSetUp(lineChart ,chartJSONObject)
                linearLayout.addView(textView)
                linearLayout.addView(rangeLayout)
                linearLayout.addView(lineChart)
            }

            parentLayout.addView(linearLayout)
        }
    }

    private fun createTextView(text: String, color: Int,textSize: Float): TextView{

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

    private fun lineChartSetUp(setChart: LineChart, graphObject: JSONObject){

        val min =  graphObject.getInt("min").toFloat()
        val max = graphObject.getInt("max").toFloat()

        setChart.setDrawGridBackground(false)
        setChart.setBorderColor(Color.BLACK)

        setChart.description.isEnabled = false
        setChart.setTouchEnabled(true)
        setChart.isDragEnabled = true
        setChart.setScaleEnabled(false)
        setChart.setPinchZoom(true)

        setChart.xAxis.isEnabled = false

        val leftAxis = setChart.axisLeft
        leftAxis.axisMinimum = min
        leftAxis.axisMaximum = max

        setChart.axisLeft.setDrawLimitLinesBehindData(true)

        val rightAxis = setChart.axisRight
        rightAxis.isEnabled = false

        setChart.legend.isEnabled = false

        setChart.invalidate()

        setBackgroundChartColor(setChart , graphObject.getJSONArray("range"))

        setData(5,min,max,setChart)


    }

    private fun setData(count: Int , min: Float,max: Float , chart: LineChart) {

        val entries = ArrayList<Entry>()
        for (i in 0 until count) {
            val xVal = (min.toInt()..max.toInt()).random()
            val yVal = (min.toInt()..max.toInt()).random()
            entries.add(Entry(xVal.toFloat(), yVal.toFloat()))
        }

        Collections.sort(entries, EntryXComparator())

        val set1 = LineDataSet(entries, "DataSet 1")

        set1.lineWidth = 1.5f
        set1.circleRadius = 4f
        set1.circleHoleRadius = 2.5f
        set1.color = Color.BLACK
        set1.setCircleColor(Color.BLACK)
        set1.highLightColor = Color.BLACK

        val data = LineData(set1)

        chart.data = data
    }

    private fun setBackgroundChartColor(chart: LineChart, rangeArr: JSONArray){

        val colors = intArrayOf(
            Color.rgb(181 , 227, 240),
            Color.rgb(193 , 227, 202),
            Color.rgb(246 , 234, 179),
            Color.rgb(248 , 212, 188),
            Color.rgb(239 , 194, 210),
            Color.rgb(248 , 178, 173)

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

    fun PieChartSetUp(chart: PieChart){

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

        val l = chart.getLegend()
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

        setDataPie(2,50F,chart)
    }

    private fun setDataPie(count: Int, range: Float,chart: PieChart) {

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

        entries.add(PieEntry(70F,70F))
        entries.add(PieEntry(30F,30F))

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