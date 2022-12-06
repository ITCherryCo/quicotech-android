package com.quico.tech.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.quico.tech.adapter.AddressRecyclerViewAdapter
import com.quico.tech.databinding.ActivityShippingAddressListBinding
import com.quico.tech.model.Address

class ShippingAddressListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityShippingAddressListBinding
    private lateinit var addressRecyclerViewAdapter: AddressRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityShippingAddressListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpCardAdapter()
        binding.apply {

            addAddressContainer.setOnClickListener {
                startActivity(Intent(this@ShippingAddressListActivity, AddressActivity::class.java))
            }

            backArrow.setOnClickListener {
                onBackPressed()
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

            recyclerView.layoutManager =
                LinearLayoutManager(
                    this@ShippingAddressListActivity,
                    LinearLayoutManager.VERTICAL,
                    false
                )
            recyclerView.setItemAnimator(DefaultItemAnimator())
            recyclerView.setAdapter(addressRecyclerViewAdapter)

            addressRecyclerViewAdapter.differ.submitList(addresses)
        }
    }
}