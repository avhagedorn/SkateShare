package com.skateshare.repostitories

import android.net.Uri
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import com.skateshare.interfaces.RoutesInterface
import com.skateshare.misc.POST_MEDIA
import com.skateshare.misc.POST_ROUTE
import com.skateshare.misc.routeToRoutePath
import com.skateshare.misc.routeToRoutePost
import com.skateshare.modelUtils.toRouteGlobalMap
import com.skateshare.models.Route
import com.skateshare.models.RouteGlobalMap
import kotlinx.coroutines.tasks.await
import java.util.*

object FirestoreRoutes : RoutesInterface {

    override suspend fun getRoutesAboutRadius(lat: Double, lng: Double, radius: Double)
                                                                : List<RouteGlobalMap> {
        try {
            val db = FirebaseFirestore.getInstance()

            val center = GeoLocation(lat, lng)
            val bounds = GeoFireUtils.getGeoHashQueryBounds(center, radius)
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
            return listOf()
        }
    }

    // For greater efficiency, route data is denormalized to avoid double querying when post
    // data is required and to avoid querying unnecessarily large datasets.
    override suspend fun createRoute(route: Route, description: String, boardType: String,
                                     terrainType: String, roadType: String, uri: Uri?) {
        val posterId = FirebaseAuth.getInstance().uid!!
        val documentId = UUID.randomUUID().toString()
        val timestamp = Timestamp.now()
        val location = getLocationData(route.lat_start, route.lng_start)
        val geohash = GeoFireUtils.getGeoHashForLocation(
            GeoLocation(route.lat_start, route.lng_start))

        val routeDataPreview = routeToRoutePost(
            id = documentId,
            uid = posterId,
            date = timestamp,
            url = null,
            description = description,
            boardType = boardType,
            terrainType = terrainType,
            roadType = roadType,
            route = route,
            city = location.city,
            province = location.province,
            country = location.country,
            geohash = geohash
        )

        if (uri != null)
            saveRouteWithImage(routeDataPreview, documentId, uri)
        else
            FirebaseFirestore.getInstance()
                .document("posts/$documentId").set(routeDataPreview)

        createRoutePath(route, timestamp, posterId, documentId, geohash)
    }

    override suspend fun saveRouteWithImage(data: HashMap<String, Any?>, documentId: String, uri: Uri) {
        val imageReference = FirebaseStorage.getInstance()
            .getReference("postPictures/$documentId")
        imageReference.putFile(uri).await()
        imageReference.downloadUrl.addOnSuccessListener { newUri ->
            data["imageUrl"] = newUri.toString()

            FirebaseFirestore.getInstance()
                .document("posts/$documentId").set(data)
        }.await()
    }

    override suspend fun createRoutePath(route: Route, date: Timestamp,
                                        uid: String, id: String, geohash: String) {
        val routeMapData = routeToRoutePath(id, date, uid, route, geohash)
        FirebaseFirestore.getInstance()
            .document("routesGlobalMap/$id")
            .set(routeMapData)
    }

    override suspend fun deleteRoute(id: String) {
        val db = FirebaseFirestore.getInstance()
        db.document("posts/$id").delete()
        db.document("routesGlobalMap/$id").delete()
        FirebaseStorage.getInstance().getReference("postPictures/$id").delete()
    }
}
