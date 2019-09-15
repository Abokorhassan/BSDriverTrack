package com.example.bsdrivertrack.ApiInterface

import com.example.bsdrivertrack.Models.DriverModel
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface RetrofitApiInterface {
//    @GET("data")
//    fun getData(): Call<List<DriverModel>>

//    @FormUrlEncoded
//    @POST("users")
//    fun login( @Field("username") username:String ): Call<FakeUserModel>

    @GET("data")
    fun checkDriver( @Query("driver_number") driver_number: String ): Call<List<DriverModel>>

    @GET("users")
    fun check( @Query("username") username: String ): Call<List<DriverModel>>

    @GET("users")
    fun getUser(): Call<List<DriverModel>>


}