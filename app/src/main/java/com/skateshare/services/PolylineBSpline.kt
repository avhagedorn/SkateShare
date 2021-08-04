package com.skateshare.services

import com.google.android.gms.maps.model.LatLng
import kotlin.math.pow

// Sources used:
// https://en.wikipedia.org/wiki/B-spline
// https://johan.karlsteen.com/2011/07/30/improving-google-maps-polygons-with-b-splines/
fun bSpline(oldVals: MutableList<Double>) : MutableList<Double> {
    val newVals = mutableListOf<Double>()

    var a = 0.0; var b = 0.0; var c = 0.0; var d = 0.0
    var aCoeff = 0.0; var bCoeff = 0.0; var cCoeff = 0.0
    var newVal = 0.0

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