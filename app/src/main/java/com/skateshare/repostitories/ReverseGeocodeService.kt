package com.skateshare.repostitories

import android.util.Log
import com.skateshare.models.ReverseGeocodeLocation
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

fun createReverseGeocoder() : ReverseGeocoder {
    val retrofit = Retrofit.Builder()
        .baseUrl("https://api.bigdatacloud.net")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    return retrofit.create(ReverseGeocoder::class.java)
}

// Do not call this on the main thread.
// This is ONLY intended to be used to get location data within an existing IO thread.
fun getLocationData(lat: Double, lng: Double) : ReverseGeocodeLocation {
    val call = createReverseGeocoder().getLocation(lat, lng)
    val response = call.execute()

    if (response.isSuccessful)
        return response.body()!!
    return ReverseGeocodeLocation()
}

interface ReverseGeocoder {

    @GET("/data/reverse-geocode-client")
    fun getLocation(
        @Query("latitude") lat: Double,
        @Query("longitude") lng: Double
    ) : Call<ReverseGeocodeLocation>
}
