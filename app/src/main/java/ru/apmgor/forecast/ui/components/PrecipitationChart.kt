package ru.apmgor.forecast.ui.components

import android.content.Context
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import ru.apmgor.forecast.R
import ru.apmgor.forecast.data.models.*
import ru.apmgor.forecast.ui.theme.ForecastTheme
import kotlin.random.Random

@Composable
fun PrecipitationChart(
    units: List<Units>,
    minutely: List<MinutelyForecast>,
    modifier: Modifier = Modifier
) {
    val precipUnit = stringArrayResource(id = R.array.precip_units)
    val context = LocalContext.current
    val isDarkMode = isSystemInDarkTheme()
    val lineData = remember(minutely) {
        val entries = minutely.mapIndexed { index, elem ->
            Entry(
                index.toFloat(),
                (units[3].revert(elem.precipitation) as Double).toFloat()
            )
        }
        val dataSet = LineDataSet(
            entries,
            context.getString(
                R.string.chart_legend_name,
                units[3].getResName(precipUnit)
            )
        )
        dataSet.configureDataSet(context)

        LineData(dataSet)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(150.dp)
            .padding(bottom = 8.dp, start = 8.dp, end = 8.dp)
    ) {
        AndroidView(
            factory = { context ->  
                LineChart(context).apply {
                    val colorWhite = ContextCompat.getColor(context, R.color.transparent_white)
                    val colorBlack = ContextCompat.getColor(context, R.color.transparent_black)
                    setTouchEnabled(false)
                    axisRight.isEnabled = false
                    axisLeft.configureLeftYAxis(isDarkMode,colorWhite,colorBlack)
                    xAxis.configureXAxis(isDarkMode,context,colorWhite,colorBlack)
                    description.isEnabled = false
                    legend.configureLegend(isDarkMode,colorWhite,colorBlack)
                    this.invalidate()
                }
            },
            update = { it.data = lineData },
            modifier = Modifier.padding(all = 8.dp)
        )
    }
}

private fun LineDataSet.configureDataSet(context: Context) {
    setDrawValues(false)
    setDrawCircles(false)
    mode = LineDataSet.Mode.CUBIC_BEZIER
    setDrawFilled(true)
    fillDrawable = context.getDrawable(R.drawable.gradient_chart)
    color = ContextCompat.getColor(context,R.color.teal_700)
    lineWidth = 0.001f
}

private fun YAxis.configureLeftYAxis(
    isDarkMode: Boolean,
    colorWhite: Int,
    colorBlack: Int
) {
    setDrawAxisLine(false)
    setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART)
    textColor = if (isDarkMode) {
        colorWhite
    } else {
        colorBlack
    }
    yOffset = -7f
    enableGridDashedLine(16f,24f,0f)
    axisMinimum = 0f
    setLabelCount(3,true)
}

private fun XAxis.configureXAxis(
    isDarkMode: Boolean,
    context: Context,
    colorWhite: Int,
    colorBlack: Int
) {
    setDrawGridLines(false)
    position = XAxis.XAxisPosition.BOTTOM
    setDrawAxisLine(false)
    textColor = if (isDarkMode) {
        colorWhite
    } else {
        colorBlack
    }
    axisMinimum = -10f
    isGranularityEnabled = true
    granularity = 15f
    valueFormatter = object : ValueFormatter() {
        val arrayLabels = context.resources.getStringArray(R.array.cart_x_labels)
        override fun getFormattedValue(value: Float): String {
            return arrayLabels[value.toInt()/15]
        }
    }
}

private fun Legend.configureLegend(
    isDarkMode: Boolean,
    colorWhite: Int,
    colorBlack: Int
) {
    isEnabled = true
    textColor = if (isDarkMode) {
        colorWhite
    } else {
        colorBlack
    }
    yOffset = -4f
    verticalAlignment = Legend.LegendVerticalAlignment.TOP
    horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
    orientation = Legend.LegendOrientation.HORIZONTAL
    setDrawInside(false)
}




@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PrecipitationChartPreview() {
    ForecastTheme {
        PrecipitationChart(
            listOf(
                TempUnits(),
                WindSpeedUnits(),
                PressureUnits(),
                PrecipUnits(),
                DistUnits(),
                TimeUnits()
            ),
            List(61) { Point(it,Random.nextFloat()) }
                .map {
                    MinutelyForecast(it.x.toLong(),it.y.toDouble())
                }
        )
    }
}
data class Point(val x: Int, val y: Float)