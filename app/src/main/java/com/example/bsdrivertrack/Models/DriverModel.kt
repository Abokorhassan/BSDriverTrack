package com.example.bsdrivertrack.Models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class DriverModel {
    @Expose
    @SerializedName("bus_number")
    val bus_number: String = ""

    @Expose
    @SerializedName("driver_number")
    val driver_number: String = ""

    @Expose
    @SerializedName("route_name")
    val route_name: String = ""

    @Expose
    @SerializedName("schedule_number")
    val schedule_number: String = ""

    @Expose
    @SerializedName("station_name")
    val station_name: String = ""

//    @Expose
//    @SerializedName("id")
//    val id: Int = 0
//
//    @Expose
//    @SerializedName("name")
//    val name: String = ""
//
//    @Expose
//    @SerializedName("username")
//    val username: String = ""
}