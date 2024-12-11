package com.kashif.common.charts.internal.common.model

import kotlin.jvm.JvmName

internal class ChartData(val data: List<Pair<String,Double>>) {
    val labels: List<String> get() = data.map { it.first }
    val points: List<Double> get() = data.map { it.second }
}



//@JvmName("toDoubleChartData")
//internal fun List<Double>.toChartData(
//    prefix: String = "",
//    postfix: String = ""
//): ChartData = ChartData(this.map { "${prefix}${it}${postfix}" to it })

@JvmName("toFloatChartData")
internal fun List<Float>.toChartData(
    prefix: String = "",
    postfix: String = ""
): ChartData = ChartData(this.map { "${prefix}${it}${postfix}" to it.toDouble() })


//@JvmName("toFloatChartData")
//internal fun List<Float>.toChartData(
//    prefix: String = "",
//    postfix: String = ""
//): ChartData {
//    val dataPairs = this.map { value ->
//        "${prefix}${value}${postfix}" to value.toDouble()
//    }
//    return ChartData(mutableListOf()).also { chartData ->
//        dataPairs.forEach { pair ->
//            chartData.addData(pair)
//        }
//    }
//}

//@JvmName("toStringChartData")
//internal fun List<String>.toChartData(
//    prefix: String = "",
//    postfix: String = ""
//): ChartData = ChartData(this.map { "${prefix}${it}${postfix}" to it.toDouble() })
//
//@JvmName("toIntChartData")
//internal fun List<Int>.toChartData(
//    prefix: String = "",
//    postfix: String = ""
//): ChartData = ChartData(this.map { "${prefix}${it}${postfix}" to it.toDouble() })
