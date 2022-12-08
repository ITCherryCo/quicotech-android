package com.quico.tech.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.quico.tech.R
import com.quico.tech.adapter.OrderSummaryRecyclerViewAdapter
import com.quico.tech.data.Constant
import com.quico.tech.databinding.ActivityCheckoutBinding
import com.quico.tech.model.Item
import com.quico.tech.utils.Common
import com.quico.tech.viewmodel.SharedViewModel

class CheckoutActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCheckoutBinding
    private lateinit var orderSummaryRecyclerViewAdapter: OrderSummaryRecyclerViewAdapter
    private var trackOrder:Boolean? = false
    private val viewModel: SharedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        trackOrder = intent.extras?.getBoolean(Constant.TRACK_ORDER)

        setUpCartAdapter()
        setUpText()
       // manageTracking()
        binding.apply {

            confirmBtn.setOnClickListener {
                Common.setUpAlert(
                    this@CheckoutActivity, true, viewModel.getLangResources().getString(
                        R.string.thank_you
                    ),
                    viewModel.getLangResources().getString(
                        R.string.payment_successful
                    ),
                    viewModel.getLangResources().getString(
                        R.string.go_home
                    ), object : Common.ResponseConfirm {
                        override fun onConfirm() {

                        }
                    }
                )
            }

            backArrow.setOnClickListener {
                onBackPressed()
            }
        }
    }

    private fun manageTracking() {
        if (trackOrder!!){

        }
        else{

        }
    }

    private fun setUpText(){
        binding.apply {
            title.text = viewModel.getLangResources().getString(R.string.checkout)
            paymentMethodText.text = viewModel.getLangResources().getString(R.string.payment_method)
            orderSummaryText.text = viewModel.getLangResources().getString(R.string.order_summary)
            totalText.text = viewModel.getLangResources().getString(R.string.total)
            confirmBtn.text = viewModel.getLangResources().getString(R.string.confirm_and_pay)

            if (viewModel.getLanguage().equals(Constant.AR))
                backArrow.scaleX = -1f
        }
    }

    fun setUpCartAdapter() {
        // call the adapter for item list
        binding.apply {

            orderSummaryRecyclerViewAdapter = OrderSummaryRecyclerViewAdapter()
            var items = ArrayList<Item>()

            items.add(Item(1, ""))
            items.add(Item(1, ""))
            items.add(Item(1, ""))
            items.add(Item(1, ""))
            items.add(Item(1, ""))

            recyclerView.layoutManager =
                LinearLayoutManager(this@CheckoutActivity, LinearLayoutManager.VERTICAL, false)
            recyclerView.setItemAnimator(DefaultItemAnimator())
            recyclerView.setAdapter(orderSummaryRecyclerViewAdapter)

            orderSummaryRecyclerViewAdapter.differ.submitList(items)
        }
    }

    private fun setUpCartInfo(){

    }
}