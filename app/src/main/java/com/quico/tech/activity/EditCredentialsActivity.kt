package com.quico.tech.activity

import android.content.Intent
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.quico.tech.R
import com.quico.tech.data.Constant
import com.quico.tech.data.Constant.CHANGE_EMAIL
import com.quico.tech.data.Constant.CHANGE_PASSWORD
import com.quico.tech.data.Constant.CHANGE_PHONE_NUMBER
import com.quico.tech.data.Constant.EMAIL
import com.quico.tech.data.Constant.FORGET_PASSWORD
import com.quico.tech.data.Constant.PHONE_NUMBER
import com.quico.tech.databinding.ActivityEditCredentialsBinding
import com.quico.tech.model.PasswordBodyParameters
import com.quico.tech.model.PasswordParams
import com.quico.tech.model.RegisterParams
import com.quico.tech.utils.Common
import com.quico.tech.viewmodel.SharedViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class EditCredentialsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditCredentialsBinding
    private val viewModel: SharedViewModel by viewModels()
    private var edit_profile_type: String = ""
    private var showPass = false
    private var showConfirmPass = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditCredentialsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        edit_profile_type = intent.extras?.getString(Constant.PROFILE_EDIT_TYPE)!!
        checkEditType()
        initStatusBar()
    }

    fun initStatusBar() {
        Common.setSystemBarColor(this, R.color.white)
        Common.setSystemBarLight(this)
    }

    private fun checkPasswordText() {
        if (showPass)
            hidePassword()
        else
            showPassword()
    }

    private fun checkConfirmPasswordText() {
        if (showConfirmPass)
            hideConfirmPassword()
        else
            showConfirmPassword()
    }

    private fun showPassword() {
        showPass = true
        binding.apply {
            passwordField.setTransformationMethod(null)
            eyeImage.setImageResource(android.R.color.transparent)
            eyeImage.setImageResource(R.drawable.show_password)
            passwordField.setSelection(passwordField.text.length)
        }
    }

    private fun hidePassword() {
        showPass = false
        binding.apply {
            passwordField.setTransformationMethod(PasswordTransformationMethod())
            eyeImage.setImageResource(android.R.color.transparent)
            eyeImage.setImageResource(R.drawable.hide_password)
            passwordField.setSelection(passwordField.text.length)
            passwordField.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START)
        }
    }

    private fun showConfirmPassword() {
        showConfirmPass = true
        binding.apply {
            confirmPasswordField.setTransformationMethod(null)
            eyeImage2.setImageResource(android.R.color.transparent)
            eyeImage2.setImageResource(R.drawable.show_password)
            confirmPasswordField.setSelection(confirmPasswordField.text.length)
        }
    }

    private fun hideConfirmPassword() {
        showConfirmPass = false
        binding.apply {
            confirmPasswordField.setTransformationMethod(PasswordTransformationMethod())
            eyeImage2.setImageResource(android.R.color.transparent)
            eyeImage2.setImageResource(R.drawable.hide_password)
            confirmPasswordField.setSelection(confirmPasswordField.text.length)
            confirmPasswordField.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START)
        }
    }

    private fun checkEditType() {
        binding.apply {
            title.text = viewModel.getLangResources().getString(R.string.profile)
            saveBtn.text = viewModel.getLangResources().getString(R.string.save)
            backArrow.setOnClickListener {
                onBackPressed()
            }
            if (viewModel.getLanguage().equals(Constant.AR))
                backArrow.scaleX = -1f

            saveBtn.setOnClickListener {
                checkFields()
            }

            eyeImage.setOnClickListener {
                checkPasswordText()
            }

            eyeImage2.setOnClickListener {
                checkConfirmPasswordText()
            }

            when (edit_profile_type) {
                Constant.CHANGE_PHONE_NUMBER -> {
                    titleText.text =
                        viewModel.getLangResources().getString(R.string.mobile_number)
                    phoneField.hint =
                        viewModel.getLangResources().getString(R.string.phone_number_example)
                    phoneContainer.visibility = View.VISIBLE
                    emailField.visibility = View.GONE
                    passwordContainer.visibility = View.GONE
                }

                Constant.CHANGE_EMAIL -> {
                    titleText.text =
                        viewModel.getLangResources().getString(R.string.change_email_address)
                    emailField.hint = viewModel.getLangResources().getString(R.string.email_example)
                    phoneContainer.visibility = View.GONE
                    passwordContainer.visibility = View.GONE
                    emailField.visibility = View.VISIBLE
                }

                Constant.CHANGE_PASSWORD, FORGET_PASSWORD -> {
                    titleText.text =
                        viewModel.getLangResources().getString(R.string.change_current_password)
                    phoneContainer.visibility = View.GONE
                    emailField.visibility = View.GONE
                    passwordContainer.visibility = View.VISIBLE
                    passwordText.text =
                        viewModel.getLangResources().getString(R.string.new_password)
                    passwordField.hint =
                        viewModel.getLangResources().getString(R.string.password)
                    confirmPasswordText.text =
                        viewModel.getLangResources().getString(R.string.confirm_password)
                    confirmPasswordField.hint =
                        viewModel.getLangResources().getString(R.string.password)
                }
                FORGET_PASSWORD -> {
                    title.text =
                        viewModel.getLangResources().getString(R.string.change_current_password)
                }
            }
        }
    }

    private fun checkFields() {
        binding.apply {
            when (edit_profile_type) {
                Constant.CHANGE_PHONE_NUMBER -> {
                    val phoneValue = phoneField.text.toString().trim { it <= ' ' }

                    // when saving mobile nb check if its the same phone nb unless you can change it
                    if (phoneValue.isEmpty())
                        phoneField.error =
                            viewModel.getLangResources().getString(R.string.required_field)
                    else if (!phoneValue.isEmpty() && phoneValue.length < 8)
                        phoneField.error =
                            viewModel.getLangResources().getString(R.string.invalid_phone_number)
                    else if (phoneValue.equals(viewModel.user!!.mobile.substring(4))) {
                        phoneField.error =
                            viewModel.getLangResources().getString(R.string.same_phone_number)
                    } else {
                        viewModel.operation_type = CHANGE_PHONE_NUMBER
                        viewModel.verification_type = PHONE_NUMBER
                        viewModel.temporar_user =  RegisterParams(mobile = phoneValue)
                        // go to verification
                        startActivity(
                            Intent(
                                this@EditCredentialsActivity,
                                VerificationCodeActivity::class.java
                            )

                                //.putExtra(Constant.VERIFICATION_TYPE, Constant.PHONE_NUMBER)
                                //.putExtra(Constant.OPERATION_TYPE, Constant.CHANGE_PHONE_NUMBER)
                               // .putExtra(Constant.PHONE_NUMBER, phoneValue)
                        )
                    }
                }

                Constant.CHANGE_EMAIL -> {
                    if (!Patterns.EMAIL_ADDRESS.matcher(emailField.text).matches())
                        emailField.error =
                            viewModel.getLangResources().getString(R.string.wrong_email)
                    else {
                        //TEMPORAR_USER = RegisterParams(emailField.text.toString(), "")
                        viewModel.temporar_user = RegisterParams(emailField.text.toString(), "")
                        viewModel.operation_type = CHANGE_EMAIL
                        viewModel.verification_type = EMAIL
                        startActivity(
                            Intent(
                                this@EditCredentialsActivity,
                                VerificationCodeActivity::class.java
                            )
                               // .putExtra(Constant.VERIFICATION_TYPE, Constant.EMAIL)
                               // .putExtra(Constant.OPERATION_TYPE, Constant.CHANGE_EMAIL)
                        )
                    }
                }

                Constant.CHANGE_PASSWORD, FORGET_PASSWORD -> {

                    if (passwordField.text.toString().isEmpty())
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

                        var reset = false
                        var passwordParams: PasswordBodyParameters? = null
                        if (edit_profile_type.equals(CHANGE_PASSWORD)) {
                            reset = false
                            passwordParams = PasswordBodyParameters(
                                PasswordParams(
                                    Common.encryptPassword(passwordField.text.toString())
                                )
                            )
                        } else if (edit_profile_type.equals(FORGET_PASSWORD)) {
                            reset = true
                            passwordParams = PasswordBodyParameters(
                                PasswordParams(
                                    viewModel.temporar_user!!.login,
                                    //TEMPORAR_USER!!.login,
                                    Common.encryptPassword(passwordField.text.toString())
                                )
                            )
                        }
                        changeResetPassword(reset, passwordParams!!)
                    }
                }
            }
        }
    }

    private fun changeResetPassword(reset: Boolean, passwordParams: PasswordBodyParameters) {
        Common.setUpProgressDialog(this@EditCredentialsActivity)

        viewModel.changePassword(reset,
            passwordParams!!,
            object : SharedViewModel.ResponseStandard {
                override fun onSuccess(
                    success: Boolean,
                    resultTitle: String,
                    message: String
                ) {
                    Common.cancelProgressDialog()
                    Toast.makeText(
                        this@EditCredentialsActivity,
                        message,
                        Toast.LENGTH_LONG
                    )
                    lifecycleScope.launch {
                        delay(300)

                        startActivity(
                            Intent(
                                this@EditCredentialsActivity,
                                LoginActivity::class.java
                            )
                                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        )
                    }
                }

                override fun onFailure(
                    success: Boolean,
                    resultTitle: String,
                    message: String
                ) {
                    Common.cancelProgressDialog()
                    if (message.equals(getString(R.string.session_expired))) {
                        viewModel.resetSession()
                        Common.setUpSessionProgressDialog(this@EditCredentialsActivity)
                    } else
                        Common.setUpAlert(
                            this@EditCredentialsActivity, false,
                            resultTitle,
                            // viewModel.getLangResources().getString(R.string.error),
                            message,
                            viewModel.getLangResources().getString(R.string.ok),
                            null
                        )
                }
            })
    }
}


