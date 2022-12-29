package com.quico.tech.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.quico.tech.R
import com.quico.tech.adapter.AddressSelectionRecyclerViewAdapter
import com.quico.tech.data.Constant
import com.quico.tech.data.Constant.CONNECTION
import com.quico.tech.data.Constant.EMAIL
import com.quico.tech.data.Constant.ERROR
import com.quico.tech.data.Constant.NO_ADDRESSES
import com.quico.tech.data.Constant.VERIFICATION_TYPE
import com.quico.tech.databinding.ActivityAddressListBinding
import com.quico.tech.model.Address
import com.quico.tech.utils.Common
import com.quico.tech.utils.Resource
import com.quico.tech.viewmodel.SharedViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AddressListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddressListBinding
    private lateinit var addressSelectionRecyclerViewAdapter: AddressSelectionRecyclerViewAdapter
    private val viewModel: SharedViewModel by viewModels()
    private val ADDRESS_TAG = "ADDRESSES_RESPONSE"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address_list)
        binding = ActivityAddressListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            nextBtn.setOnClickListener {
                startActivity(
                    Intent(this@AddressListActivity, VerificationCodeActivity::class.java)
                        .putExtra(VERIFICATION_TYPE, EMAIL)
                        .putExtra(VERIFICATION_TYPE, EMAIL)
                )
            }
            backArrow.setOnClickListener {
                onBackPressed()
            }
        }
        setUpText()
        initStatusBar()
        // setUpCartAdapter()

        onRefresh()
//        viewModel.getAddresses(1)
//        subscribeAddresses()

    }

    fun initStatusBar(){
        Common.setSystemBarColor(this, R.color.white)
        Common.setSystemBarLight(this)
    }

    private fun setUpText() {

        binding.apply {
            title.text = viewModel.getLangResources().getString(R.string.address)
            selectPaymentText.text =
                viewModel.getLangResources().getString(R.string.select_payment_method)
            addNewAddressText.text =
                viewModel.getLangResources().getString(R.string.add_new_address)
            nextBtn.text = viewModel.getLangResources().getString(R.string.next)

            if (viewModel.getLanguage().equals(Constant.AR))
                backArrow.scaleX = -1f
        }
    }

    private fun subscribeAddresses() {
        lifecycleScope.launch {
            viewModel.addresses.collect { response ->
                when (response) {
                    is Resource.Success -> {
                        binding.apply {
                            stopShimmer()
                            recyclerView.visibility = View.VISIBLE
                        }

                        response.data?.let { addressesResponse ->
                            if (addressesResponse.result.isEmpty())
                                setUpErrorForm(NO_ADDRESSES)
                            else {
                                addressSelectionRecyclerViewAdapter.differ.submitList(
                                    addressesResponse.result
                                )
                                binding.recyclerView.setVisibility(View.VISIBLE)
                            }
                        }
                        Log.d(ADDRESS_TAG, "SUCCESS")
                    }

                    is Resource.Error -> {
                        response.message?.let { message ->
                            Log.d(ADDRESS_TAG, "ERROR $message")
                            setUpErrorForm(ERROR)
                        }
                    }

                    is Resource.Connection -> {
                        Log.d(ADDRESS_TAG, "ERROR CONNECTION")
                        setUpErrorForm(CONNECTION)
                    }

                    is Resource.Loading -> {
                        setLoading()
                        Log.d(ADDRESS_TAG, "LOADING")
                    }
                }
            }
        }
    }


    private fun setUpCartAdapter() {
        // call the adapter for item list
        binding.apply {
            stopShimmer()
            addressSelectionRecyclerViewAdapter = AddressSelectionRecyclerViewAdapter()
            recyclerView.visibility = View.VISIBLE

            var addresses = ArrayList<Address>()
           /* addresses.add(Address(1))
            addresses.add(Address(1))
            addresses.add(Address(1))
            addresses.add(Address(1))
            addresses.add(Address(1))*/

            nextBtn.setEnabled(true)

            recyclerView.layoutManager =
                LinearLayoutManager(this@AddressListActivity, LinearLayoutManager.VERTICAL, false)
            recyclerView.setItemAnimator(DefaultItemAnimator())
            recyclerView.setAdapter(addressSelectionRecyclerViewAdapter)

            addressSelectionRecyclerViewAdapter.differ.submitList(addresses)
        }
    }

    private fun stopShimmer() {
        binding.apply {
            shimmer.visibility = View.GONE
            shimmer.stopShimmer()
        }
    }

    private fun setLoading() {
        binding.apply {
            addressErrorContainer.root.visibility = View.GONE
            shimmer.visibility = View.VISIBLE
            shimmer.startShimmer()
            recyclerView.visibility = View.GONE
            nextBtn.setEnabled(false)

            lifecycleScope.launch {
                delay(3000)
                setUpCartAdapter()
            }
        }
    }

    private fun onRefresh() {
        setLoading()
        //viewModel.getAddresses(1)
    }

    fun setUpErrorForm(error_type: String) {
        binding.apply {
            recyclerView.visibility = View.GONE
            stopShimmer()
            addressErrorContainer.apply {
                root.visibility = View.VISIBLE
                tryAgain.visibility = View.VISIBLE
                errorImage.visibility = View.GONE
                errorBtn.visibility = View.GONE
                tryAgain.setText(
                    viewModel.getLangResources().getString(R.string.try_again)
                )
                tryAgain.setOnClickListener {  }

                when (error_type) {
                    Constant.CONNECTION -> {
                        errorMsg1.text =
                            viewModel.getLangResources().getString(R.string.connection)

                        errorMsg2.text =
                            viewModel.getLangResources().getString(R.string.check_connection)

                    }
                    NO_ADDRESSES -> {
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

