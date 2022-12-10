package com.quico.tech.activity

import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
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
import com.quico.tech.viewmodel.SharedViewModel


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
        setUpCardAdapter()
        monitorRadioBtns()
    }
    private fun setUpText() {
        binding.apply {
            //title.text = viewModel.getLangResources().getString(R.string.shipping_addresses)
            submitBtn.text = viewModel.getLangResources().getString(R.string.submit_request)
            addressListFragment.addNewAddressText.text = viewModel.getLangResources().getString(R.string.add_new_card)

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
    fun setUpCardAdapter() {
        binding.apply {
            addressRecyclerViewAdapter = AddressRecyclerViewAdapter()
            var addresses = ArrayList<Address>()
            addresses.add(Address(1))
            addresses.add(Address(1))
            addresses.add(Address(1))

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

}