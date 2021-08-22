package com.skateshare.services

import kotlin.math.pow

// Sources used:
// https://en.wikipedia.org/wiki/B-spline
// https://johan.karlsteen.com/2011/07/30/improving-google-maps-polygons-with-b-splines/
fun bSpline(oldVals: MutableList<Double>) : MutableList<Double> {

    // If we have less than 5 points, we will not be able to interpolate the line properly.
    // This fringe case is so rare that we handle it by simply using using raw gps data.
    if (oldVals.size < 5)
        return oldVals

    val newVals = mutableListOf<Double>()

    var a: Double
    var b: Double
    var c: Double
    var d: Double
    var aCoeff: Double
    var bCoeff: Double
    var cCoeff: Double
    var newVal: Double

    for (i in 2 until (oldVals.size - 2)) {
        for (j in listOf(0.33, 0.66, 0.99)) {
            a = (-oldVals[i-2] + 3 * oldVals[i-1] - 3 * oldVals[i] + oldVals[i+1]) / 6
            b = (oldVals[i-2] - 2 * oldVals[i-1] + oldVals[i]) / 2
            c = (-oldVals[i-2] + oldVals[i]) / 2
            d = (oldVals[i-2] + 4 * oldVals[i-1] + oldVals[i]) / 6

            aCoeff = (j + 0.1).pow(3.0)
            bCoeff = (j + 0.1).pow(2.0)
            cCoeff = (j + 0.1).pow(1.0)
            // dCoeff is always 1, so no need for coefficients!

            newVal = (aCoeff * a) + (bCoeff * b) + (cCoeff * c) + d
            newVals.add(newVal)
            }
        }
    return newVals
}