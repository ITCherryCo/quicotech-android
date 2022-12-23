package com.quico.tech.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.quico.tech.R
import com.quico.tech.data.Constant
import com.quico.tech.databinding.ActivityEditEmailPhoneBinding
import com.quico.tech.viewmodel.SharedViewModel

class EditEmailPhoneActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditEmailPhoneBinding
    private val viewModel: SharedViewModel by viewModels()
    private var edit_profile_type: String = Constant.ORDERS

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditEmailPhoneBinding.inflate(layoutInflater)
        setContentView(binding.root)
        edit_profile_type = intent.extras?.getString(Constant.PROFILE_EDIT_TYPE)!!
        checkEditType()
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

            when (edit_profile_type) {
                Constant.PHONE_NUMBER -> {
                    emailMobileText.text = viewModel.getLangResources().getString(R.string.mobile_number)
                    phoneField.hint = viewModel.getLangResources().getString(R.string.phone_number_example)
                    phoneContainer.visibility = View.VISIBLE
                    emailField.visibility = View.GONE
                    passwordContainer.visibility = View.GONE
                }

                Constant.EMAIL -> {
                    emailMobileText.text = viewModel.getLangResources().getString(R.string.email_address)
                    emailField.hint = viewModel.getLangResources().getString(R.string.email_example)
                    phoneContainer.visibility = View.GONE
                    passwordContainer.visibility = View.GONE
                    emailField.visibility = View.VISIBLE
                }

                Constant.PASSWORD -> {
                    emailMobileText.text = viewModel.getLangResources().getString(R.string.change_current_password)
                    phoneContainer.visibility = View.GONE
                    emailField.visibility = View.GONE
                    passwordContainer.visibility = View.VISIBLE
                    passwordText.text = viewModel.getLangResources().getString(R.string.new_password)
                    passwordField.hint = viewModel.getLangResources().getString(R.string.new_password)
                    confirmPasswordText.text = viewModel.getLangResources().getString(R.string.confirm_password)
                    confirmPasswordField.hint = viewModel.getLangResources().getString(R.string.confirm_password)
                }
            }
        }
    }

    private fun checkField(){

    }
}