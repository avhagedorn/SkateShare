package com.skateshare.services

import com.google.android.gms.maps.model.LatLng
import kotlin.math.pow

// Sources used:
// https://en.wikipedia.org/wiki/B-spline
// https://johan.karlsteen.com/2011/07/30/improving-google-maps-polygons-with-b-splines/
fun bSpline(route: MutableList<LatLng>) : MutableList<LatLng> {
    val newCoordinates = mutableListOf<LatLng>()
    val rawCoordinates = deconstructRoute(route)

    try {
        val lats = rawCoordinates["lats"]!!
        val lngs = rawCoordinates["lngs"]!!

        var aLat = 0.0; var aLng = 0.0; var bLat = 0.0; var bLng = 0.0
        var cLat = 0.0; var cLng = 0.0; var dLat = 0.0; var dLng = 0.0
        var aCoeff = 0.0; var bCoeff = 0.0; var cCoeff = 0.0
        var newLat = 0.0; var newLng = 0.0

        for (i in 2 until (route.size - 2)) {
            for (j in listOf(0.0, 0.2, 0.4, 0.6, 0.8)) {
                aLat = (-lats[i-2] + 3 * lats[i-1] - 3 * lats[i] + lats[i+1]) / 6
                aLng = (-lngs[i-2] + 3 * lngs[i-1] - 3 * lngs[i] + lngs[i+1]) / 6

                bLat = (lats[i-2] - 2 * lats[i-1] + lats[i]) / 2
                bLng = (lngs[i-2] - 2 * lngs[i-1] + lngs[i]) / 2

                cLat = (-lats[i-2] + lats[i]) / 2
                cLng = (-lngs[i-2] + lngs[i]) / 2

                dLat = (lats[i-2] + 4 * lats[i-1] + lats[i]) / 6
                dLng = (lngs[i-2] + 4 * lngs[i-1] + lngs[i]) / 6

                aCoeff = (j + 0.1).pow(3.0)
                bCoeff = (j + 0.1).pow(2.0)
                cCoeff = (j + 0.1).pow(1.0)
                // dCoeff is always 1, so no need for coefficients!

                newLat = (aCoeff * aLat) + (bCoeff * bLat) + (cCoeff * cLat) + dLat
                newLng = (aCoeff * aLng) + (bCoeff * bLng) + (cCoeff * cLng) + cLng
                newCoordinates.add(LatLng(newLat, newLng))
            }
        }
        return newCoordinates
    }
    catch (e: Exception) {
        return route
    }
}

private fun deconstructRoute(route: List<LatLng>) : HashMap<String, List<Double>> {
    val lats = mutableListOf<Double>()
    val lngs = mutableListOf<Double>()

    route.forEach { point ->
        lats.add(point.latitude)
        lngs.add(point.longitude)
    }
    return hashMapOf(
        "lats" to lats,
        "lngs" to lngs
    )
}