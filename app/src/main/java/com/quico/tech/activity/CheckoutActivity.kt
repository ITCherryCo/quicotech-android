package com.quico.tech.activity

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.quico.tech.R
import com.quico.tech.adapter.OrderSummaryRecyclerViewAdapter
import com.quico.tech.adapter.ServicePhotoRecyclerViewAdapter
import com.quico.tech.data.Constant
import com.quico.tech.data.Constant.ORDERS
import com.quico.tech.data.Constant.SERVICE
import com.quico.tech.databinding.ActivityCheckoutBinding
import com.quico.tech.model.Item
import com.quico.tech.utils.Common
import com.quico.tech.viewmodel.SharedViewModel


class CheckoutActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCheckoutBinding
    private lateinit var orderSummaryRecyclerViewAdapter: OrderSummaryRecyclerViewAdapter
    private lateinit var servicePhotoRecyclerViewAdapter: ServicePhotoRecyclerViewAdapter
    private var trackOrder: Boolean? = false
    private val viewModel: SharedViewModel by viewModels()
    private var checkout_type: String = ORDERS

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        trackOrder = intent.extras?.getBoolean(Constant.TRACKING_ON)
        checkout_type = intent.extras?.getString(Constant.CHECKOUT_TYPE)!!

        setUpText()
        initStatusBar()
        manageTracking()
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

    fun initStatusBar(){
        Common.setSystemBarColor(this, R.color.white)
        Common.setSystemBarLight(this)
    }

    private fun manageTracking() {
        binding.apply {
            // visibility of tracking progress
            when (trackOrder) {
                true -> {
                    footerContainer.visibility = View.GONE
                    trackingContainer.visibility = View.VISIBLE
//                    stepView.state.steps(object : ArrayList<String?>() {
//                        init {
//                            add("")
//                            add("")
//                            add("")
//                            add("")
//                        }
//                    })
//                        .stepsNumber(4)
//                        .commit()

//                    val descriptionData = arrayOf(
//                        viewModel.getLangResources().getString(R.string.request_received),
//                        viewModel.getLangResources().getString(R.string.waiting_response),
//                        viewModel.getLangResources().getString(R.string.in_progress),
//                        viewModel.getLangResources().getString(R.string.completed)
//                    )
//                    stateProgressBar.setStateDescriptionData(descriptionData)

                }
                false -> {
                    trackingContainer.visibility = View.GONE
                }
            }

            when (checkout_type) {
                ORDERS -> {
                    setUpItemsAdapter()
                    if (trackOrder == false) {
                        footerContainer.visibility = View.VISIBLE
                    }
                }
                SERVICE -> {
                    footerContainer.visibility = View.GONE
                    setUpServiceAdapter()
                }
            }
        }
    }

    private fun setUpText() {
        binding.apply {
            title.text = viewModel.getLangResources().getString(R.string.checkout)
            paymentMethodText.text = viewModel.getLangResources().getString(R.string.payment_method)
            totalText.text = viewModel.getLangResources().getString(R.string.total)
            confirmBtn.text = viewModel.getLangResources().getString(R.string.confirm_and_pay)

            if (viewModel.getLanguage().equals(Constant.AR))
                backArrow.scaleX = -1f
        }
    }

    fun setUpItemsAdapter() {
        // call the adapter for item list

        binding.apply {
            orderSummaryText.text = viewModel.getLangResources().getString(R.string.order_summary)

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

    fun setUpServiceAdapter() {
        binding.apply {
            orderSummaryText.text = viewModel.getLangResources().getString(R.string.service_name_id)
            serviceDetailsText.visibility = View.VISIBLE

            var serviceDetails = arrayOf("Service Category","Service Type","Case Problem",
                "Delivery Type","Solution and how much it is going to cost","Start Date : DD/MM/YYYY   End Date: DD/MM/YYYY"
            )

            var serviceDetailsValue=""
            for (serviceDetail in serviceDetails){
                serviceDetailsValue+= ". ${serviceDetail}\n"
            }
            serviceDetailsText.text= serviceDetailsValue

                servicePhotoRecyclerViewAdapter = ServicePhotoRecyclerViewAdapter()
            var photos = ArrayList<String>()
            photos.add("https://jakcomputer.com/wp-content/uploads/2022/09/1-26.jpg")
            photos.add("https://jakcomputer.com/wp-content/uploads/2022/09/1-26.jpg")
            photos.add("https://jakcomputer.com/wp-content/uploads/2022/09/1-26.jpg")
            photos.add("https://jakcomputer.com/wp-content/uploads/2022/09/1-26.jpg")
            photos.add("https://jakcomputer.com/wp-content/uploads/2022/09/1-26.jpg")

            recyclerView.layoutManager =
                LinearLayoutManager(this@CheckoutActivity, LinearLayoutManager.HORIZONTAL, false)
            recyclerView.setItemAnimator(DefaultItemAnimator())
            recyclerView.setAdapter(servicePhotoRecyclerViewAdapter)
            servicePhotoRecyclerViewAdapter.differ.submitList(photos)
        }
    }
}