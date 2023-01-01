package com.quico.tech.utils

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.os.Build
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.WindowManager
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import com.quico.tech.R
import com.quico.tech.databinding.AlertChoicesDialogBinding
import com.quico.tech.databinding.AlertSuccessDialogBinding
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.regex.Pattern

object Common {

    private var progressDialog: Dialog? = null

    interface ResponseConfirm {
        fun onConfirm()
    }

    interface ResponseChoices {
        fun onConfirm()
        fun onCancel()
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

    fun checkGalleryPermissions(context: Context): Boolean {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED /*&&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED */
        ) {
            return true
        }
        return false
    }

    fun requestPermissions(activity: Activity) {

        val PERMISSIONS = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        ActivityCompat.requestPermissions(
            activity,
            PERMISSIONS,
            PackageManager.PERMISSION_GRANTED
        )
    }

    fun setUpAlert(
        context: Context,
        withSuccessImage: Boolean,
        title: String,
        msg: String,
        buttonText: String?,
        responseConfirm: ResponseConfirm?
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
                responseConfirm?.onConfirm()
            }
        }
    }


    fun setUpChoicesAlert(
        context: Context,
        title: String,
        msg: String,
        cancelBtnText: String,
        confirmBtnText: String,
        responseChoices: ResponseChoices?
    ) {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.alert_choices_dialog)
        val binding: AlertChoicesDialogBinding =
            AlertChoicesDialogBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(binding.getRoot())
        dialog.setCancelable(false)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding.apply {

            titleText.text = title
            msgText.text = msg
            cancelBtn.text = cancelBtnText
            confirmBtn.text = confirmBtnText

            dialog.show()
            confirmBtn.setOnClickListener {
                dialog.dismiss()
                responseChoices?.onConfirm()
            }

            cancelBtn.setOnClickListener {
                dialog.dismiss()
                responseChoices?.onCancel()
            }
        }
    }

    fun setUpProgressDialog(context: Context) {
        progressDialog = Dialog(context)
        progressDialog?.setContentView(R.layout.loading_progress_dialog)
        progressDialog?.setCancelable(false)
        progressDialog?.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progressDialog?.show()
    }

    fun cancelProgressDialog() {
        if (progressDialog != null) progressDialog!!.dismiss()
    }

    fun isProgressIsLoading() = (progressDialog != null && progressDialog?.isShowing == true)

    fun isPasswordValid(password: String): Boolean {
        val PASSWORD_PATTERN: Pattern = Pattern.compile(
            "[a-zA-Z0-9\\!\\@\\#\\$]{8,24}"
        )
        return !TextUtils.isEmpty(password) && PASSWORD_PATTERN.matcher(password).matches()
    }

    fun encryptPassword(s: String): String {
        try {
            // Create MD5 Hash
            val digest: MessageDigest = MessageDigest.getInstance("MD5")
            digest.update(s.toByteArray())
            val messageDigest: ByteArray = digest.digest()

            // Create Hex String
            val hexString = StringBuffer()
            for (i in messageDigest.indices) hexString.append(
                Integer.toHexString(
                    0xFF and messageDigest[i]
                        .toInt()
                )
            )
            return hexString.toString()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
        return ""
    }
}
