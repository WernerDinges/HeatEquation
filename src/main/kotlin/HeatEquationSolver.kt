package org.dinges.heatequation

import kotlin.math.pow

class HeatEquationSolver(
    private val initialTemperatures: Array3D,
    private val diffusivities: Array3D,
    spatialStep: Double,
    private val totalTime: Double
) {
    private val drSquared = spatialStep.pow(2)
    private val dt = calculateTimeStep()

    private val sizeX = initialTemperatures[0][0].size
    private val sizeY = initialTemperatures[0].size
    private val sizeZ = initialTemperatures.size

    fun solve(): Array3D {
        var temperatures = initialTemperatures
        val numSteps = (totalTime / dt).toInt()

        for(step in 0 ..< numSteps)
            temperatures = stepForward(temperatures)

        return temperatures
    }

    private fun stepForward(temperatures: Array3D): Array3D {
        val newTemperatures = Array(sizeZ) { z ->
            Array(sizeY) { y ->
                DoubleArray(sizeX) { x ->
                    val currentTemp = temperatures[z][y][x]
                    currentTemp +
                            computeDerivative(temperatures, diffusivities, x, y, z, ::neighborX) +
                            computeDerivative(temperatures, diffusivities, x, y, z, ::neighborY) +
                            computeDerivative(temperatures, diffusivities, x, y, z, ::neighborZ)
                }
            }
        }
        return newTemperatures
    }

    private fun computeDerivative(
        temperatures: Array3D,
        diffusivities: Array3D,
        x: Int, y: Int, z: Int,
        neighborFunc: (Int, Int, Int) -> Pair<Double, Double>
    ): Double {
        val (uMinus1, uPlus1) = neighborFunc(x, y, z)
        val coef = diffusivities[z][y][x] * dt / drSquared
        val u = temperatures[z][y][x]
        return coef * (uPlus1 - 2 * u + uMinus1)
    }

    private fun neighborX(x: Int, y: Int, z: Int): Pair<Double, Double> {
        val uMinus1 = if(x > 0) initialTemperatures[z][y][x - 1] else initialTemperatures[z][y][x]
        val uPlus1 = if(x < sizeX - 1) initialTemperatures[z][y][x + 1] else initialTemperatures[z][y][x]
        return uMinus1 to uPlus1
    }

    private fun neighborY(x: Int, y: Int, z: Int): Pair<Double, Double> {
        val uMinus1 = if(y > 0) initialTemperatures[z][y - 1][x] else initialTemperatures[z][y][x]
        val uPlus1 = if(y < sizeY - 1) initialTemperatures[z][y + 1][x] else initialTemperatures[z][y][x]
        return uMinus1 to uPlus1
    }

    private fun neighborZ(x: Int, y: Int, z: Int): Pair<Double, Double> {
        val uMinus1 = if(z > 0) initialTemperatures[z - 1][y][x] else initialTemperatures[z][y][x]
        val uPlus1 = if(z < sizeZ - 1) initialTemperatures[z + 1][y][x] else initialTemperatures[z][y][x]
        return uMinus1 to uPlus1
    }

    private fun calculateTimeStep(): Double {
        val maxAlpha = diffusivities.map { i -> i.map { j -> j.max() }.max() }.max()
        return 0.1 * drSquared / maxAlpha
    }
}