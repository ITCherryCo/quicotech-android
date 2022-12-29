package com.quico.tech.activity

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.quico.tech.R
import com.quico.tech.adapter.OrderRecyclerViewAdapter
import com.quico.tech.data.Constant
import com.quico.tech.data.Constant.ALL
import com.quico.tech.data.Constant.CONNECTION
import com.quico.tech.data.Constant.ERROR
import com.quico.tech.data.Constant.NO_ORDERS
import com.quico.tech.data.Constant.ONGOING_ORDERS
import com.quico.tech.data.Constant.ORDERS
import com.quico.tech.data.Constant.ORDERS_TYPE
import com.quico.tech.data.Constant.SERVICES
import com.quico.tech.data.Constant.TRACK_ORDER
import com.quico.tech.databinding.ActivityOrderListBinding
import com.quico.tech.model.Order
import com.quico.tech.utils.Resource
import com.quico.tech.viewmodel.SharedViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class OrderListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOrderListBinding
    private lateinit var orderRecyclerViewAdapter: OrderRecyclerViewAdapter
    private lateinit var orders_type: String
    private val viewModel: SharedViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            backArrow.setOnClickListener {
                onBackPressed()
            }
        }

        orders_type = intent.extras?.getString(ORDERS_TYPE)!!
        controlBtnsClicks()
        subscribeOrderFilterType()
        setUpText()
        viewModel.updateOrderFilterType(ALL)
        setLoading()
