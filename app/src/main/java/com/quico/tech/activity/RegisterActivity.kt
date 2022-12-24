package com.quico.tech.activity

import android.content.Intent
import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.quico.tech.R
import com.quico.tech.data.Constant
import com.quico.tech.databinding.ActivityRegisterBinding
import com.quico.tech.model.RegisterBodyParameters
import com.quico.tech.model.RegisterParams
import com.quico.tech.utils.Common
import com.quico.tech.viewmodel.SharedViewModel
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: SharedViewModel by viewModels()
    private var showPass = false
    private var showConfirmPass = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
        setUpText()
        subscribeRegisterUser()
    }

    fun init() {
        //Remove Statusbar
        Common.removeStatusBarColor(this);
      //  Common.hideSystemUIBeloR(this);
    }

    private fun setUpText() {
        binding.apply {
            registerText.text = viewModel.getLangResources().getString(R.string.register)
            fullNameText.text = viewModel.getLangResources().getString(R.string.full_name)
            fullNameField.hint = viewModel.getLangResources().getString(R.string.your_full_name)
            emailText.text = viewModel.getLangResources().getString(R.string.email)
            emailField.hint = viewModel.getLangResources().getString(R.string.email_address)
            phoneNumberText.text = viewModel.getLangResources().getString(R.string.phone_number)
            phoneNumberField.hint =
                viewModel.getLangResources().getString(R.string.phone_number_example)
            passwordText.text = viewModel.getLangResources().getString(R.string.password)
            passwordField.hint = viewModel.getLangResources().getString(R.string.password)
            confirmPasswordText.text =
                viewModel.getLangResources().getString(R.string.confirm_password)
            confirmPasswordField.hint =
                viewModel.getLangResources().getString(R.string.confirm_password)
            haveAccountText.text = viewModel.getLangResources().getString(R.string.have_account)
            loginText.text = viewModel.getLangResources().getString(R.string.login)
            registerBtn.text = viewModel.getLangResources().getString(R.string.register)

            loginText.paintFlags = Paint.UNDERLINE_TEXT_FLAG

            loginText.setOnClickListener {
                startActivity(
                    Intent(this@RegisterActivity, LoginActivity::class.java)
                )
            }

            registerBtn.setOnClickListener {
                checkFields()
            }

            eyeImage.setOnClickListener {
                checkPasswordText()
            }

            eyeImage2.setOnClickListener {
                checkConfirmPasswordText()
            }
           // countryPicker.setEnabled(false)
        }
    }

     fun subscribeRegisterUser() {
        binding.apply {
            lifecycleScope.launch {
                viewModel.can_register.collect { can_register ->
                    when (can_register) {
                        true -> {
                            Log.d(Constant.USER_REGISTER_TAG, "can register now")
                           // Toast.makeText(this@RegisterActivity,"can register",Toast.LENGTH_LONG).show()
                           // Common.setUpProgressDialog(this@RegisterActivity)
                           // registerUser()
                        }
                        false -> {
                            Log.d(Constant.USER_REGISTER_TAG, "cant register now")
                          //  Toast.makeText(this@RegisterActivity,"cant",Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }


    override fun onResume() {
        super.onResume()
      //  subscribeRegisterUser()
        if (Constant.can_register)
            registerUser()
    }

    private fun checkFields() {

        binding.apply {
            val phoneValue = phoneNumberField.text.toString().trim { it <= ' ' }

            if (fullNameField.text.toString().isEmpty()) {
                fullNameField.error =
                    viewModel.getLangResources().getString(R.string.required_field)
            } else if (!Patterns.EMAIL_ADDRESS.matcher(emailField.text).matches())
                emailField.error = viewModel.getLangResources().getString(R.string.wrong_email)
            else if (phoneValue.isEmpty())
                phoneNumberField.error =
                    viewModel.getLangResources().getString(R.string.required_field)
            else if (!phoneValue.isEmpty() && phoneValue.length < 8)
                phoneNumberField.error =
                    viewModel.getLangResources().getString(R.string.invalid_phone_number)
            else if (passwordField.text.toString().isEmpty())
                passwordField.error =
                    viewModel.getLangResources().getString(R.string.required_field)
            else if (passwordField.text.length < 8 && !Common.isPasswordValid(passwordField.text.toString()))
                passwordField.setError(
                    viewModel.getLangResources().getString(R.string.wrong_password)
                )
            else if (confirmPasswordField.text.toString().isEmpty())
                confirmPasswordField.error =
                    viewModel.getLangResources().getString(R.string.required_field)
            else if (!confirmPasswordField.text.toString()
                    .equals(passwordField.text.toString())
            )
                confirmPasswordField.error =
                    viewModel.getLangResources().getString(R.string.mismatch_password)
            else {
                // send sms verification code

                startActivity(
                    Intent(this@RegisterActivity, VerificationCodeActivity::class.java)
                        .putExtra(Constant.VERIFICATION_TYPE, Constant.PHONE_NUMBER)
                        .putExtra(Constant.OPERATION_TYPE, Constant.REGISTER)
                        .putExtra(Constant.PHONE_NUMBER, phoneValue)
                )
            }
        }
    }

    fun checkPasswordText() {
        if (showPass)
            hidePassword()
        else
            showPassword()
    }

    fun checkConfirmPasswordText() {
        if (showConfirmPass)
            showConfirmPassword()
        else
            hideConfirmPassword()
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


    fun showConfirmPassword() {
        showConfirmPass = true
        binding.apply {
            confirmPasswordField.setTransformationMethod(null)
            eyeImage.setImageResource(android.R.color.transparent)
            eyeImage.setImageResource(R.drawable.show_password)
            confirmPasswordField.setSelection(confirmPasswordField.text.length)
        }
    }

    fun hideConfirmPassword() {
        showConfirmPass = false
        binding.apply {
            confirmPasswordField.setTransformationMethod(PasswordTransformationMethod())
            eyeImage.setImageResource(android.R.color.transparent)
            eyeImage.setImageResource(R.drawable.hide_password)
            confirmPasswordField.setSelection(confirmPasswordField.text.length)
            confirmPasswordField.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START)
        }
    }

    private fun registerUser() {
        binding.apply {

            val params = RegisterBodyParameters(
                RegisterParams(
                    emailField.text.toString(),
                    "+961${phoneNumberField.text.toString()}",
                    fullNameField.text.toString(),
                    Common.encryptPassword(passwordField.text.toString())
                )
            )

            Common.setUpProgressDialog(this@RegisterActivity)
            viewModel.register(params, object : SharedViewModel.ResponseStandard {

                override fun onSuccess(success: Boolean, resultTitle: String, message: String) {
                    if (message != null)
                        viewModel.login(RegisterBodyParameters(
                            RegisterParams(
                                emailField.text.toString(),
                                Common.encryptPassword(passwordField.text.toString())
                            )
                        ), object : SharedViewModel.ResponseStandard {
                            override fun onSuccess(
                                success: Boolean,
                                resultTitle: String,
                                message: String
                            ) {
                                Common.cancelProgressDialog()
                                startActivity(
                                    Intent(this@RegisterActivity, HomeActivity::class.java)
                                )
                                viewModel.canRegister=false
                            }

                            override fun onFailure(
                                success: Boolean,
                                resultTitle: String,
                                message: String
                            ) {
                                Common.cancelProgressDialog()
                            }
                        })
                }

                override fun onFailure(success: Boolean, resultTitle: String, message: String) {
                    Common.cancelProgressDialog()
                    Common.setUpAlert(
                        this@RegisterActivity, false,
                        viewModel.getLangResources().getString(R.string.error),
                        message,
                        viewModel.getLangResources().getString(R.string.ok),
                        null
                    )
                }
            })
        }
    }


}