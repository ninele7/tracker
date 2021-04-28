package com.ninele7.tracker

import android.app.Application
import android.content.Context

class App : Application() {
    companion object {
        private var INSTANCE: Application? = null

        fun getApplication(): Application? {
            return INSTANCE
        }

        fun getContext(): Context? {
            return INSTANCE?.applicationContext
        }
    }

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
    }
}