//        viewModel.getOrders(1,orders_type)
//        subscribeOrders()
    }


    private fun subscribeOrderFilterType() {
        lifecycleScope.launch {
            viewModel.orders_filter_type.collect { order_filter_type ->
                when (order_filter_type) {
                    ALL -> {
                        controlAllBtn(true)
                        controlOrdersBtn(false)
                        controlServicesBtn(false)
                        //viewModel.getOrders(1, ALL)
                    }
                    ORDERS -> {
                        controlAllBtn(false)
                        controlOrdersBtn(true)
                        controlServicesBtn(false)
                        //viewModel.getOrders(1, ORDERS)
                    }
                    SERVICES -> {
                        controlAllBtn(false)
                        controlOrdersBtn(false)
                        controlServicesBtn(true)
                        //viewModel.getOrders(1, SERVICES)
                    }
                }
            }
        }
    }

    fun subscribeOrders() {
        lifecycleScope.launch {
            viewModel.orders.collect { response ->
                when (response) {

                    is Resource.Success -> {
                        stopShimmer()
                        binding.ordersErrorContainer.errorContainer.visibility=View.GONE

                        response.data?.let { ordersResponse ->

                            if (ordersResponse.result.equals(ERROR) || ordersResponse.orders.isEmpty()) {
                                setUpErrorForm(NO_ORDERS)
                            } else {
                                orderRecyclerViewAdapter.differ.submitList(ordersResponse.orders)
                                binding.recyclerView.visibility=View.VISIBLE
                            }
                        }
                    }

                    is Resource.Error -> {
                        response.message?.let { message ->
                            setUpErrorForm(ERROR)
                        }
                    }

                    is Resource.Connection -> {
                        setUpErrorForm(CONNECTION)
                    }

                    is Resource.Loading -> {
                        setLoading()
                    }
                }
            }
        }
    }

    private fun setUpText() {
        binding.apply {
            if (orders_type.equals(ONGOING_ORDERS))
                title.text = viewModel.getLangResources().getString(R.string.ongoing_orders)
            else
                title.text = viewModel.getLangResources().getString(R.string.order_history)

            allBtn.text = viewModel.getLangResources().getString(R.string.all)
            ordersBtn.text = viewModel.getLangResources().getString(R.string.orders)
            servicesBtn.text = viewModel.getLangResources().getString(R.string.services)

            if (viewModel.getLanguage().equals(Constant.AR))
                backArrow.scaleX = -1f
        }
    }

    private fun controlBtnsClicks() {
        binding.apply {
            allBtn.setOnClickListener {
                viewModel.updateOrderFilterType(ALL)
            }

            ordersBtn.setOnClickListener {
                viewModel.updateOrderFilterType(ORDERS)

            }
            servicesBtn.setOnClickListener {
                viewModel.updateOrderFilterType(SERVICES)
            }
        }
    }

    private fun controlAllBtn(active: Boolean) {
        binding.apply {
            if (active) {
                allBtn.background =
                    this@OrderListActivity.resources.getDrawable(R.drawable.container_purple_corner_12)
                allBtn.setTextColor(this@OrderListActivity.resources.getColor(R.color.white))
            } else {
                allBtn.background = resources.getDrawable(R.drawable.container_white_gray_stroke_12)
                allBtn.setTextColor(this@OrderListActivity.resources.getColor(R.color.gray_dark))
            }
        }
    }

    private fun controlOrdersBtn(active: Boolean) {
        binding.apply {

            if (active) {
                ordersBtn.background =
                    this@OrderListActivity.resources.getDrawable(R.drawable.container_purple_corner_12)
                ordersBtn.setTextColor(this@OrderListActivity.resources.getColor(R.color.white))
            } else {
                ordersBtn.background =
                    this@OrderListActivity.resources.getDrawable(R.drawable.container_white_gray_stroke_12)
                ordersBtn.setTextColor(this@OrderListActivity.resources.getColor(R.color.gray_dark))
            }
        }
    }

    private fun controlServicesBtn(active: Boolean) {
        binding.apply {
            if (active) {
                servicesBtn.background =
                    this@OrderListActivity.resources.getDrawable(R.drawable.container_purple_corner_12)
                servicesBtn.setTextColor(this@OrderListActivity.resources.getColor(R.color.white))
            } else {
                servicesBtn.background =
                    this@OrderListActivity.resources.getDrawable(R.drawable.container_white_gray_stroke_12)
                servicesBtn.setTextColor(this@OrderListActivity.resources.getColor(R.color.gray_dark))
            }
        }
    }

    private fun stopShimmer() {
        binding.apply {
            shimmer.visibility = View.GONE
            shimmer.stopShimmer()
        }
    }

    fun setUpOrdersAdapter() {
        orderRecyclerViewAdapter = OrderRecyclerViewAdapter()
        var orders = ArrayList<Order>()
        binding.apply {

            stopShimmer()
            recyclerView.visibility = View.VISIBLE

            orders.add(Order(1))
            orders.add(Order(2))
            orders.add(Order(3))
            orders.add(Order(4))

            recyclerView.setLayoutManager(
                LinearLayoutManager(
                    this@OrderListActivity,
                    LinearLayoutManager.VERTICAL,
                    false
                )
            )
            recyclerView.setItemAnimator(DefaultItemAnimator())
            recyclerView.setAdapter(orderRecyclerViewAdapter)
        }
        orderRecyclerViewAdapter.differ.submitList(orders)

        orderRecyclerViewAdapter.setOnItemClickListener {
            startActivity(
                Intent(this, CheckoutActivity::class.java)
                    .putExtra(TRACK_ORDER, true)
            )
        }
    }

    fun setLoading() {
        binding.apply {
            recyclerView.visibility = View.GONE
            ordersErrorContainer.errorContainer.visibility = View.GONE
            shimmer.visibility = View.VISIBLE
            shimmer.startShimmer()

            lifecycleScope.launch {
                delay(3000)
                setUpOrdersAdapter()
            }
        }
    }

    fun onRefresh() {
        setLoading() // later we will remove it because the observable will call it
        // viewModel.getOrders(1, orders_type) // get User id


    }

    fun setUpErrorForm(error_type: String) {
        binding.apply {
            recyclerView.visibility = View.GONE
            stopShimmer()
            ordersErrorContainer.apply {
                errorContainer.visibility = View.VISIBLE
                tryAgain.visibility = View.VISIBLE
                errorImage.visibility = View.GONE
                errorBtn.visibility = View.GONE
                tryAgain.setText(
                    viewModel.getLangResources().getString(R.string.try_again)
                )
                tryAgain.setOnClickListener {  }

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