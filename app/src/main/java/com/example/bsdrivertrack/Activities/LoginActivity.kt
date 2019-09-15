package com.example.bsdrivertrack.Activities

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.telecom.Call
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.bsdrivertrack.ApiClients.DriverApiClient
import com.example.bsdrivertrack.Models.DriverModel
import com.example.bsdrivertrack.R
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    var driverList = ArrayList<DriverModel>()
    lateinit var progerssProgressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        enterButton.setOnClickListener {

            val driver_number = driverNumberTxt.text.toString().trim()
            if (driver_number.isEmpty()){
                driverNumberTxt.error = "User name is required"
                driverNumberTxt.requestFocus()
                return@setOnClickListener
            }

//            Log.e("DRIVER NUMBER", driver_number);

            progerssProgressDialog= ProgressDialog(this)
            progerssProgressDialog.setTitle("Loading")
            progerssProgressDialog.setCancelable(false)
            progerssProgressDialog.show()

            DriverApiClient.getClient.checkDriver(driver_number)
                .enqueue(object : Callback<List<DriverModel>>{
                    override fun onFailure(
                        call: retrofit2.Call<List<DriverModel>>,
                        t: Throwable
                    ) {
                        progerssProgressDialog.dismiss()
                        Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
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
                            intent.putExtra("username",driver_number)
                            startActivity(intent)
                        }
                    }
                })
        }
    }
}
