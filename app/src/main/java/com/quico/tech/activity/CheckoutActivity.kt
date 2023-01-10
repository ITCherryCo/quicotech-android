package com.quico.tech.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.quico.tech.R
import com.quico.tech.adapter.OrderSummaryRecyclerViewAdapter
import com.quico.tech.adapter.ServicePhotoRecyclerViewAdapter
import com.quico.tech.data.Constant
import com.quico.tech.data.Constant.ORDERS
import com.quico.tech.data.Constant.PRODUCT_LIST
import com.quico.tech.data.Constant.SERVICE
import com.quico.tech.data.Constant.TEMPORAR_ADDRESS
import com.quico.tech.databinding.ActivityCheckoutBinding
import com.quico.tech.model.*
import com.quico.tech.utils.Common
import com.quico.tech.viewmodel.SharedViewModel


class CheckoutActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCheckoutBinding
    private lateinit var orderSummaryRecyclerViewAdapter: OrderSummaryRecyclerViewAdapter
    private lateinit var servicePhotoRecyclerViewAdapter: ServicePhotoRecyclerViewAdapter
    private var trackOrder: Boolean? = false
    private val viewModel: SharedViewModel by viewModels()
    private var checkout_type: String = ORDERS
    private var delivery_order_id: Int? = null
    private var service_order_id: Int? = null

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

            backArrow.setOnClickListener {
                onBackPressed()
            }
        }

        if (delivery_order_id == null && service_order_id == null) {
            setUpPageInfo()
        }
    }


    fun initStatusBar() {
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
            var items = ArrayList<Product>()
            // items.add(Item(1, ""))
            recyclerView.layoutManager =
                LinearLayoutManager(this@CheckoutActivity, LinearLayoutManager.VERTICAL, false)
            recyclerView.setItemAnimator(DefaultItemAnimator())
            recyclerView.setAdapter(orderSummaryRecyclerViewAdapter)

            orderSummaryRecyclerViewAdapter.differ.submitList(items)
        }
    }

    private fun setUpPageInfo() {
        binding.apply {
            cashText.text = viewModel.getLangResources().getString(R.string.cash_with_delivery)
            var addressValue = ""
            TEMPORAR_ADDRESS?.let { address ->
                addressValue = "${address.country}, ${address.city}, ${address.street}"
                if (address.street2.isNotEmpty())
                    addressValue = "${addressValue} , ${address.street2}"

                addressValue = "${addressValue}, ${address.zip}"

                userAddress.text = addressValue
                phoneNumber.text = viewModel.user?.mobile
            }

            orderSummaryRecyclerViewAdapter.differ.submitList(Constant.PRODUCT_LIST!!)
            total.text = "$ ${PRODUCT_LIST!!.map { product ->
                product.new_price
            }.sum()}"

            var productQtyList = ArrayList<ProductParams>()
            PRODUCT_LIST?.let { products->
                products.forEach {
                    productQtyList.add(ProductParams(it.id,it.quantity!!))
                }
            }

            confirmBtn.setOnClickListener {
                val params = OrderBodyParameters(OrderParams(TEMPORAR_ADDRESS!!.id,productQtyList))
                // here must call an api to create order then make an alert to continue shopping
                createDeliveryOrder(params)
            }
        }
    }


    fun setUpServiceAdapter() {
        binding.apply {
            orderSummaryText.text = viewModel.getLangResources().getString(R.string.service_name_id)
            serviceDetailsText.visibility = View.VISIBLE

            var serviceDetails = arrayOf(
                "Service Category",
                "Service Type",
                "Case Problem",
                "Delivery Type",
                "Solution and how much it is going to cost",
                "Start Date : DD/MM/YYYY   End Date: DD/MM/YYYY"
            )

            var serviceDetailsValue = ""
            for (serviceDetail in serviceDetails) {
                serviceDetailsValue += ". ${serviceDetail}\n"
            }
            serviceDetailsText.text = serviceDetailsValue

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

    private fun createDeliveryOrder(params : OrderBodyParameters) {
        Common.setUpChoicesAlert(this,
            viewModel.getLangResources().getString(R.string.checkout),
            viewModel.getLangResources().getString(R.string.sure_to_checkout),
            viewModel.getLangResources().getString(R.string.no),
            viewModel.getLangResources().getString(R.string.yes),
            object : Common.ResponseChoices {
                override fun onConfirm() {
                    Common.setUpProgressDialog(this@CheckoutActivity)
                    viewModel.createDeliveryOrder(params,
                        object : SharedViewModel.ResponseStandard {
                            override fun onSuccess(
                                success: Boolean,
                                resultTitle: String,
                                message: String
                            ) {
                                Common.cancelProgressDialog()
                                viewModel.user = viewModel.user?.copy(have_items_in_cart = false)
                                // later add progress bar to view
                                TEMPORAR_ADDRESS=null
                                PRODUCT_LIST=null
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
                                            startActivity(Intent(this@CheckoutActivity, HomeActivity::class.java)
                                                 .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                            )
                                        }
                                    }
                                )
                            }

                            override fun onFailure(
                                success: Boolean,
                                resultTitle: String,
                                message: String
                            ) {
                                Common.cancelProgressDialog()

                                if (message.equals(resources.getString(R.string.session_expired))) {
                                    viewModel.resetSession()
                                    Common.setUpSessionProgressDialog(this@CheckoutActivity)
                                } else
                                    Common.setUpAlert(
                                        this@CheckoutActivity, false,
                                        viewModel.getLangResources().getString(R.string.error),
                                        message,
                                        viewModel.getLangResources().getString(R.string.ok),
                                        null
                                    )
                            }
                        })
                }

                override fun onCancel() {

                }

            })




    }
}