package com.example.bsdrivertrack.Activities

import android.app.Activity
import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.content.pm.PackageManager
import android.telecom.Call
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.bsdrivertrack.ApiClients.DriverApiClient
import com.example.bsdrivertrack.Manifest
import com.example.bsdrivertrack.Models.DriverModel
import com.example.bsdrivertrack.R
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Callback
import retrofit2.Response
import android.net.NetworkInfo
import android.content.Context.CONNECTIVITY_SERVICE
import androidx.core.content.ContextCompat.getSystemService
import android.net.ConnectivityManager

import android.content.Context


class LoginActivity : AppCompatActivity() {

    companion object{
        private const val INTERNET_PERMISSION_CODE = 1
    }
    lateinit var driver_number: String
    var driverList = ArrayList<DriverModel>()
    lateinit var progerssProgressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        enterButton.setOnClickListener {

            driver_number = driverNumberTxt.text.toString().trim()
            if (driver_number.isEmpty()){
                driverNumberTxt.error = "Driver Number is required"
                driverNumberTxt.requestFocus()
                return@setOnClickListener
            }
            if (checkConnType()) {
                progerssProgressDialog= ProgressDialog(this)
                progerssProgressDialog.setTitle("Loading")
                progerssProgressDialog.setCancelable(false)
                progerssProgressDialog.show()

                driverLogin()

            } else {
                Toast.makeText(getApplicationContext(), "Please check your INTERNET Connection", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun driverLogin(){
        DriverApiClient.getClient.checkDriver(driver_number)
            .enqueue(object : Callback<List<DriverModel>>{
                override fun onFailure(
                    call: retrofit2.Call<List<DriverModel>>,
                    t: Throwable
                ) {
                    progerssProgressDialog.dismiss()
                    Toast.makeText(applicationContext, "Error! there was problem connecting to the server", Toast.LENGTH_LONG).show()
                    Log.e("Log Response", t.message)
                }

                override fun onResponse(
                    call: retrofit2.Call<List<DriverModel>>,
                    response: Response<List<DriverModel>>
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

                        val builder = AlertDialog.Builder(this@LoginActivity)
                        builder.setTitle("Error Message")
                        builder.setMessage("wrong credential or you're not on the Ongoing bus list")
                        builder.setPositiveButton("Ok", null)

                        val alertDialog:AlertDialog = builder.create()
                        alertDialog.setCancelable(false)
                        alertDialog.show()

//                            Toast.makeText(applicationContext, "false", Toast.LENGTH_LONG).show()
//                            Log.e("Log Response", response.body().toString())
                    }else{

                        progerssProgressDialog.dismiss()
//                            Toast.makeText(applicationContext, "true", Toast.LENGTH_LONG).show()
//                            Toast.makeText(applicationContext, response.body()!![0].first_name, Toast.LENGTH_LONG).show()

                        Log.e("Log Response", response.body()!!.toString())
                        val intent = Intent(this@LoginActivity,MainActivity::class.java)
                        intent.putExtra("driver_number",driver_number)
                        intent.putExtra("bus_number", response.body()!![0].bus_number)
                        intent.putExtra("route_name", response.body()!![0].route_name)
                        intent.putExtra("schedule_number", response.body()!![0].schedule_number)
                        intent.putExtra("station_name", response.body()!![0].station_name)
                        startActivity(intent)
                        finish()
                    }
                }
            })
    }


    private fun  checkConnType(): Boolean {
        val connManger:ConnectivityManager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkConnection = connManger.activeNetworkInfo
        if (networkConnection != null && networkConnection.isConnectedOrConnecting) {
            return true
        } else {
            return false
        }
    }
}
