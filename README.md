# Heat equation - 3D simulation

This little project is an implementation of 3-dimensional [Finite difference method](https://en.wikipedia.org/wiki/Finite_difference_method) in the context of the [Heat equation](https://en.wikipedia.org/wiki/Heat_equation).
The simulator has a very modest implementation: a rectangular three-dimensional mesh with a custom initial temperature distribution and a diffusivity distribution.
The edges of the simulation area do not allow heat to flow outward. Zero diffusivity can represent a heat source.

The code is executed in two variants: a single function and a class. Both variants were compared in terms of simulation performance.

## How does it work?

The numerical solution of the Heat equation is the computation of the time derivative through the second order derivatives for X, Y and Z. For each node of the grid:
```kotlin
temp[i][j][k] = temp[i][j][k] + derivX(/*...*/) + derivY(/*...*/) + derivZ(/*...*/)
```

The spatial derivative of the second order is computed as follows:
```kotlin
private fun derivX(
    // Current temperature distribution
    temperatures: Array3D,
    // Distribution of diffusivity
    diffusivities: Array3D,
    // Time step and spatial step
    dt: Double, dr: Double,
    // Node coordinates
    x: Int, y: Int, z: Int
): Double {
    val coef = diffusivities[z][y][x] * dt / dr.pow(2)
    val uPlus1 = if(x < temperatures[0][0].lastIndex) temperatures[z][y][x+1] else temperatures[z][y][x]
    val u = temperatures[z][y][x]
    val uMinus1 = if(x > 0) temperatures[z][y][x-1] else temperatures[z][y][x]

    return coef * (uPlus1 - 2*u + uMinus1)
}
```

For the simulation to be stable, the time step must be small enough (see [CFL criterion fulfillment](https://en.wikipedia.org/wiki/Courant%E2%80%93Friedrichs%E2%80%93Lewy_condition)). The time step is calculated once when the simulation is initialized:
```kotlin
private fun calculateTimeStep(
    diffusivities: Array3D,
    spatialStep: Double
): Double {
    // Max diffusivity number among all the nodes.
    val maxAlpha = diffusivities.map { i -> i.map { j -> j.max() }.max() }.max()
    return 0.1 * spatialStep.pow(2) / maxAlpha
}
```

By repeating the process for each node, we progress through the simulation.

## Beautiful graphs

Example of heat distribution taken from a 3-dimensional simulation:
![T(t = 0; 0,02; 0,1; 0,5)](https://github.com/user-attachments/assets/8c6653b0-421f-49d1-a995-c994614174e2)

The vertical axis shows the temperature. Here it is noticeable that on one side the substance has a higher diffusivity.

Comparsion of the two versions of the code. We notice an interesting dependence of the performance on the number of iterations:
![Old (Single function) _ New (Class)](https://github.com/user-attachments/assets/53c1b6cc-a7db-4d61-b79f-d4dc2993e36a)

The vertical axis shows the execution time in milliseconds. The new version (class) has a faster setup and handles longer simulations (over 100000 steps) better, while the older version (single feature) handles small simulations (10-100000 steps) better. The tests were carried out for a mesh of size 11x11x11.

The big disadvantage is the strong discrepancy in results between the two versions of the code (up to 15% error on some nodes) in short simulations.

## Versions of the solver
- [Old version - A single function](https://github.com/WernerDinges/HeatEquation/blob/master/src/main/kotlin/heatEquation.kt)
- [New version - A class](https://github.com/WernerDinges/HeatEquation/blob/master/src/main/kotlin/HeatEquationSolver.kt)
