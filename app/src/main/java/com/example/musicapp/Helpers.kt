package com.example.musicapp

import android.graphics.Color
import com.google.android.material.snackbar.Snackbar

fun String.extractFilename(): String{
    return this.substring(this.lastIndexOf("/")+1)
}

fun Snackbar.successColor():Snackbar{
    this.setTextColor(Color.GREEN)
    return this
}
fun Snackbar.failColor():Snackbar{
    this.setTextColor(Color.RED)
    return this
}