package com.example.bsdrivertrack.LocationHelper

import android.graphics.Color
import android.util.Log
import com.example.bsdrivertrack.Activities.MainActivity
import com.example.bsdrivertrack.R
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.*
import com.google.maps.android.PolyUtil

class GoogleMapHelper {

    companion object {
        private const val ZOOM_LEVEL = 18
        private const val TILT_LEVEL = 25
    }

    /**
     * @param latLng in which position to Zoom the camera.
     * @return the [CameraUpdate] with Zoom and Tilt level added with the given position.
     */

    fun buildCameraUpdate(latLng: LatLng): CameraUpdate {
        val cameraPosition = CameraPosition.Builder()
                .target(latLng)
                .tilt(TILT_LEVEL.toFloat())
                .zoom(ZOOM_LEVEL.toFloat())
                .build()
        return CameraUpdateFactory.newCameraPosition(cameraPosition)
    }

    /**
     * @param position where to draw the [com.google.android.gms.maps.model.Marker]
     * @return the [MarkerOptions] with given properties added to it.
     */

    fun getDriverMarkerOptions(position: LatLng): MarkerOptions {
        val options = getMarkerOptions(R.drawable.bus_icon, position)
        options.flat(true)
        return options
    }

    private fun getMarkerOptions(resource: Int, position: LatLng): MarkerOptions {
        return MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(resource))
                .position(position)
    }

    fun getPolylineOptions(path:String): PolylineOptions {
         val decodedPath = PolyUtil.decode(path)
         val a = LatLng(9.562389, 44.077011)
         val b = LatLng(28.6969421, 77.1423825)
         return PolylineOptions()
             .addAll(decodedPath)
             .geodesic(true)
             .color(Color.RED)
             .width(8f)
    }

}