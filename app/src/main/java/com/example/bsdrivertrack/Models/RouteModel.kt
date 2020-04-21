package com.example.bsdrivertrack.Models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class RouteModel {

    @Expose
    @SerializedName("name")
    val name: String = ""

    @Expose
    @SerializedName("path")
    val path: String = ""

    @Expose
    @SerializedName("station_name")
    val station_name: String = ""
}