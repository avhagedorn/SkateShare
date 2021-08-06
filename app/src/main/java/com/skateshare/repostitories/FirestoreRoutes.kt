package com.skateshare.repostitories

import android.net.Uri
import android.util.Log
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.google.firebase.Timestamp
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import com.skateshare.misc.routeToDetailedHashMap
import com.skateshare.misc.routeToPreviewHashMap
import com.skateshare.models.Route
import com.skateshare.models.toRoute
import kotlinx.coroutines.tasks.await
import java.util.*

object FirestoreRoutes {

    suspend fun getRoutesAboutRadius(lat: Double, lng: Double, radius: Double) : List<Route> {
        val db = FirebaseFirestore.getInstance()
        val uid = FirebaseAuth.getInstance().uid!!

        val center = GeoLocation(lat, lng)
        val bounds = GeoFireUtils.getGeoHashQueryBounds(center, radius);
        val tasks = mutableListOf<Task<QuerySnapshot>>()
        val result = mutableListOf<Route>()

        for (bound in bounds) {
            val query = db.collection("routesDetailed")
                .orderBy("geohash")
                .startAt(bound.startHash)
                .endAt(bound.endHash)
            tasks.add(query.get())
        }

        Tasks.whenAllComplete(tasks)
            .addOnCompleteListener {
                for (task in tasks){
                    val snapshot = task.result
                    for (doc in snapshot.documents) {
                        val location = GeoLocation(
                            doc.getDouble("start_lat")!!,
                            doc.getDouble("start_lng")!!
                        )
                        // Double check the query radius for edge cases
                        val distance = GeoFireUtils.getDistanceBetween(center, location)
                        if (distance <= radius)
                            result.add(doc.toRoute(uid))
                    }
                }
            }
        Log.i("1one", result.toString())
        return result
    }

    // For greater efficiency, route data is denormalized to avoid double querying when detailed
    // data is required and to avoid querying unnecessarily large datasets.

    suspend fun createRoute(route: Route) {
        val posterId = FirebaseAuth.getInstance().uid!!
        val routeDataPreview = routeToPreviewHashMap(route, posterId)
        FirebaseFirestore.getInstance()
            .document("routesPreview/${routeDataPreview["id"]}")
            .set(routeDataPreview)
        createDenormalizedDetailedRoute(route, routeDataPreview["id"].toString())
    }

    private suspend fun createDenormalizedDetailedRoute(route: Route, previewId: String) {
        val posterId = FirebaseAuth.getInstance().uid!!
        val routeDataDetails = routeToDetailedHashMap(route, previewId, posterId)
        FirebaseFirestore.getInstance()
            .document("routesDetail/${routeDataDetails["previewId"]}")
            .set(routeDataDetails)
    }

    suspend fun deleteRoute(id: String) {
        val firestore = FirebaseFirestore.getInstance()
        firestore.document("routesPreview/$id").delete()
        firestore.document("routesDetail/$id").delete()
    }

}