package com.quico.tech.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.activity.viewModels
import com.quico.tech.R
import com.quico.tech.data.Constant
import com.quico.tech.databinding.ActivityEditCredentialsBinding
import com.quico.tech.utils.Common
import com.quico.tech.viewmodel.SharedViewModel

class EditCredentialsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditCredentialsBinding
    private val viewModel: SharedViewModel by viewModels()
    private var edit_profile_type: String = Constant.ORDERS

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditCredentialsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        edit_profile_type = intent.extras?.getString(Constant.PROFILE_EDIT_TYPE)!!
        checkEditType()
        initStatusBar()
    }


    fun initStatusBar(){
        Common.setSystemBarColor(this, R.color.white)
        Common.setSystemBarLight(this)
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
                checkField()
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

                Constant.CHANGE_PASSWORD -> {
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
            }
        }
    }

    private fun checkField() {
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
                    else {
                        // go to verification
                        startActivity(
                            Intent(this@EditCredentialsActivity, VerificationCodeActivity::class.java)
                                .putExtra(Constant.VERIFICATION_TYPE, Constant.PHONE_NUMBER)
                                .putExtra(Constant.OPERATION_TYPE, Constant.CHANGE_PHONE_NUMBER)
                                .putExtra(Constant.PHONE_NUMBER, phoneValue))
                    }
                }

                Constant.EMAIL -> {
                    if (!Patterns.EMAIL_ADDRESS.matcher(emailField.text).matches())
                        emailField.error =
                            viewModel.getLangResources().getString(R.string.wrong_email)
                    else {

                    }
                }

                Constant.PASSWORD -> {

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
                    }
                }
            }
        }
    }
}

