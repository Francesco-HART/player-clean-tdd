package com.example.musicapp.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class ApiLastFM {

    companion object{
        private var api:ApiArtistLastFM

        init {
            val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl("http://ws.audioscrobbler.com/2.0/")
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .client(initClient())
                .build()

            api = retrofit.create(ApiArtistLastFM::class.java)
        }

        private fun initClient(): OkHttpClient {
            val logging = HttpLoggingInterceptor() // require dependence
            logging.level = HttpLoggingInterceptor.Level.BODY
            return OkHttpClient.Builder()
                .addInterceptor(logging)
                .build()
        }

        fun getApi():ApiArtistLastFM = api

    }
}