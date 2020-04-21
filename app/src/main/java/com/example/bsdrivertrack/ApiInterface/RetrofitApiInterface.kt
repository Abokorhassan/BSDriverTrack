package com.example.bsdrivertrack.ApiInterface

import com.example.bsdrivertrack.Models.DriverModel
import com.example.bsdrivertrack.Models.RouteModel
import com.example.bsdrivertrack.Models.StationModel
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface RetrofitApiInterface {
//    @GET("data")
//    fun getData(): Call<List<DriverModel>>

//    @FormUrlEncoded
//    @POST("users")
//    fun login( @Field("username") username:String ): Call<FakeUserModel>

    @GET("onlineDrivers")
    fun checkDriver( @Query("driver_number") driver_number: String ): Call<List<DriverModel>>

    // gettting the routes
    @GET("routes")
    fun getRoute( @Query("name") name: String ): Call<List<RouteModel>>

    @GET("stations")
    fun getStation( @Query("name") station_name: String ): Call<List<StationModel>>




}