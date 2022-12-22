package com.quico.tech.activity

import android.content.Intent
import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.util.Patterns
import android.view.View
import androidx.activity.viewModels
import com.quico.tech.R
import com.quico.tech.databinding.ActivityLoginBinding
import com.quico.tech.model.RegisterBodyParameters

import com.quico.tech.model.RegisterParams
import com.quico.tech.utils.Common
import com.quico.tech.viewmodel.SharedViewModel

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val viewModel: SharedViewModel by viewModels()
    private var showPass = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        setUpText()
    }

    fun init() {
        //Remove Statusbar
        Common.removeStatusBarColor(this)
      //  Common.hideSystemUIBeloR(this)
    }

    private fun setUpText() {
        binding.apply {
            loginText.text = viewModel.getLangResources().getString(R.string.login)
            emailText.text = viewModel.getLangResources().getString(R.string.email)
            emailField.hint = viewModel.getLangResources().getString(R.string.email_address)
            passwordText.text = viewModel.getLangResources().getString(R.string.password)
            passwordField.hint = viewModel.getLangResources().getString(R.string.password)
            dontHaveAccountText.text =
                viewModel.getLangResources().getString(R.string.dont_have_account)
            registerText.text = viewModel.getLangResources().getString(R.string.register)
            orText.text = viewModel.getLangResources().getString(R.string.or)
            guestText.text = viewModel.getLangResources().getString(R.string.continue_as_guest)
            loginBtn.text = viewModel.getLangResources().getString(R.string.login)

            registerText.paintFlags = Paint.UNDERLINE_TEXT_FLAG

            registerText.setOnClickListener {
                startActivity(
                    Intent(this@LoginActivity, RegisterActivity::class.java)
                )
            }

            guestText.setOnClickListener {
                startActivity(
                    //Intent(this@LoginActivity, HomeTestActivity::class.java)
                    Intent(this@LoginActivity, HomeActivity::class.java)
                )
            }

            loginBtn.setOnClickListener {
                checkFields()
            }
            eyeImage.setOnClickListener {
                checkPasswordText()
            }
        }
    }

    fun checkPasswordText() {
        if (showPass)
            hidePassword()
        else
            showPassword()
    }

    fun showPassword() {
        showPass = true
        binding.apply {
            passwordField.setTransformationMethod(null)
            eyeImage.setImageResource(android.R.color.transparent)
            eyeImage.setImageResource(R.drawable.show_password)
            passwordField.setSelection(passwordField.text.length)
        }
    }

    fun hidePassword() {
        showPass = false
        binding.apply {
            passwordField.setTransformationMethod(PasswordTransformationMethod())
            eyeImage.setImageResource(android.R.color.transparent)
            eyeImage.setImageResource(R.drawable.hide_password)
            passwordField.setSelection(passwordField.text.length)
            passwordField.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START)
        }
    }

    private fun checkFields() {
        binding.apply {

            if (!Patterns.EMAIL_ADDRESS.matcher(emailField.text).matches())
                emailField.error = viewModel.getLangResources().getString(R.string.wrong_email)
            else if (passwordField.text.toString().isEmpty())
                passwordField.error =
                    viewModel.getLangResources().getString(R.string.required_field)
            else if (passwordField.text.length < 8 && !Common.isPasswordValid(passwordField.text.toString()))
                passwordField.setError(
                    viewModel.getLangResources().getString(R.string.wrong_password)
                )
            else {
                Common.setUpProgressDialog(this@LoginActivity)
                val loginParams = RegisterBodyParameters(
                    RegisterParams(
                        emailField.text.toString(),Common.encryptPassword(passwordField.text.toString())
                    )
                )
                viewModel.login(loginParams,object :SharedViewModel.ResponseStandard{
                    override fun onSuccess(success: Boolean, resultTitle: String, message: String) {
                        Common.cancelProgressDialog()
                        startActivity(
                            Intent(this@LoginActivity, HomeActivity::class.java)
                        )
                    }

                    override fun onFailure(success: Boolean, resultTitle: String, message: String) {
                        Common.cancelProgressDialog()
                    }
                })
            }
        }
    }
}
