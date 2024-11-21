package org.dinges.heatequation

import kotlin.system.measureTimeMillis

fun main() {

    val spatialStep = 0.1
    val totalTime = 1.0

    val t = { x: Int, y: Int, z: Int -> if(x in 4..6 && y in 4..6 && z in 4..6) 100.0 else 10.0 }
    val a = { _: Int, y: Int, _: Int -> if(y < 4) 1.0 else 0.1 }

    val temperature = Array(11) { z -> Array(11) { y -> DoubleArray(11) { x -> t(x, y, z) } } }
    val diffusivity = Array(11) { z -> Array(11) { y -> DoubleArray(11) { x -> a(x, y, z) } } }

    measureTimeMillis {
        heatEquation(temperature, diffusivity, spatialStep, totalTime)
    }.apply { println(this) }
    measureTimeMillis {
        HeatEquationSolver(temperature, diffusivity, spatialStep = spatialStep, totalTime = totalTime).solve()
    }.apply { println(this) }

}