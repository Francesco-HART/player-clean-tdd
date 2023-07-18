package com.example.musicapp

import android.app.Application
import android.content.Context

class MusicApplication : Application() {
    init {
        instance = this
    }
    companion object{
        private var instance: MusicApplication? = null
        fun applicationContext() : Context {
            return instance!!.applicationContext
        }
    }
    override fun onCreate() {
        super.onCreate()
        val context: Context = applicationContext()
    }
}