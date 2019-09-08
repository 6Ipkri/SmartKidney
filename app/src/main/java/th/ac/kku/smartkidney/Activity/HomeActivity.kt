package th.ac.kku.smartkidney

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.EntryXComparator
import kotlinx.android.synthetic.main.activity_home.*
import java.util.*

class HomeActivity : AppCompatActivity(), OnChartValueSelectedListener {

    lateinit var chart: LineChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        setUpChart(pressureChart)
        setUpChart(sugarChart)
        setUpChart(kidneyChart)

        setData(6, 5.0F , pressureChart)
        setData(6, 5.0F , sugarChart)
        setData(6, 5.0F , kidneyChart)

        alarmLayout.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, AlarmActivity::class.java)
            startActivity(intent)
        })

        healthFormLayout.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, HealthFormActivity::class.java)
            startActivity(intent)
        })

//        var readjson = ReadJSON(this)
//        var obj = readjson.getJSONObject(Constant.BLOOD_PRESSURE)
//        var rate = obj!!.getJSONArray("rangeName")
//        Log.wtf(Constant.TAG , "rate${rate.getString(0)}")
    }

    fun setUpChart(setChart: LineChart){

        chart = setChart

        chart.setOnChartValueSelectedListener(this)
        chart.setDrawGridBackground(false)
        chart.setBorderColor(Color.BLACK)

        chart.description.isEnabled = false
        chart.setTouchEnabled(true)
        chart.isDragEnabled = true
        chart.setScaleEnabled(true)
        chart.setPinchZoom(true)

        chart.xAxis.isEnabled = false
//        xl.setAvoidFirstLastClipping(true)
//        xl.position = XAxis.XAxisPosition.BOTTOM
//        xl.axisMinimum = 0f

        val leftAxis = chart.axisLeft
        leftAxis.axisMinimum = 0f

        chart.axisLeft.setDrawLimitLinesBehindData(true)

        val rightAxis = chart.axisRight
        rightAxis.isEnabled = false

        chart.legend.isEnabled = false

        chart.invalidate()

        setBackgroundChartColor(chart)


    }

    private fun setData(count: Int, range: Float , chart: LineChart) {

        val entries = ArrayList<Entry>()
            for (i in 0 until count) {
                val xVal = (i+1).toFloat()
                val yVal = i.toFloat()
                entries.add(Entry(xVal, yVal))
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


    override fun onNothingSelected() {}

    override fun onValueSelected(e: Entry?, h: Highlight?) {
          Log.i("VAL SELECTED",
              "Value: " + e?.y + ", xIndex: " + e?.x
              + ", DataSet index: " + h?.dataSetIndex
          )
    }

    fun setBackgroundChartColor(chart: LineChart){

        val colors = intArrayOf(
            Color.rgb(181 , 227, 240),
            Color.rgb(193 , 227, 202),
            Color.rgb(246 , 234, 179),
            Color.rgb(239 , 194, 210),
            Color.rgb(247 , 199, 198),
            Color.rgb(145 , 168, 132)
        )

        var metricLine = 0f

        for (i in 0..5) {
            for (j in 0..9) {
                val llRange = LimitLine(metricLine, "")
                llRange.lineColor = colors[i]
                llRange.lineWidth = 10f
                chart.axisLeft.addLimitLine(llRange)
                metricLine += 0.1f
            }
        }
    }


}
