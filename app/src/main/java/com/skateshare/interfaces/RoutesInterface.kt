package com.skateshare.interfaces

import android.net.Uri
import com.google.firebase.Timestamp
import com.skateshare.models.Route
import com.skateshare.models.RouteGlobalMap
import java.util.HashMap

interface RoutesInterface {

    suspend fun getRoutesAboutRadius(lat: Double, lng: Double, radius: Double) : List<RouteGlobalMap>
    suspend fun createRoute(route: Route, description: String, boardType: String,
                            terrainType: String, roadType: String, uri: Uri?)
    suspend fun createRoutePath(route: Route, date: Timestamp,
                                        uid: String, id: String, geohash: String)
    suspend fun deleteRoute(id: String)
    suspend fun saveRouteWithImage(data: HashMap<String, Any?>, documentId: String, uri: Uri)

}