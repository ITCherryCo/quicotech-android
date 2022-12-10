package com.quico.tech.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.quico.tech.R
import com.quico.tech.adapter.AddressRecyclerViewAdapter
import com.quico.tech.data.Constant
import com.quico.tech.databinding.ActivityShippingAddressBinding
import com.quico.tech.model.Address
import com.quico.tech.viewmodel.SharedViewModel

class ShippingAddressActivity : AppCompatActivity() {
    private lateinit var binding: ActivityShippingAddressBinding
    private lateinit var addressRecyclerViewAdapter: AddressRecyclerViewAdapter
    private val viewModel: SharedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityShippingAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpText()
        setUpCardAdapter()
    }
    private fun setUpText() {
        binding.apply {
            title.text = viewModel.getLangResources().getString(R.string.shipping_addresses)
            includedFragment.addNewAddressText.text = viewModel.getLangResources().getString(R.string.add_new_address)

            if (viewModel.getLanguage().equals(Constant.AR))
                backArrow.scaleX = -1f

            backArrow.setOnClickListener {
                onBackPressed()
            }

            includedFragment.addAddressContainer.setOnClickListener {
            }
        }
    }

    fun setUpCardAdapter() {
        binding.apply {
            addressRecyclerViewAdapter = AddressRecyclerViewAdapter()
            var addresses = ArrayList<Address>()
            addresses.add(Address(1))
            addresses.add(Address(1))
            addresses.add(Address(1))

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
}