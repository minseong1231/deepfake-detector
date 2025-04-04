package com.weit2nd.deepfakedetector.data.util

import kotlin.math.exp

fun FloatArray.softmax(): FloatArray {
    val maxLogit = this.maxOrNull() ?: 0f
    val exps = this.map { exp((it - maxLogit).toDouble()) }
    val sumExp = exps.sum()
    return exps.map {
        (it / sumExp).toFloat()
    }.toFloatArray()
}
