package com.example.bsdrivertrack.Activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.bsdrivertrack.R
import com.google.android.gms.maps.model.Marker

import android.content.pm.PackageManager.PERMISSION_DENIED
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.graphics.Color
import android.os.Looper
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SwitchCompat
import androidx.core.app.ActivityCompat
import com.example.bsdrivertrack.ApiClients.DriverApiClient
import com.google.android.gms.location.*
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.location.*
import com.example.bsdrivertrack.LocationHelper.*
import com.example.bsdrivertrack.LocationInterfaces.IPositiveNegativeListener
import com.example.bsdrivertrack.LocationInterfaces.LatLngInterpolator
import com.example.bsdrivertrack.LocationModel.Driver
import com.example.bsdrivertrack.Models.DriverModel
import com.example.bsdrivertrack.Models.RouteModel
import com.google.android.gms.maps.model.PolylineOptions
import retrofit2.Response
import javax.security.auth.callback.Callback


class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    companion object {
        public const val MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2200
//        public lateinit var pat:String = null
    }


    private var googleMap: GoogleMap? = null
    private lateinit var locationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private var locationFlag = true
    private var driverOnlineFlag = false
    private var currentPositionMarker: Marker? = null
    private val googleMapHelper = GoogleMapHelper()
    lateinit var driverStatusTextView:TextView
    lateinit var progerssProgressDialog: ProgressDialog
    lateinit var path: String
    private val markerAnimationHelper = MarkerAnimationHelper()
    private val uiHelper = UiHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val station_name = intent.getStringExtra("station_name")
        val driver_number = intent.getStringExtra("driver_number")
        val firebaseHelper = FirebaseHelper(station_name, driver_number)
        driverStatusTextView = findViewById<TextView>(R.id.driverStatusTextView)

        val mapFragment: SupportMapFragment = supportFragmentManager.findFragmentById(R.id.supportMap) as SupportMapFragment
        mapFragment.getMapAsync(this)
        displayProgrDial()
        getRoute()
