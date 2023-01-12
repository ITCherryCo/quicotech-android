package com.quico.tech.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.quico.tech.R
import com.quico.tech.adapter.OrderSummaryRecyclerViewAdapter
import com.quico.tech.adapter.ServicePhotoRecyclerViewAdapter
import com.quico.tech.data.Constant
import com.quico.tech.databinding.ActivityServiceOrderBinding
import com.quico.tech.model.*
import com.quico.tech.utils.Common
import com.quico.tech.utils.Resource
import com.quico.tech.viewmodel.SharedViewModel
import kotlinx.coroutines.launch

class ServiceOrderActivity : AppCompatActivity() {
    private lateinit var binding: ActivityServiceOrderBinding
    private lateinit var servicePhotoRecyclerViewAdapter: ServicePhotoRecyclerViewAdapter
    private var trackOrder: Boolean? = false
    private val viewModel: SharedViewModel by viewModels()
    private var order_id: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityServiceOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        trackOrder = intent.extras?.getBoolean(Constant.TRACKING_ON)
        order_id = intent.extras?.getInt(Constant.ORDER_ID)!!

        setUpText()
        initStatusBar()
        manageTracking()
        onRefresh()
        subscribeServiceOrder()
        viewModel.getServiceOrderById(order_id)
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
                    title.text = viewModel.getLangResources().getString(R.string.service_order)

                    orderTracking.root.visibility = View.GONE
                }
            }
        }
    }

    private fun setUpText() {
        binding.apply {

            if (viewModel.getLanguage().equals(Constant.AR))
                backArrow.scaleX = -1f

            backArrow.setOnClickListener {
                onBackPressed()
            }

            viewModel.user?.let { user ->
                username.text = user.name
                email.text = user.email
                phoneNumber.text = user.mobile
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

                serviceOrderAddress.userAddress.text = addressValue
                serviceOrderAddress.phoneNumber.text = viewModel.user?.mobile
            }
        }
    }

    fun subscribeServiceOrder() {
        lifecycleScope.launch {
            viewModel.service_order.collect { response ->
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
                                        if (result.address != null)
                                            setUpAddressInfo(result.address)
                                        else
                                            serviceOrderAddress.root.visibility = View.GONE

                                        orderTracking.stateProgressBar.setCurrentStateNumber(
                                            OrderStatus.getOrderState(
                                                result.status
                                            )
                                        )
                                        setUpServiceAdapter(result)
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

    fun setUpServiceAdapter(serviceOrder: ServiceOrder) {
        binding.apply {
            serviceIdText.text = viewModel.getLangResources().getString(R.string.service_name_id)
            serviceDetailsText.visibility = View.VISIBLE

            var serviceDetails = ArrayList<String>()
            if (!serviceOrder.service_nb.isNullOrEmpty())
                serviceDetails.add(
                    viewModel.getLangResources()
                        .getString(R.string.service_number, serviceOrder.service_nb)
                )

            if (!serviceOrder.service.isNullOrEmpty())
                serviceDetails.add(
                    viewModel.getLangResources()
                        .getString(R.string.service_name, serviceOrder.service)
                )

            if (!serviceOrder.service_type.isNullOrEmpty())
                serviceDetails.add(
                    viewModel.getLangResources()
                        .getString(R.string.service_type, serviceOrder.service_type)
                )

            if (!serviceOrder.delivery_type.isNullOrEmpty())
                serviceDetails.add(
                    viewModel.getLangResources()
                        .getString(R.string.service_delivery_type, serviceOrder.delivery_type)
                )

            if (!serviceOrder.problem_description.isNullOrEmpty())
                serviceDetails.add(
                    viewModel.getLangResources()
                        .getString(R.string.case_problem, serviceOrder.problem_description)
                )


            /*  serviceDetails = arrayOf(
                 "Service Category",
                 "Service Type",
                 "Case Problem",
                 "Delivery Type",
                 "Solution and how much it is going to cost",
                 "Start Date : DD/MM/YYYY   End Date: DD/MM/YYYY"
             )*/

            var serviceDetailsValue = ""
            for (serviceDetail in serviceDetails) {
                serviceDetailsValue += ". ${serviceDetail}\n"
            }
            if (serviceDetailsValue.isEmpty()) {
                serviceDetailsText.visibility = View.GONE
                serviceIdText.visibility = View.GONE
            }
            else
                serviceDetailsText.text = serviceDetailsValue

            var serviceDates = ArrayList<String>()
            if (!serviceOrder.scheduled_date_start.isNullOrEmpty())
                serviceDates.add(
                    viewModel.getLangResources()
                        .getString(R.string.service_start_date, serviceOrder.scheduled_date_start)
                )

            if (!serviceOrder.scheduled_date_end.isNullOrEmpty())
                serviceDates.add(
                    viewModel.getLangResources()
                        .getString(R.string.service_end_date, serviceOrder.scheduled_date_end)
                )

            var serviceDateValue = ""
            for (serviceDate in serviceDates) {
                serviceDateValue += ". ${serviceDate}\n"
            }

            if (serviceDateValue.isEmpty())
                serviceDate.visibility = View.GONE
            else
                serviceDate.text = serviceDateValue
            // here check for array of images

            var photos = ArrayList<String>()
            photos.add("https://jakcomputer.com/wp-content/uploads/2022/09/1-26.jpg")
            photos.add("https://jakcomputer.com/wp-content/uploads/2022/09/1-26.jpg")
            photos.add("https://jakcomputer.com/wp-content/uploads/2022/09/1-26.jpg")
            photos.add("https://jakcomputer.com/wp-content/uploads/2022/09/1-26.jpg")
            photos.add("https://jakcomputer.com/wp-content/uploads/2022/09/1-26.jpg")

            if (photos.isEmpty())
                recyclerView.visibility = View.GONE
            else {
                servicePhotoRecyclerViewAdapter = ServicePhotoRecyclerViewAdapter()

                recyclerView.layoutManager =
                    LinearLayoutManager(
                        this@ServiceOrderActivity,
                        LinearLayoutManager.HORIZONTAL,
                        false
                    )
                recyclerView.setItemAnimator(DefaultItemAnimator())
                recyclerView.setAdapter(servicePhotoRecyclerViewAdapter)
                servicePhotoRecyclerViewAdapter.differ.submitList(photos)
                recyclerView.visibility = View.VISIBLE
            }
        }
    }

    private fun setLoading() {
        binding.apply {
            mainContainer.visibility = View.GONE
            orderErrorContainer.root.visibility = View.GONE
            swipeRefreshLayout.setRefreshing(true)
            //shimmer.visibility = View.VISIBLE
            // shimmer.startShimmer()
        }
    }


    fun onRefresh() {
        binding.apply {
            swipeRefreshLayout.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
                viewModel.getServiceOrderById(order_id)
            })
        }
    }

    fun setUpErrorForm(error_type: String) {
        binding.apply {
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