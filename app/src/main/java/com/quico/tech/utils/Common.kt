package com.quico.tech.utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.os.Build
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.WindowManager
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.appcompat.widget.Toolbar
import com.quico.tech.R
import com.quico.tech.databinding.AlertSuccessDialogBinding

object Common {


    interface ResponseConfirm {
        fun onConfirm()
    }

    fun removeStatusBarColor(activity: Activity) {
        activity.window?.decorView?.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.window?.statusBarColor = Color.TRANSPARENT
        }
    }

    fun hideSystemUIBeloR(activity: Activity) {
        val decorView: View = activity.window.decorView
        val uiOptions = decorView.systemUiVisibility
        var newUiOptions = uiOptions
        newUiOptions = newUiOptions or View.SYSTEM_UI_FLAG_LOW_PROFILE
        newUiOptions = newUiOptions or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        newUiOptions = newUiOptions or View.SYSTEM_UI_FLAG_IMMERSIVE
        newUiOptions = newUiOptions or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        decorView.systemUiVisibility = newUiOptions
    }

    fun setSystemBarColor(act: Activity, @ColorRes color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = act.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = act.resources.getColor(color)
        }
    }

    fun setSystemBarLight(act: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val view = act.findViewById<View>(android.R.id.content)
            var flags = view.systemUiVisibility
            flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            view.systemUiVisibility = flags
        }
    }

    fun changeOverflowMenuIconColor(toolbar: Toolbar, @ColorInt color: Int) {
        try {
            val drawable = toolbar.overflowIcon
            drawable!!.mutate()
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
        } catch (e: Exception) {
        }
    }

    fun changeMenuIconColor(menu: Menu, @ColorInt color: Int) {
        for (i in 0 until menu.size()) {
            val drawable = menu.getItem(i).icon ?: continue
            drawable.mutate()
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
        }
    }

    fun checkInternet(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        val isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting
        return if (isConnected) {
            true
        } else {
            false
        }
    }


    fun setUpAlert(
        context: Context,
        withSuccessImage: Boolean,
        title: String,
        msg: String,
        buttonText: String?,
        responseConfirm: ResponseConfirm
    ) {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.alert_success_dialog)
        val binding: AlertSuccessDialogBinding =
            AlertSuccessDialogBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(binding.getRoot())
        dialog.setCancelable(false)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding.apply {

            if (!withSuccessImage) {
                successImage.visibility = View.GONE
            }

            if (title.isEmpty()) {
                titleText.visibility = View.INVISIBLE
            } else {
                titleText.text = title
            }

            if (msg.isEmpty()) {
                msgText.visibility = View.GONE
            } else {
                msgText.text = msg
            }
            confirmBtn.text = buttonText

            dialog.show()
            confirmBtn.setOnClickListener {
                dialog.dismiss()
                responseConfirm.onConfirm()
            }
        }
    }
}
