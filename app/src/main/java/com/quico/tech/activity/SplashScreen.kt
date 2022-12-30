package com.quico.tech.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.quico.tech.data.Constant
import com.quico.tech.data.PrefManager
import com.quico.tech.viewmodel.SharedViewModel

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefManager: PrefManager = PrefManager(applicationContext)
        val emailLink = intent?.data?.toString()

        if (emailLink != null) {
            emailLink?.let {
                Log.d(Constant.SEND_EMAIL_LINK, "Email is received well send it to verify.")
                startActivity(
                    Intent(this, VerificationCodeActivity::class.java)
                        .putExtra(Constant.VERIFICATION_TYPE, Constant.EMAIL)
                        .putExtra(Constant.EMAIL_LINK, it)
                        .putExtra(Constant.OPERATION_TYPE, Constant.REGISTER)
                        .putExtra(Constant.PHONE_NUMBER, Constant.TEMPORAR_USER?.mobile)
                )
            }
        } else {

           // prefManager.current_user = null
            if (prefManager!!.isFirstTimeLaunch) {
                prefManager.isFirstTimeLaunch = false
                startActivity(
                    Intent(this, LoginActivity::class.java)
                )
            }

           else if (prefManager.current_user==null) {
                startActivity(
                    Intent(this, LoginActivity::class.java)
                )
            } else
                startActivity(
                    Intent(this, HomeActivity::class.java)
                )
        }
    }
}