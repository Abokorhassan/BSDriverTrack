package com.example.bsdrivertrack.LocationModel

data class Driver(
    val lat: Double,
    val lng: Double,
    val bus_number: String,
    val driver_number: String,
    val route_name: String,
    val schedule_number: String,
    val station_name: String
)