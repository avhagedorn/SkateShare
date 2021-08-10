package com.skateshare.repostitories

import android.util.Log
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.maps.android.PolyUtil
import com.skateshare.misc.routeToRoutePath
import com.skateshare.misc.routeToRoutePost
import com.skateshare.models.Route
import com.skateshare.models.RouteGlobalMap
import com.skateshare.modelUtils.toRouteGlobalMap
import com.skateshare.models.ReverseGeocodeLocation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

object FirestoreRoutes {

    suspend fun getRoutesAboutRadius(lat: Double, lng: Double, radius: Double) : List<RouteGlobalMap> {
        try {
            val db = FirebaseFirestore.getInstance()

            val center = GeoLocation(lat, lng)
            val bounds = GeoFireUtils.getGeoHashQueryBounds(center, radius);
            val tasks = mutableListOf<Task<QuerySnapshot>>()
            val result = mutableListOf<RouteGlobalMap>()

            for (bound in bounds) {
                val query = db.collection("routesGlobalMap")
                    .orderBy("geohash")
                    .startAt(bound.startHash)
                    .endAt(bound.endHash)
                tasks.add(query.get())
            }

            Tasks.whenAllComplete(tasks).await()
            for (task in tasks){
                val snapshot = task.result
                for (doc in snapshot.documents) {
                    val location = GeoLocation(
                        doc.getDouble("startLat")!!,
                        doc.getDouble("startLng")!!
                    )
                    // Double check the query radius for edge cases
                    val distance = GeoFireUtils.getDistanceBetween(center, location)
                    if (distance <= radius) {
                        val route = doc.toRouteGlobalMap()
                        if (route != null)
                            result.add(route)
                    }
                }
            }
            return result
        } catch (e: Exception) {
            Log.i("FirestoreRoutes", e.message.toString())
            return listOf()
        }
    }

    // For greater efficiency, route data is denormalized to avoid double querying when post
    // data is required and to avoid querying unnecessarily large datasets.

    suspend fun createRoute(route: Route, description: String,
                            minBoardType: String, altitudeRating: String, path: String) {
        val posterId = FirebaseAuth.getInstance().uid!!
        val documentId = UUID.randomUUID().toString()
        val timestamp = Timestamp.now()
        val location = getLocationData(route.lat_start, route.lng_start)

        val routeDataPreview = routeToRoutePost(
            id = documentId,
            uid = posterId,
            date = timestamp,
            url = null,
            description = description,
            minBoardType = minBoardType,
            altitudeRating = altitudeRating,
            route = route,
            city = location.city,
            province = location.province,
            country = location.country
        )
        FirebaseFirestore.getInstance()
            .document("posts/$documentId")
            .set(routeDataPreview)
        createRoutePath(route, timestamp, posterId, documentId, path)
    }

    private suspend fun createRoutePath(route: Route, date: Timestamp,
                                        uid: String, id: String, path: String) {
        val routeMapData = routeToRoutePath(id, date, uid, route, path)
        FirebaseFirestore.getInstance()
            .document("routesGlobalMap/$id")
            .set(routeMapData)
    }

    suspend fun deleteRoute(id: String) {
        val firestore = FirebaseFirestore.getInstance()
        firestore.document("posts/$id").delete()
        firestore.document("routesGlobalMap/$id").delete()
        // Also delete image if needed
    }
}
