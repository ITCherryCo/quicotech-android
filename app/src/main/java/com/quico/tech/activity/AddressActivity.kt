package com.quico.tech.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import com.quico.tech.R
import com.quico.tech.data.Constant
import com.quico.tech.databinding.ActivityAddressBinding
import com.quico.tech.model.Address
import com.quico.tech.model.AddressBodyParameters
import com.quico.tech.model.RegisterBodyParameters
import com.quico.tech.model.RegisterParams
import com.quico.tech.utils.Common
import com.quico.tech.viewmodel.SharedViewModel

class AddressActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddressBinding
    private val viewModel: SharedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setUpText()
        initStatusBar()

    }

    fun initStatusBar() {
        Common.setSystemBarColor(this, R.color.white)
        Common.setSystemBarLight(this)
    }

    private fun setUpText() {

        binding.apply {
            title.text = viewModel.getLangResources().getString(R.string.add_new_address)
            saveBtn.text = viewModel.getLangResources().getString(R.string.save)

            if (viewModel.getLanguage().equals(Constant.AR))
                backArrow.scaleX = -1f

            saveBtn.setOnClickListener {
                checkFields()
            }
        }
    }

    private fun checkFields() {
        binding.apply {
            includedAddressFragment.apply {

                if (nameField.text.toString().isEmpty())
                    nameField.error =
                        viewModel.getLangResources().getString(R.string.required_field)

                if (streetField.text.toString().isEmpty())
                    streetField.error =
                        viewModel.getLangResources().getString(R.string.required_field)

                if (apartmentField.text.toString().isEmpty())
                    apartmentField.error =
                        viewModel.getLangResources().getString(R.string.required_field)

                if (cityField.text.toString().isEmpty())
                    cityField.error =
                        viewModel.getLangResources().getString(R.string.required_field)
                if (postalCodeField.text.toString().isEmpty())
                    postalCodeField.error =
                        viewModel.getLangResources().getString(R.string.required_field)
                else {

                    viewModel.user?.let {user->
                        var address = Address(
                            cityField.text.toString(),
                            user.name,
                            streetField.text.toString(),
                            streetField.text.toString(),
                            cityField.text.toString(),
                            postalCodeField.text.toString(),
                        )
                        addAddress(address)
                    }
                }
            }
        }
    }

    private fun addAddress(address: Address) {

        val addressParams = AddressBodyParameters(
            address
        )
        Common.setUpProgressDialog(this)

        viewModel.addAddress(addressParams,object :SharedViewModel.ResponseStandard{
            override fun onSuccess(success: Boolean, resultTitle: String, message: String) {
                Common.cancelProgressDialog()
                Toast.makeText(this@AddressActivity,message,Toast.LENGTH_LONG).show()
                onBackPressed()
            }

            override fun onFailure(success: Boolean, resultTitle: String, message: String) {
                Common.cancelProgressDialog()
                Common.setUpAlert(
                    this@AddressActivity, false,
                    viewModel.getLangResources().getString(R.string.error),
                    message,
                    viewModel.getLangResources().getString(R.string.ok),
                    null
                )
            }
        })
    }
}