package com.skateshare.repostitories

import android.util.Log
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.skateshare.modelUtils.toLiteRoutePost
import com.skateshare.models.RoutePost
import kotlinx.coroutines.tasks.await

object FirestoreRoutePosts {

    suspend fun getRoutePostsAboutRadius(lat: Double, lng: Double, radius: Double)
                                                                : List<RoutePost> {
        try {
            val db = FirebaseFirestore.getInstance()

            val center = GeoLocation(lat, lng)
            val bounds = GeoFireUtils.getGeoHashQueryBounds(center, radius)
            val tasks = mutableListOf<Task<QuerySnapshot>>()
            val result = mutableListOf<RoutePost>()

            for (bound in bounds) {
                val query = db.collection("posts")
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
                        val route = doc.toLiteRoutePost(lat, lng)
                        if (route != null)
                            result.add(route)
                    }
                }
            }
            return result
        } catch (e: Exception) {
            Log.i("FirebaseRouteUtil", e.message.toString())
            return listOf()
        }
    }

}