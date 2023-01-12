package com.quico.tech.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.quico.tech.R
import com.quico.tech.adapter.OrderSummaryRecyclerViewAdapter
import com.quico.tech.adapter.ServicePhotoRecyclerViewAdapter
import com.quico.tech.data.Constant
import com.quico.tech.data.Constant.DELIVERY_ORDERS
import com.quico.tech.data.Constant.PRODUCT_LIST
import com.quico.tech.data.Constant.SERVICE
import com.quico.tech.data.Constant.SERVICE_ORDERS
import com.quico.tech.data.Constant.TEMPORAR_ADDRESS
import com.quico.tech.databinding.ActivityCheckoutBinding
import com.quico.tech.model.*
import com.quico.tech.utils.Common
import com.quico.tech.utils.Resource
import com.quico.tech.viewmodel.SharedViewModel
import kotlinx.coroutines.launch


class CheckoutActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCheckoutBinding
    private lateinit var orderSummaryRecyclerViewAdapter: OrderSummaryRecyclerViewAdapter
    private lateinit var servicePhotoRecyclerViewAdapter: ServicePhotoRecyclerViewAdapter
    private var trackOrder: Boolean? = false
    private val viewModel: SharedViewModel by viewModels()
    private var order_type: String = DELIVERY_ORDERS
    private var order_id: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        trackOrder = intent.extras?.getBoolean(Constant.TRACKING_ON)
        order_type = intent.extras?.getString(Constant.ORDER_TYPE)!!
        order_id = intent.extras?.getInt(Constant.ORDER_ID)

        setUpText()
        initStatusBar()
        manageTracking()
        checkOrderType()

        onRefresh()
    }


    fun initStatusBar() {
        Common.setSystemBarColor(this, R.color.white)
        Common.setSystemBarLight(this)
    }

    private fun checkOrderType() {

        when (order_type) {
            DELIVERY_ORDERS -> {
                subscribeOrder()
                viewModel.getDeliveryOrderById(order_id!!)
            }
            else -> {
                setUpCheckoutInfo()
                binding.swipeRefreshLayout.setEnabled(false)
            }
        }
    }

    private fun manageTracking() {
        binding.apply {
            // visibility of tracking progress
            when (trackOrder) {
                true -> {
                    footerContainer.visibility = View.VISIBLE
                    confirmBtn.visibility = View.GONE
                    orderTracking.root.visibility = View.VISIBLE
                    title.text = viewModel.getLangResources().getString(R.string.tracking)

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

                }
                false -> {
                    orderTracking.root.visibility = View.GONE
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

            backArrow.setOnClickListener {
                onBackPressed()
            }
        }
    }

    fun setUpProductsAdapter(products: List<Product>) {
        // call the adapter for item list

        binding.apply {
            orderSummary.visibility = View.VISIBLE
            orderSummaryText.text = viewModel.getLangResources().getString(R.string.order_summary)

            orderSummaryRecyclerViewAdapter = OrderSummaryRecyclerViewAdapter()

            recyclerView.layoutManager =
                LinearLayoutManager(this@CheckoutActivity, LinearLayoutManager.VERTICAL, false)
            recyclerView.setItemAnimator(DefaultItemAnimator())
            recyclerView.setAdapter(orderSummaryRecyclerViewAdapter)

            orderSummaryRecyclerViewAdapter.differ.submitList(products)
        }
    }

    private fun setUpCheckoutInfo() {
        binding.apply {
            title.text = viewModel.getLangResources().getString(R.string.delivery_order)

            cashText.text = viewModel.getLangResources().getString(R.string.cash_on_delivery)

            footerContainer.visibility = View.VISIBLE

            setUpProductsAdapter(Constant.PRODUCT_LIST!!)
            setUpAddressInfo(TEMPORAR_ADDRESS)
            total.text = "$ ${
                PRODUCT_LIST!!.map { product ->
                    product.subtotal
                }.sum()
            }"

            var productQtyList = ArrayList<ProductParams>()
            PRODUCT_LIST?.let { products ->
                products.forEach {
                    productQtyList.add(ProductParams(it.id, it.quantity!!))
                }
            }

            confirmBtn.setOnClickListener {
                val params = OrderBodyParameters(OrderParams(TEMPORAR_ADDRESS!!.id, productQtyList))
                // here must call an api to create order then make an alert to continue shopping
                createDeliveryOrder(params)
            }
        }
    }

    private fun setUpAddressInfo(order_address: Address?) {
        binding.apply {
            var addressValue = ""
            order_address?.let { address ->
                addressValue =
                    "${if (address.country == null) "Lebanon" else address.country}, ${address.city}, ${address.street}"
                if (address.street2.isNotEmpty())
                    addressValue = "${addressValue} , ${address.street2}"

                addressValue = "${addressValue}, ${address.zip}"

                orderAddress.userAddress.text = addressValue
                orderAddress.phoneNumber.text = viewModel.user?.mobile
            }
        }
    }

    private fun checkPaymentMethod(payment_method: String) {
        binding.apply {
            when (payment_method) {
                "cash on delivery" -> {
                    cashContainer.visibility = View.VISIBLE
                    visaContainer.visibility = View.GONE
                }
                "visa" -> {
                    cashContainer.visibility = View.GONE
                    visaContainer.visibility = View.VISIBLE
                }
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

    private fun createDeliveryOrder(params: OrderBodyParameters) {
        Common.setUpChoicesAlert(this,
            viewModel.getLangResources().getString(R.string.checkout),
            viewModel.getLangResources().getString(R.string.sure_to_checkout),
            viewModel.getLangResources().getString(R.string.no),
            viewModel.getLangResources().getString(R.string.yes),
            object : Common.ResponseChoices {
                override fun onConfirm() {
                    Common.setUpProgressDialog(this@CheckoutActivity)
                    binding.confirmBtn.setEnabled(false)
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
                                TEMPORAR_ADDRESS = null
                                PRODUCT_LIST = null
                                Common.setUpAlert(
                                    this@CheckoutActivity,
                                    true,
                                    viewModel.getLangResources().getString(
                                        R.string.thank_you
                                    ),
                                    viewModel.getLangResources().getString(
                                        R.string.payment_successful
                                    ),
                                    viewModel.getLangResources().getString(
                                        R.string.go_home
                                    ),
                                    object : Common.ResponseConfirm {
                                        override fun onConfirm() {
                                            startActivity(
                                                Intent(
                                                    this@CheckoutActivity,
                                                    HomeActivity::class.java
                                                )
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
                                binding.confirmBtn.setEnabled(true)

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


    fun subscribeOrder() {
        lifecycleScope.launch {
            viewModel.delivery_order.collect { response ->
                when (response) {

                    is Resource.Success -> {

                        binding.apply {
                            mainContainer.visibility = View.VISIBLE
                            orderErrorContainer.root.visibility = View.GONE
                            swipeRefreshLayout.setRefreshing(false)
                            // totalContainer.visibility = View.GONE
                            //confirmBtn.visibility = View.GONE
                        }

                        response.data?.let { orderResponse ->

                            if (orderResponse.result == null) {
                                setUpErrorForm(Constant.NO_ORDERS)
                            } else {
                                orderResponse.result?.let { result ->
                                    binding.apply {

                                        if (result.order_lines.isNullOrEmpty()) {
                                            orderSummary.visibility = View.GONE
                                        } else {
                                            setUpProductsAdapter(result.order_lines)
                                        }
                                        if (result.address != null)
                                            setUpAddressInfo(result.address)
                                        else
                                            orderAddress.root.visibility = View.GONE

                                        checkPaymentMethod(result.payment_method)
                                        binding.apply {
                                            confirmBtn.visibility = View.GONE
                                            total.text = "$ ${result.total_price}"

                                            orderTracking.stateProgressBar.setCurrentStateNumber(
                                                OrderStatus.getOrderState(
                                                    result.status
                                                )
                                            )
                                        }
                                }
                            }
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

private fun setLoading() {
    binding.apply {
        mainContainer.visibility = View.GONE
        orderErrorContainer.root.visibility = View.GONE
        totalContainer.visibility = View.GONE
        confirmBtn.visibility = View.GONE
        swipeRefreshLayout.setRefreshing(true)
        //shimmer.visibility = View.VISIBLE
        // shimmer.startShimmer()
    }
}


fun onRefresh() {
    binding.apply {
        swipeRefreshLayout.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
            checkOrderType()
        })
    }
}

fun setUpErrorForm(error_type: String) {
    binding.apply {
        footerContainer.visibility = View.GONE
        mainContainer.visibility = View.GONE
        swipeRefreshLayout.setRefreshing(false)
        //stopShimmer()
        orderErrorContainer.apply {
            root.visibility = View.VISIBLE
            tryAgain.visibility = View.GONE
            errorImage.visibility = View.VISIBLE
            errorBtn.visibility = View.GONE

            errorImage.setImageResource(R.drawable.empty_item)

            when (error_type) {
                Constant.CONNECTION -> {
                    errorMsg1.text =
                        viewModel.getLangResources().getString(R.string.connection)

                    errorMsg2.text =
                        viewModel.getLangResources().getString(R.string.check_connection)

                }
                Constant.NO_ORDERS -> {
                    errorMsg1.text =
                        viewModel.getLangResources().getString(R.string.orders)

                    errorMsg2.text =
                        viewModel.getLangResources().getString(R.string.no_orders)
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