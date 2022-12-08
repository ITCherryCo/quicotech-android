package com.quico.tech.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.quico.tech.R
import com.quico.tech.adapter.AddressSelectionRecyclerViewAdapter
import com.quico.tech.data.Constant
import com.quico.tech.data.Constant.CONNECTION
import com.quico.tech.data.Constant.EMAIL
import com.quico.tech.data.Constant.ERROR
import com.quico.tech.data.Constant.NO_ADDRESSES
import com.quico.tech.data.Constant.OPERATION_TYPE
import com.quico.tech.databinding.ActivityAddressListBinding
import com.quico.tech.model.Address
import com.quico.tech.utils.Resource
import com.quico.tech.viewmodel.SharedViewModel
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

                startActivity(Intent(this@AddressListActivity, VerificationCodeActivity::class.java)
                    .putExtra(OPERATION_TYPE, EMAIL))
            }
            backArrow.setOnClickListener {
                onBackPressed()
            }
        }
        setUpText()
        setUpCartAdapter()
//        onRefresh()
//        viewModel.getAddresses(1)
//        subscribeAddresses()

    }

    private fun setUpText(){
        binding.apply {
            title.text = viewModel.getLangResources().getString(R.string.address)
            selectPaymentText.text = viewModel.getLangResources().getString(R.string.select_payment_method)
            addNewAddressText.text = viewModel.getLangResources().getString(R.string.add_new_address)
            nextBtn.text = viewModel.getLangResources().getString(R.string.next)

            if (viewModel.getLanguage().equals(Constant.AR))
                backArrow.scaleX = -1f
        }
    }

    fun subscribeAddresses(){
        lifecycleScope.launch {
            viewModel.addresses.collect { response ->
                when (response) {
                    is Resource.Success -> {
                        binding.apply {
                            progressBar.visibility = View.GONE
                                recyclerView.visibility = View.VISIBLE
                        }

                        response.data?.let { addressesResponse ->
                            if (addressesResponse.addresses.isEmpty())
                                setUpErrorForm(NO_ADDRESSES)
                            else {
                                addressSelectionRecyclerViewAdapter.differ.submitList(addressesResponse.addresses)
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


    fun setUpCartAdapter() {
        // call the adapter for item list
        binding.apply {

            addressSelectionRecyclerViewAdapter = AddressSelectionRecyclerViewAdapter()
            var addresses = ArrayList<Address>()
            addresses.add(Address(1))
            addresses.add(Address(1))
            addresses.add(Address(1))
            addresses.add(Address(1))
            addresses.add(Address(1))

            recyclerView.layoutManager =
                LinearLayoutManager(this@AddressListActivity, LinearLayoutManager.VERTICAL, false)
            recyclerView.setItemAnimator(DefaultItemAnimator())
            recyclerView.setAdapter(addressSelectionRecyclerViewAdapter)

            addressSelectionRecyclerViewAdapter.differ.submitList(addresses)
        }
    }

    fun setLoading() {
        binding.apply {
            ErrorContainer.visibility = View.GONE
            progressBar.visibility = View.GONE
            recyclerView.visibility = View.GONE
            nextBtn.setEnabled(false)
        }
    }

    fun onRefresh() {
        binding.apply {
            ErrorContainer.visibility = View.GONE
            progressBar.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
            nextBtn.setEnabled(false)
        }
        viewModel.getAddresses(1)
    }

    fun setUpErrorForm(error_type: String) {
        binding.apply {
            progressBar.visibility = View.GONE
            ErrorContainer.visibility = View.VISIBLE
            selectPaymentText.visibility = View.GONE
            recyclerView.visibility = View.GONE

            when(error_type){
                CONNECTION->errorText.setText(viewModel.getLangResources().getString(R.string.check_connection))
                NO_ADDRESSES->errorText.setText(viewModel.getLangResources().getString(R.string.no_addresses))
                ERROR->errorText.setText(viewModel.getLangResources().getString(R.string.error_msg))
            }
        }
    }
}