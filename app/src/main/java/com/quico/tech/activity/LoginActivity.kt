package com.quico.tech.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.quico.tech.R
import com.quico.tech.utils.Common

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        init();
    }

    fun init(){
        //Remove Statusbar
        Common.removeStatusBarColor(this);
        Common.hideSystemUIBeloR(this);
    }

}