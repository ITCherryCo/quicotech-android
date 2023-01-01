package com.quico.tech

import android.app.Application
import com.quico.tech.connection.RetrofitInstance

class MyApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        RetrofitInstance.app = this
    }
}