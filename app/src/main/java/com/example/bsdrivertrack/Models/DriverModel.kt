package com.example.bsdrivertrack.Models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class DriverModel {
    @Expose
    @SerializedName("ph_number")
    val ph_number: Int = 0

    @Expose
    @SerializedName("driver_number")
    val driver_number: String = ""

    @Expose
    @SerializedName("first_name")
    val first_name: String = ""

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