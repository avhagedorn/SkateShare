package com.skateshare.models

import com.google.gson.annotations.SerializedName

data class ReverseGeocodeLocation(

    @SerializedName("city")
    val city: String,

    @SerializedName("principalSubdivision")
    val province: String,

    @SerializedName("countryName")
    val country: String
)
