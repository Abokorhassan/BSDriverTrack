package com.example.bsdrivertrack.ApiClients

import com.example.bsdrivertrack.ApiInterface.RetrofitApiInterface
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object DriverApiClient {

    var BASE_URL:String="http://192.168.1.128/BSProject/public/"
//    var BASE_URL:String="https://jsonplaceholder.typicode.com/"

    val getClient: RetrofitApiInterface
        get() {

            val gson = GsonBuilder()
                .serializeNulls()
                .setLenient()
                .create()
            val interceptor = HttpLoggingInterceptor()
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

            val client = OkHttpClient.Builder()
                .connectTimeout(40, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()

            return retrofit.create(RetrofitApiInterface::class.java)

        }
}