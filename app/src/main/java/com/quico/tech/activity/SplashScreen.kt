package com.quico.tech.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.quico.tech.data.PrefManager
import com.quico.tech.viewmodel.SharedViewModel

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefManager: PrefManager = PrefManager(applicationContext)

        if (prefManager!!.isFirstTimeLaunch || prefManager.session_id.isNullOrEmpty()) {
            startActivity(
                Intent(this, LoginActivity::class.java)
            )
        } else
            startActivity(
                Intent(this, HomeTestActivity::class.java)
            )
    }
}