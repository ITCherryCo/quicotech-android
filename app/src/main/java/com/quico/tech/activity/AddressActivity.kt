package com.quico.tech.activity

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.quico.tech.R
import com.quico.tech.data.Constant
import com.quico.tech.data.Constant.ADDRESS
import com.quico.tech.data.Constant.TEMPORAR_ADDRESS
import com.quico.tech.databinding.ActivityAddressBinding
import com.quico.tech.model.Address
import com.quico.tech.model.AddressBodyParameters
import com.quico.tech.utils.Common
import com.quico.tech.viewmodel.SharedViewModel


class AddressActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddressBinding
    private val viewModel: SharedViewModel by viewModels()
    private var address: Address? = null
    private var address_id: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //  address =  intent.extras?.getParcelable(ADDRESS)

        //address = intent.getParcelableExtra(ADDRESS)!! as(Address)
        address = intent.getParcelableExtra<Address>(ADDRESS)
        // Toast.makeText(this,address.toString(),Toast.LENGTH_LONG).show()
        Log.d("ADDRESS", address.toString())

        address = TEMPORAR_ADDRESS
        setUpText()
        initStatusBar()

        address?.let {
            setUpAddress(it)
            Log.d("ADDRESS", "NOT NULL")
            address_id = it.id
        }
    }

    fun initStatusBar() {
        Common.setSystemBarColor(this, R.color.white)
        Common.setSystemBarLight(this)
    }

    private fun setUpText() {

        binding.apply {
            title.text = viewModel.getLangResources().getString(R.string.add_new_address)
            saveBtn.text = viewModel.getLangResources().getString(R.string.save)

            includedAddressFragment.nameField.setText(viewModel.user?.name)
            if (viewModel.getLanguage().equals(Constant.AR))
                backArrow.scaleX = -1f

            saveBtn.setOnClickListener {
                checkFields()
            }
        }
    }

    private fun setUpAddress(address: Address) {

        binding.apply {
            title.text = viewModel.getLangResources().getString(R.string.edit_address)
            includedAddressFragment.apply {
                nameField.setText(address.name)
                streetField.setText(address.street)
                apartmentField.setText(address.street2)
                cityField.setText(address.city)
                postalCodeField.setText(address.zip)
            }
        }
    }

    private fun checkFields() {
        binding.apply {
            includedAddressFragment.apply {

                if (nameField.text.toString().isEmpty())
                    nameField.error =
                        viewModel.getLangResources().getString(R.string.required_field)
                else if (streetField.text.toString().isEmpty())
                    streetField.error =
                        viewModel.getLangResources().getString(R.string.required_field)
                else if (apartmentField.text.toString().isEmpty())
                    apartmentField.error =
                        viewModel.getLangResources().getString(R.string.required_field)
                else if (cityField.text.toString().isEmpty())
                    cityField.error =
                        viewModel.getLangResources().getString(R.string.required_field)
                else if (postalCodeField.text.toString().isEmpty())
                    postalCodeField.error =
                        viewModel.getLangResources().getString(R.string.required_field)
                else {

                    viewModel.user?.let { user ->
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
        Log.d("ADDRESS", "NOT NULL ${address.id}")

        Common.setUpProgressDialog(this)

        viewModel.addEditAddress(
            if (address == null) 0 else address_id,
            addressParams,
            object : SharedViewModel.ResponseStandard {
                override fun onSuccess(success: Boolean, resultTitle: String, message: String) {
                    Common.cancelProgressDialog()
                    Toast.makeText(this@AddressActivity, message, Toast.LENGTH_LONG).show()
                    Constant.TEMPORAR_ADDRESS = null
                    onBackPressed()
                }

                override fun onFailure(success: Boolean, resultTitle: String, message: String) {
                    Common.cancelProgressDialog()
                    Constant.TEMPORAR_ADDRESS = null
                    if (message.equals(getString(R.string.session_expired))) {
                        viewModel.resetSession()
                        Common.setUpSessionProgressDialog(this@AddressActivity)

                    } else
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