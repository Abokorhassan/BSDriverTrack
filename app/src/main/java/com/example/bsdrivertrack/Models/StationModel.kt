package com.example.bsdrivertrack.Models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class StationModel {

    @Expose
    @SerializedName("name")
    val name: String = ""

    @Expose
    @SerializedName("lat")
    val latitude: Double = 0.0

    @Expose
    @SerializedName("long")
    val longitude: Double=0.0
}