package com.quico.tech.activity

import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.quico.tech.R
import com.quico.tech.adapter.AddressRecyclerViewAdapter
import com.quico.tech.data.Constant
import com.quico.tech.data.Constant.DOOR_TO_DOOR
import com.quico.tech.data.Constant.DROP_CENTER
import com.quico.tech.databinding.ActivityRequestDeliveryBinding
import com.quico.tech.model.Address
import com.quico.tech.utils.Common
import com.quico.tech.utils.Resource
import com.quico.tech.viewmodel.SharedViewModel
import kotlinx.coroutines.launch


class RequestDeliveryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRequestDeliveryBinding
    private lateinit var addressRecyclerViewAdapter: AddressRecyclerViewAdapter
    private val viewModel: SharedViewModel by viewModels()
    private var delivery_type= ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRequestDeliveryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpText()
        setUpText()
        setUpAdressesAdapter()

        monitorRadioBtns()
        initStatusBar()
    }

    fun initStatusBar(){
        Common.setSystemBarColor(this, R.color.white)
        Common.setSystemBarLight(this)
    }

    private fun setUpText() {
        binding.apply {
            //title.text = viewModel.getLangResources().getString(R.string.shipping_addresses)
            submitBtn.text = viewModel.getLangResources().getString(R.string.submit_request)
            addressListFragment.addNewAddressText.text = viewModel.getLangResources().getString(R.string.add_new_address)

            if (viewModel.getLanguage().equals(Constant.AR))
                backArrow.scaleX = -1f

            backArrow.setOnClickListener {
                onBackPressed()
            }

            submitBtn.setOnClickListener {
            setUpConfirmAlert()

            }
        }
    }

    override fun onResume() {
        super.onResume()
        subscribeAddresses()
        viewModel.user?.let {
            viewModel.getAddresses(false)
        }
    }

    private fun setUpConfirmAlert() {
        if (delivery_type.isEmpty()) {
            Common.setUpAlert(
                this@RequestDeliveryActivity, false, viewModel.getLangResources().getString(
                    R.string.delivery_type
                ),
                viewModel.getLangResources().getString(
                    R.string.choose_delivery_type
                ),
                viewModel.getLangResources().getString(
                    R.string.ok
                ), object : Common.ResponseConfirm {
                    override fun onConfirm() {
                    }
                }
            )
        }
        else{
            Common.setUpAlert(
                this@RequestDeliveryActivity, true, viewModel.getLangResources().getString(
                    R.string.thank_you
                ),
                viewModel.getLangResources().getString(
                    R.string.request_submitted
                ),
                viewModel.getLangResources().getString(
                    R.string.continue_shopping
                ), object : Common.ResponseConfirm {
                    override fun onConfirm() {

                    }
                }
            )
        }
    }


    private fun monitorRadioBtns(){
        binding.apply {

            addressFragment.root.visibility = View.GONE
            addressListFragment.root.visibility = View.GONE

            addressListFragment.addAddressContainer.setOnClickListener {
                addressFragment.root.visibility = View.VISIBLE
            }

            doorRadioBtn.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
                override fun onCheckedChanged(p0: CompoundButton?, checked: Boolean) {
                    if (checked){
                        addressListFragment.root.visibility = View.VISIBLE
                        viewModel.getAddresses(true)
                        centerRadioBtn.setChecked(false)
                        delivery_type = DOOR_TO_DOOR
                    }
                }
            })

            centerRadioBtn.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
                override fun onCheckedChanged(p0: CompoundButton?, checked: Boolean) {
                    if (checked) {
                        addressFragment.root.visibility = View.GONE
                        addressListFragment.root.visibility = View.GONE
                        doorRadioBtn.setChecked(false)
                        delivery_type = DROP_CENTER
                    }
                }
            })
        }
    }

    fun subscribeAddresses() {
        lifecycleScope.launch {
            viewModel.addresses.collect { response ->
                when (response) {

                    is Resource.Success -> {
                       // stopShimmer()

                        response.data?.let { addressesResponse ->

                            binding.progressBar.visibility = View.GONE
                            binding.addressListFragment.root.visibility = View.VISIBLE

                            if (addressesResponse.result.isNullOrEmpty()) {
                                setUpAddressErrorForm(Constant.NO_ADDRESSES)
                            } else {
                                if (addressesResponse.result.size<3)
                                    binding.addressListFragment.addAddressContainer.setEnabled(true)

                                addressRecyclerViewAdapter.differ.submitList(addressesResponse.result)
                                binding.addressListFragment.recyclerView.visibility = View.VISIBLE
                            }
                        }
                    }

                    is Resource.Error -> {
                        response.message?.let { message ->
                            setUpAddressErrorForm(Constant.ERROR)
                        }
                    }

                    is Resource.Connection -> {
                        setUpAddressErrorForm(Constant.CONNECTION)
                    }

                    is Resource.Loading -> {
                        setLoading()
                    }
                }
            }
        }
    }


    fun setUpAdressesAdapter() {
        binding.apply {
            addressRecyclerViewAdapter = AddressRecyclerViewAdapter(true,object :AddressRecyclerViewAdapter.OnAddressSelect{


                override fun onAddressSelect(id: Int) {
                }

                override fun OnEditAddress(address: Address?) {
                }
            },viewModel)

            var addresses = ArrayList<Address>()

            addressListFragment.recyclerView.layoutManager =
                LinearLayoutManager(
                    this@RequestDeliveryActivity,
                    LinearLayoutManager.VERTICAL,
                    false
                )
            addressListFragment.recyclerView.setItemAnimator(DefaultItemAnimator())
            addressListFragment.recyclerView.setAdapter(addressRecyclerViewAdapter)

            addressRecyclerViewAdapter.differ.submitList(addresses)
        }
    }

    private fun onRefresh() {
        setLoading()
        viewModel.getAddresses(false)
    }
    private fun setLoading() {
        binding.apply {
            addressErrorContainer.root.visibility = View.GONE
            addressListFragment.root.visibility = View.GONE
            progressBar.visibility = View.VISIBLE
            addressListFragment.addAddressContainer.setEnabled(false)
        }
    }

  private fun setUpAddressErrorForm(error_type: String) {
        binding.apply {
            addressListFragment.recyclerView.visibility = View.GONE
            addressErrorContainer.apply {
                root.visibility = View.VISIBLE
                tryAgain.visibility = View.VISIBLE
                errorImage.visibility = View.GONE
                errorBtn.visibility = View.GONE
                tryAgain.setText(
                    viewModel.getLangResources().getString(R.string.try_again)
                )
                tryAgain.setOnClickListener {
                    onRefresh()
                }

                when (error_type) {
                    Constant.CONNECTION -> {
                        errorMsg1.text =
                            viewModel.getLangResources().getString(R.string.connection)

                        errorMsg2.text =
                            viewModel.getLangResources().getString(R.string.check_connection)

                    }
                    Constant.NO_ADDRESSES -> {
                        errorMsg1.text =
                            viewModel.getLangResources().getString(R.string.address)

                        errorMsg2.text =
                            viewModel.getLangResources().getString(R.string.no_addresses)
                    }

                    Constant.ERROR -> {
                        errorMsg1.text =
                            viewModel.getLangResources().getString(R.string.error)

                        errorMsg2.text =
                            viewModel.getLangResources().getString(R.string.error_msg)
                    }
                }
            }
        }
    }

}