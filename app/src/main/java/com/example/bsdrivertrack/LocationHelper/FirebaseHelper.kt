package com.example.bsdrivertrack.LocationHelper

import android.util.Log
import com.example.bsdrivertrack.LocationModel.Driver
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class FirebaseHelper constructor(station_name: String) {

    companion object {
        private const val ONLINE_DRIVERS = "online_drivers"
    }

    private val onlineDriverDatabaseReference: DatabaseReference = FirebaseDatabase
            .getInstance()
            .reference
            .child(ONLINE_DRIVERS)
            .child(station_name)

    init {
        onlineDriverDatabaseReference
                .onDisconnect()
                .removeValue()
    }

    fun updateDriver(driver: Driver) {
        onlineDriverDatabaseReference
                .setValue(driver)
        Log.e("Driver Info", " Updated")
    }

    fun deleteDriver() {
        onlineDriverDatabaseReference
                .removeValue()
    }
}