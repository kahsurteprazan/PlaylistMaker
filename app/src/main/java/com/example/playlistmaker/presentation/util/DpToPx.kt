package com.example.playlistmaker.presentation.util

import android.content.res.Resources

object DpToPx {
    fun Int.toPx(): Int {
        return (this * Resources.getSystem().displayMetrics.density).toInt()
    }
}