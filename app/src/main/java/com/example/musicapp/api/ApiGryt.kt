package com.example.musicapp.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class ApiGryt {
    companion object{
        private var api:ApiMusicGryt
        private var token:String=""
        val baseUrl = "https://music.gryt.tech/"

        init {
            val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .client(initClient())
                .build()

            api = retrofit.create(ApiMusicGryt::class.java)
        }

        private fun initClient():OkHttpClient{
            val logging = HttpLoggingInterceptor() // require dependence
            logging.level = HttpLoggingInterceptor.Level.BODY
            return OkHttpClient.Builder()
                .addInterceptor(logging)
                .build()
        }

        fun getApi():ApiMusicGryt = api

        fun setToken(value:String){
            token = value
        }
        fun getHeaders():Map<String,String>{
            val headerMap = mutableMapOf<String, String>()
            headerMap["Authorization"] = "Token $token"
            return headerMap
        }
    }
}