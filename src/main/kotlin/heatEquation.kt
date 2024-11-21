package org.dinges.heatequation

import kotlin.math.pow

fun heatEquation(
    initialTemperatures: Array<Array<DoubleArray>>,
    diffusivities: Array<Array<DoubleArray>>,
    spatialStep: Double, time: Double
): Array<Array<DoubleArray>> {

    val dr = spatialStep
    val dt = calculateTimeStep(diffusivities, dr)
    val numSteps = (time/dt).toInt()

    var temperature = initialTemperatures

    for(currentStep in 0 ..< numSteps) {
        val temp = temperature

        for(i in temp.indices)
            for(j in temp[0].indices)
                for(k in temp[0][0].indices)
                    temp[i][j][k] = temp[i][j][k] + derivX(temp, diffusivities, dt, dr, k, j, i) + derivY(temp, diffusivities, dt, dr, k, j, i) + derivZ(temp, diffusivities, dt, dr, k, j, i)

        temperature = temp
    }

    return temperature
}

private fun derivX(temperatures: Array<Array<DoubleArray>>, diffusivities: Array<Array<DoubleArray>>, dt: Double, dr: Double, x: Int, y: Int, z: Int): Double {
    val coef = diffusivities[z][y][x] * dt / dr.pow(2)
    val uPlus1 = if(x < temperatures[0][0].lastIndex) temperatures[z][y][x+1] else temperatures[z][y][x]
    val u = temperatures[z][y][x]
    val uMinus1 = if(x > 0) temperatures[z][y][x-1] else temperatures[z][y][x]

    return coef * (uPlus1 - 2*u + uMinus1)
}

private fun derivY(temperatures: Array<Array<DoubleArray>>, diffusivities: Array<Array<DoubleArray>>, dt: Double, dr: Double, x: Int, y: Int, z: Int): Double {
    val coef = diffusivities[z][y][x] * dt / dr.pow(2)
    val uPlus1 = if(y < temperatures[0].lastIndex) temperatures[z][y+1][x] else temperatures[z][y][x]
    val u = temperatures[z][y][x]
    val uMinus1 = if(y > 0) temperatures[z][y-1][x] else temperatures[z][y][x]

    return coef * (uPlus1 - 2*u + uMinus1)
}

private fun derivZ(temperatures: Array<Array<DoubleArray>>, diffusivities: Array<Array<DoubleArray>>, dt: Double, dr: Double, x: Int, y: Int, z: Int): Double {
    val coef = diffusivities[z][y][x] * dt / dr.pow(2)
    val uPlus1 = if(z < temperatures.lastIndex) temperatures[z+1][y][x] else temperatures[z][y][x]
    val u = temperatures[z][y][x]
    val uMinus1 = if(z > 0) temperatures[z-1][y][x] else temperatures[z][y][x]

    return coef * (uPlus1 - 2*u + uMinus1)
}

private fun calculateTimeStep(
    diffusivities: Array<Array<DoubleArray>>,
    spatialStep: Double
): Double {
    val maxAlpha = diffusivities.map { i -> i.map { j -> j.max() }.max() }.max()
    return 0.1 * spatialStep.pow(2) / maxAlpha
}