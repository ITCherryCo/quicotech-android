package com.quico.tech.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.quico.tech.R
import com.quico.tech.adapter.AddressRecyclerViewAdapter
import com.quico.tech.data.Constant
import com.quico.tech.databinding.ActivityShippingAddressBinding
import com.quico.tech.model.Address
import com.quico.tech.utils.Common
import com.quico.tech.utils.Resource
import com.quico.tech.viewmodel.SharedViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ShippingAddressActivity : AppCompatActivity() {
    private lateinit var binding: ActivityShippingAddressBinding
    private lateinit var addressRecyclerViewAdapter: AddressRecyclerViewAdapter
    private val viewModel: SharedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityShippingAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpText()
        initStatusBar()
        subscribeAddresses()
        viewModel.getAddresses()
    }

    fun initStatusBar(){
        Common.setSystemBarColor(this, R.color.white)
        Common.setSystemBarLight(this)
    }

    private fun setUpText() {
        binding.apply {
            title.text = viewModel.getLangResources().getString(R.string.shipping_addresses)
            includedFragment.addNewAddressText.text =
                viewModel.getLangResources().getString(R.string.add_new_address)

            if (viewModel.getLanguage().equals(Constant.AR))
                backArrow.scaleX = -1f

            backArrow.setOnClickListener {
                onBackPressed()
            }

            includedFragment.addAddressContainer.setOnClickListener {
                startActivity(
                    Intent(this@ShippingAddressActivity, AddressActivity::class.java)
                        //.putExtra(Constant.OPERATION_TYPE, Constant.REGISTER)
                )
            }
        }
    }

    fun subscribeAddresses() {
        lifecycleScope.launch {
            viewModel.addresses.collect { response ->
                when (response) {

                    is Resource.Success -> {
                        stopShimmer()
                        binding.swipeRefreshLayout.setRefreshing(false)

                        response.data?.let { addressesResponse ->

                            if (addressesResponse.result.isNullOrEmpty()) {
                                setUpErrorForm(Constant.NO_ADDRESSES)
                            } else {
                                addressRecyclerViewAdapter.differ.submitList(addressesResponse.result)
                                binding.includedFragment.recyclerView.visibility = View.VISIBLE
                            }
                        }
                    }

                    is Resource.Error -> {
                        response.message?.let { message ->
                            setUpErrorForm(Constant.ERROR)
                        }
                    }

                    is Resource.Connection -> {
                        setUpErrorForm(Constant.CONNECTION)
                    }

                    is Resource.Loading -> {
                        setLoading()
                    }
                }
            }
        }
    }

    fun setUpAddressAdapter() {
        binding.apply {
            addressRecyclerViewAdapter = AddressRecyclerViewAdapter()

            var addresses = ArrayList<Address>()
         /*   addresses.add(Address(1))
            addresses.add(Address(1))
            addresses.add(Address(1))*/

            includedFragment.recyclerView.layoutManager =
                LinearLayoutManager(
                    this@ShippingAddressActivity,
                    LinearLayoutManager.VERTICAL,
                    false
                )
            includedFragment.recyclerView.setItemAnimator(DefaultItemAnimator())
            includedFragment.recyclerView.setAdapter(addressRecyclerViewAdapter)

            addressRecyclerViewAdapter.differ.submitList(addresses)
        }
    }

    private fun stopShimmer() {
        binding.apply {
            includedFragment.shimmer.visibility = View.GONE
            includedFragment.shimmer.stopShimmer()
        }
    }

    fun setLoading() {
        binding.apply {
            includedFragment.recyclerView.visibility = View.GONE
            includedFragment.shippingAddressErrorContainer.root.visibility = View.GONE
            includedFragment.shimmer.visibility = View.VISIBLE
            includedFragment.shimmer.startShimmer()
            swipeRefreshLayout.setRefreshing(false)

          /*  lifecycleScope.launch {
                delay(3000)
                setUpAddressAdapter()
            }*/
        }
    }

    fun onRefresh() {
        binding.apply {
            swipeRefreshLayout.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
                setLoading() // later we will remove it because the observable will call it
                 viewModel.getAddresses() // get User id
            })
        }
    }

    fun setUpErrorForm(error_type: String) {
        binding.apply {
            swipeRefreshLayout.setRefreshing(false)
            includedFragment.recyclerView.visibility = View.GONE

            stopShimmer()
            swipeRefreshLayout.setEnabled(true)
            includedFragment.shippingAddressErrorContainer.apply {
                errorMsg1.visibility = View.GONE
                tryAgain.visibility = View.GONE
                errorBtn.visibility = View.GONE
                root.visibility = View.VISIBLE
                errorImage.setImageResource(android.R.color.transparent)
                when (error_type) {
                    Constant.CONNECTION -> {
                        errorMsg2.setText(
                            viewModel.getLangResources().getString(R.string.check_connection)
                        )
                        errorImage.setImageResource(R.drawable.empty_item)

                    }
                    Constant.NO_ADDRESSES -> {
                        errorMsg2.text =
                            viewModel.getLangResources().getString(R.string.no_addresses)
                        errorImage.setImageResource(R.drawable.empty_item)
                    }

                    Constant.ERROR -> {
                        errorMsg2.setText(
                            viewModel.getLangResources().getString(R.string.error_msg)
                        )
                        errorImage.setImageResource(R.drawable.empty_item)

                    }
                }
            }
            }
        }
    }