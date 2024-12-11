package com.kashif.common.charts.internal.barchart

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import com.kashif.common.charts.internal.AnimationSpec
import com.kashif.common.charts.internal.DEFAULT_SCALE
import com.kashif.common.charts.internal.MAX_SCALE
import com.kashif.common.charts.internal.NO_SELECTION
import com.kashif.common.charts.internal.TestTags
import com.kashif.common.charts.internal.common.model.ChartData
import com.kashif.common.charts.style.BarChartStyle
import com.kashif.common.charts.testTag
import kotlin.math.abs


@OptIn(ExperimentalTextApi::class)
@Composable
internal fun BarChart(
    chartData: ChartData,
    style: BarChartStyle,
    onValueChanged: (Int) -> Unit = {}
) {
    val barColor = style.barColor
    var progress by remember {
        mutableStateOf<List<Animatable<Float, AnimationVector1D>>>(chartData.points.map { value ->
            Animatable(0f)
        })
    }

    val maxValue = remember { chartData.points.max() }
    val minValue = remember { chartData.points.min() }
    var selectedIndex by remember { mutableStateOf(NO_SELECTION) }
    val textMeasurer = rememberTextMeasurer()
    // When chartData value was changed then make action
    // 设定 progress 的初始值

    // 保险措施，避免索引溢出边界导致崩溃
    chartData.points.forEachIndexed { index, value ->

        LaunchedEffect(chartData.points) {
            progress[index].animateTo(
                targetValue = abs(value).toFloat(),
                animationSpec = AnimationSpec.barChart(index)
            )
        }
    }

    Canvas(modifier = style.modifier
        .testTag(TestTags.BAR_CHART)
        .pointerInput(Unit) {
            detectDragGestures(
                onDrag = { change, _ ->
                    selectedIndex =
                        getSelectedIndex(
                            position = change.position,
                            dataSize = chartData.points.count(),
                            canvasSize = size
                        )
                    onValueChanged(selectedIndex)
                    change.consume()
                },
                onDragEnd = {
                    selectedIndex = NO_SELECTION
                    onValueChanged(NO_SELECTION)
                }
            )
        }, onDraw = {
        drawBars(
            style = style,
            size = size,
            chartData = chartData,
            progress = progress,
            selectedIndex = selectedIndex,
            barColor = barColor,
            maxValue = maxValue,
            minValue = minValue,
            textMeasurer
        )
    })


}


@OptIn(ExperimentalTextApi::class)
private fun DrawScope.drawBars(
    style: BarChartStyle,
    size: Size,
    chartData: ChartData,
    progress: List<Animatable<Float, AnimationVector1D>>,
    selectedIndex: Int,
    barColor: Color,
    maxValue: Double,
    minValue: Double,
    textMeasurer: TextMeasurer
) {
    val baselineY = size.height * (maxValue / (maxValue - minValue))
    val dataSize = chartData.points.size

    // 绘制Y轴刻度线
    drawLine(
        color = Color.Gray, // X轴刻度线的颜色，这里使用灰色作为示例
        start = Offset(x = 0f, y = 0f), // X轴刻度线的起点
        end = Offset(x = 0f, y = size.height), // X轴刻度线的终点
        strokeWidth = 1f // X轴刻度线的宽度
    )

    // 绘制X轴刻度线
    drawLine(
        color = Color.Gray, // X轴刻度线的颜色，这里使用灰色作为示例
        start = Offset(x = 0f, y = size.height), // X轴刻度线的起点
        end = Offset(x = size.width, y = size.height), // X轴刻度线的终点
        strokeWidth = 1f // X轴刻度线的宽度
    )

    chartData.points.forEachIndexed { index, value ->

        val spacing = style.space.toPx()
        val barWidth = (size.width - spacing * (dataSize - 1)) / dataSize
        val barWidth2 = size.width / dataSize - spacing

        val selectedBarScale = if (index == selectedIndex) MAX_SCALE else DEFAULT_SCALE
        val finalBarHeight =
            size.height * selectedBarScale * (abs(value) / (maxValue - minValue)) / 100


        val barHeight = if (value.toInt() != 0)
            lerp(0f, finalBarHeight.toFloat(), progress[index].value)
        else
            size.height / 2 * 0.1f

        val top = if (value >= 0) baselineY - barHeight else baselineY
        val left = (barWidth2 + spacing) * index- 0.1f

        drawRect(
            color = if (value.toInt() != 0) barColor else Color.Red,
            topLeft = Offset(x = left, y = top.toFloat()),
            size = Size(
                width = barWidth2 * selectedBarScale,
                height = barHeight
            )
        )
        // 动态计算文本大小
        val dynamicTextSize = maxOf(5, (barWidth2 * 0.1f).toInt()) // 基于条形宽度的百分比计算文本大小
        // 测量文本
        val text = "${index + 1}" // 要绘制的文本
        val textLayoutResult = textMeasurer.measure(
            text = text,
            style = TextStyle(fontSize = dynamicTextSize.sp),
        )




        drawText(
            textLayoutResult = textLayoutResult,
            color = Color.Black, // 使用条形相同的颜色
            topLeft = Offset(x = left + barWidth2 / 2, y = size.height), // 居中对齐文本
            alpha = 1f // 不透明度
        )

    }

}

internal fun getSelectedIndex(position: Offset, dataSize: Int, canvasSize: IntSize): Int {
    val barWidth = canvasSize.width / dataSize
    val index = (position.x / (barWidth)).toInt()
    return index.coerceIn(0, dataSize - 1)
}
