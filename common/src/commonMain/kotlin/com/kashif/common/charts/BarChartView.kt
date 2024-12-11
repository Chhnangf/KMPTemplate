package com.kashif.common.charts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.unit.dp
import com.kashif.common.charts.internal.NO_SELECTION
import com.kashif.common.charts.internal.TestTags
import com.kashif.common.charts.internal.barchart.BarChart
import com.kashif.common.charts.internal.common.model.composable.ChartView
import com.kashif.common.charts.internal.common.model.validateBarData
import com.kashif.common.charts.model.ChartDataSet
import com.kashif.common.charts.style.BarChartDefaults
import com.kashif.common.charts.style.BarChartStyle
import com.kashif.common.charts.style.ChartViewStyle

/**
 * A composable function that displays a Bar Chart.
 *
 * @param dataSet The data set to be displayed in the chart.
 * @param style The style to be applied to the chart. If not provided, the default style will be used.
 */

@Composable
fun BarChartView(
    dataSet: ChartDataSet,
    style: BarChartStyle = BarChartDefaults.style()
) {
    val errors by remember {
        mutableStateOf(
            validateBarData(
                data = dataSet.data.item
            )
        )
    }
    println("ChartDataSet.dataSet = $dataSet")
    if (errors.isEmpty()) {
        ChartContent(dataSet = dataSet, style = style)
    } else {
        ChartErrors(chartViewStyle = style.chartViewStyle, errors = errors)
    }
}

@Composable
private fun ChartContent(
    dataSet: ChartDataSet,
    style: BarChartStyle
) {
    var title by remember { mutableStateOf(dataSet.data.label) }
    Text(
            modifier = style.chartViewStyle.modifierTopTitle
                .testTag(TestTags.CHART_TITLE),
            text = title,
            //style = style.chartViewStyle.styleTitle
        )
    ChartView(chartViewsStyle = style.chartViewStyle) {
        BarChart(chartData = dataSet.data.item, style = style) {
            title = when (it) {
                NO_SELECTION -> dataSet.data.label
                else -> dataSet.data.item.labels[it]
            }
        }
    }
}

@Composable
internal fun ChartErrors(chartViewStyle: ChartViewStyle, errors: List<String>) {
        Column(
            modifier = chartViewStyle.modifierMain
                .padding(15.dp)
                .testTag(TestTags.CHART_ERROR)
        ) {
            errors.forEach { error ->
                println("ChartErrors = $error")
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = MaterialTheme.colors.error,
                            shape = RoundedCornerShape(5.dp)
                        )
                        .padding(5.dp),
                    text = "$error\n",
                    color = MaterialTheme.colors.error
                )
                Spacer(modifier = Modifier.height(5.dp))
            }

    }
}

@Stable
fun Modifier.testTag(tag: String) = semantics(
    properties = {
        testTag = tag
    }
)