//        displayPolyline()
        createLocationCallback()
        locationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = uiHelper.getLocationRequest()

        if (!uiHelper.isPlayServicesAvailable(this)) {
            Toast.makeText(this, "Play Services did not installed!", Toast.LENGTH_SHORT).show()
            finish()
        } else requestLocationUpdate()

        findViewById<SwitchCompat>(R.id.driverStatusSwitch).setOnCheckedChangeListener { _, b ->
            driverOnlineFlag = b
            if (driverOnlineFlag) driverStatusTextView.text = resources.getString(R.string.online_driver)
            else {

                driverStatusTextView.text = resources.getString(R.string.offline)
                val station_name = intent.getStringExtra("station_name")
                val driver_number = intent.getStringExtra("driver_number")
                val firebaseHelper = FirebaseHelper(station_name, driver_number)
                firebaseHelper.deleteDriver()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestLocationUpdate() {
        if (!uiHelper.isHaveLocationPermission(this)) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
            return
        }
        if (uiHelper.isLocationProviderEnabled(this))
            uiHelper.showPositiveDialogWithListener(this, resources.getString(R.string.need_location), resources.getString(R.string.location_content), object : IPositiveNegativeListener {
                override fun onPositive() {
                    startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }
            }, "Turn On", false)
        locationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())
    }

    private fun createLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                super.onLocationResult(locationResult)
                if (locationResult!!.lastLocation == null) return
                val latLng = LatLng(locationResult.lastLocation.latitude, locationResult.lastLocation.longitude)
                Log.e("Location", latLng.latitude.toString() + " , " + latLng.longitude)
                if (locationFlag) {
                    locationFlag = false
                    animateCamera(latLng)
                }
                val driver_number = intent.getStringExtra("driver_number")
                val bus_number = intent.getStringExtra("bus_number")
                val route_name = intent.getStringExtra("route_name")
                val schedule_number = intent.getStringExtra("schedule_number")
                val station_name = intent.getStringExtra("station_name")
                val firebaseHelper = FirebaseHelper(station_name, driver_number)
                if (driverOnlineFlag) firebaseHelper.updateDriver(Driver(lat = latLng.latitude, lng = latLng.longitude, bus_number = bus_number,
                    driver_number = driver_number, route_name = route_name, schedule_number = schedule_number, station_name = station_name))
                showOrAnimateMarker(latLng)
            }
        }
    }

    private fun showOrAnimateMarker(latLng: LatLng) {
        if (currentPositionMarker == null)
            currentPositionMarker = googleMap?.addMarker(googleMapHelper.getDriverMarkerOptions(latLng))
        else markerAnimationHelper.animateMarkerToGB(currentPositionMarker!!, latLng, LatLngInterpolator.Spherical())
    }

    private fun animateCamera(latLng: LatLng) {
        val cameraUpdate = googleMapHelper.buildCameraUpdate(latLng)
        googleMap?.animateCamera(cameraUpdate, 10, null)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            val value = grantResults[0]
            if (value == PERMISSION_DENIED) {
                Toast.makeText(this, "Location Permission denied", Toast.LENGTH_SHORT).show()
                finish()
            } else if (value == PERMISSION_GRANTED) requestLocationUpdate()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.main_menu, menu)
        return true;
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return  when(item.itemId){
            R.id.logout ->{
//                Toast.makeText(applicationContext, "Logout", Toast.LENGTH_LONG).show()
                val builder = AlertDialog.Builder(this@MainActivity)
                builder.setTitle("Logout")
                builder.setMessage("Do you want to logout?")
                builder.setPositiveButton("Yes", { dialogInterface: DialogInterface, i: Int ->
                    driverOnlineFlag = false
                    driverStatusTextView.text = resources.getString(R.string.offline)
                    val station_name = intent.getStringExtra("station_name")
                    val driver_number = intent.getStringExtra("driver_number")
                    val firebaseHelper = FirebaseHelper(station_name, driver_number)
                    firebaseHelper.deleteDriver()

                    val intent = Intent(this@MainActivity,LoginActivity::class.java)
                    startActivity(intent)
                })
                builder.setNegativeButton("No", { dialogInterface: DialogInterface, i: Int -> })
                val alertDialog: AlertDialog = builder.create()
                alertDialog.setCancelable(false)
                alertDialog.show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun getRoute() {
        val route_name = intent.getStringExtra("route_name")
        DriverApiClient.getClient.getRoute(route_name)
            .enqueue(object : retrofit2.Callback<List<RouteModel>> {
                override fun onFailure(
                    call: retrofit2.Call<List<RouteModel>>,
                    t: Throwable
                ) {
                    progerssProgressDialog.dismiss()
                    Toast.makeText(applicationContext, "Error! there was problem connecting to the server", Toast.LENGTH_LONG).show()
                    Log.e("Log Response", t.message)
                }

                override fun onResponse(
                    call: retrofit2.Call<List<RouteModel>>,
                    response: Response<List<RouteModel>>
                ) {
                    Log.e("OnResponse", response.body().toString());
                    if(!response.isSuccessful){
                        progerssProgressDialog.dismiss()
                        Toast.makeText(applicationContext, response.code(), Toast.LENGTH_SHORT).show()
                        Log.e("Log Response", response.code().toString())
                        return
                    }
                    if (response.body()?.isEmpty()!!) {
                        progerssProgressDialog.dismiss()
                        Toast.makeText(applicationContext, "Empty", Toast.LENGTH_LONG).show()
                        Log.e("Log Response", response.body().toString())
                    }else{
                        progerssProgressDialog.dismiss()
                        Toast.makeText(applicationContext, response.body()!![0].path, Toast.LENGTH_LONG).show()
                        val passedPath = response.body()!![0].path
                        displayPolyline(passedPath)
                        Log.e("Log Response", response.body()!!.toString())
                    }
                }
            })
    }

    private fun displayProgrDial(){
        progerssProgressDialog= ProgressDialog(this)
        progerssProgressDialog.setTitle("Drawing Route")
        progerssProgressDialog.setCancelable(false)
        progerssProgressDialog.show()
    }

    private fun displayPolyline(path:String){
        googleMap?.addPolyline(googleMapHelper.getPolylineOptions(path))
    }

    override fun onMapReady(mMap: GoogleMap){
        googleMap = mMap

    }

    override fun onBackPressed() {
        moveTaskToBack(true)
    }

    override fun onPause() {
        super.onPause()
        Toast.makeText(applicationContext, "On Pause", Toast.LENGTH_LONG).show()
        createLocationCallback()
        locationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = uiHelper.getLocationRequest()

        if (!uiHelper.isPlayServicesAvailable(this)) {
            Toast.makeText(this, "Play Services did not installed!", Toast.LENGTH_SHORT).show()
            finish()
        } else requestLocationUpdate()

//        val driverStatusTextView = findViewById<TextView>(R.id.driverStatusTextView)
        findViewById<SwitchCompat>(R.id.driverStatusSwitch).setOnCheckedChangeListener { _, b ->
            driverOnlineFlag = b
            if (driverOnlineFlag) driverStatusTextView.text = resources.getString(R.string.online_driver)
            else {

                driverStatusTextView.text = resources.getString(R.string.offline)
                val station_name = intent.getStringExtra("station_name")
                val driver_number = intent.getStringExtra("driver_number")
                val firebaseHelper = FirebaseHelper(station_name, driver_number)
                firebaseHelper.deleteDriver()
            }
        }



    }
}
