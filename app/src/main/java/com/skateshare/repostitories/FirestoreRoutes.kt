package com.skateshare.repostitories

import android.net.Uri
import com.google.firebase.Timestamp
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.skateshare.misc.routeToDetailedHashMap
import com.skateshare.misc.routeToPreviewHashMap
import com.skateshare.models.Route
import kotlinx.coroutines.tasks.await
import java.util.*

object FirestoreRoutes {

    suspend fun createRoute(route: Route) {
        val posterId = FirebaseAuth.getInstance().uid!!
        val routeDataPreview = routeToPreviewHashMap(route, posterId)
        FirebaseFirestore.getInstance()
            .document("routesPreview/${routeDataPreview["id"]}")
            .set(routeDataPreview)
        addRouteDetail(route, routeDataPreview["id"].toString())
    }

    private suspend fun addRouteDetail(route: Route, previewId: String) {
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