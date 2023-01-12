package com.quico.tech.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.quico.tech.R
import com.quico.tech.adapter.OrderRecyclerViewAdapter
import com.quico.tech.adapter.OrderStatusRecyclerViewAdapter
import com.quico.tech.data.Constant
import com.quico.tech.data.Constant.ALL
import com.quico.tech.data.Constant.CANCELED
import com.quico.tech.data.Constant.CONNECTION
import com.quico.tech.data.Constant.DELIVERED
import com.quico.tech.data.Constant.DELIVERY_ORDERS
import com.quico.tech.data.Constant.ERROR
import com.quico.tech.data.Constant.ON_THE_WAY
import com.quico.tech.data.Constant.ORDER_RECEIVED
import com.quico.tech.data.Constant.ORDER_TYPE
import com.quico.tech.data.Constant.PACKAGING
import com.quico.tech.data.Constant.SERVICE_ORDERS
import com.quico.tech.data.Constant.TRACK_ORDER
import com.quico.tech.databinding.ActivityOrderListBinding
import com.quico.tech.model.Order
import com.quico.tech.utils.Common
import com.quico.tech.utils.Resource
import com.quico.tech.viewmodel.SharedViewModel
import kotlinx.coroutines.launch

class OrderListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOrderListBinding
    private lateinit var orderRecyclerViewAdapter: OrderRecyclerViewAdapter
    private lateinit var orderStatusRecyclerViewAdapter: OrderStatusRecyclerViewAdapter
    private lateinit var orders_type: String
    private val viewModel: SharedViewModel by viewModels()
    private var original_orders = ArrayList<Order>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            backArrow.setOnClickListener {
                onBackPressed()
            }
        }

        orders_type = intent.extras?.getString(ORDER_TYPE)!!

        initStatusBar()
        setUpText()
        setUpOrdersAdapter()
        setUpStatusOrdersAdapter()
        //   viewModel.updateOrderFilterType(ALL)
        //   setLoading()

        subscribeOrders()
        checkOrdersType()
        //controlBtnsClicks()
        // subscribeOrderFilterType()
    }

    fun initStatusBar() {
        Common.setSystemBarColor(this, R.color.white)
        Common.setSystemBarLight(this)
    }

    private fun checkOrdersType(){
        when (orders_type){
            DELIVERY_ORDERS->viewModel.getOrders(false)
            SERVICE_ORDERS->viewModel.getOrders(true)
        }
    }

    /**  private fun subscribeOrderFilterType() {
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
    }*/


    private fun setUpText() {
        binding.apply {
            if (orders_type.equals(DELIVERY_ORDERS))
                title.text = viewModel.getLangResources().getString(R.string.delivery_orders)
            else if (orders_type.equals(SERVICE_ORDERS))
                title.text = viewModel.getLangResources().getString(R.string.service_orders)

            /*  allBtn.text = viewModel.getLangResources().getString(R.string.all)
              ordersBtn.text = viewModel.getLangResources().getString(R.string.orders)
              servicesBtn.text = viewModel.getLangResources().getString(R.string.services)
  */

            if (viewModel.getLanguage().equals(Constant.AR))
                backArrow.scaleX = -1f
        }
    }

    fun subscribeOrders() {
        lifecycleScope.launch {
            viewModel.delivery_orders.collect { response ->
                when (response) {

                    is Resource.Success -> {
                        stopShimmer()
                        binding.ordersErrorContainer.root.visibility = View.GONE

                        response.data?.let { ordersResponse ->

                            if (ordersResponse.result.isNullOrEmpty()) {
                                setUpErrorForm(Constant.NO_ORDERS)
                            } else {
                                 original_orders = ordersResponse.result
                                 orderRecyclerViewAdapter.differ.submitList(ordersResponse.result)
                                 binding.recyclerView.visibility = View.VISIBLE
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


    /*  private fun controlBtnsClicks() {
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
      }*/

    private fun stopShimmer() {
        binding.apply {
            shimmer.visibility = View.GONE
            shimmer.stopShimmer()
        }
    }

    fun setUpOrdersAdapter() {
        orderRecyclerViewAdapter = OrderRecyclerViewAdapter(if (orders_type.equals(SERVICE_ORDERS))true else false,viewModel)
        var orders = ArrayList<Order>()
        binding.apply {

            /*   orders.add(Order(1))
               orders.add(Order(2))
               orders.add(Order(3))
               orders.add(Order(4))*/

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

    fun setUpStatusOrdersAdapter() {
        orderStatusRecyclerViewAdapter = OrderStatusRecyclerViewAdapter(object :
            OrderStatusRecyclerViewAdapter.OnOrderStatusClick {
            override fun onOrderStatusClick(status: String) {
                // filter
                filterOrders(status)
            }
        })

        var status = ArrayList<Array<String>>()
        status.addAll(
            listOf(
                arrayOf(ALL, viewModel.getLangResources().getString(R.string.all)),
                arrayOf(ORDER_RECEIVED, viewModel.getLangResources().getString(R.string.order_received)),
                arrayOf(PACKAGING, viewModel.getLangResources().getString(R.string.packaging)),
                arrayOf(ON_THE_WAY, viewModel.getLangResources().getString(R.string.on_the_way)),
                arrayOf(DELIVERED, viewModel.getLangResources().getString(R.string.delivered)),
                arrayOf(CANCELED, viewModel.getLangResources().getString(R.string.canceled))
                // ORDER_RECEIVED, PACKAGING, ON_THE_WAY, DELIVERED, CANCELED
            )
        )

        binding.apply {

            statusRecyclerView.setLayoutManager(
                LinearLayoutManager(
                    this@OrderListActivity,
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
            )
            statusRecyclerView.setItemAnimator(DefaultItemAnimator())
            statusRecyclerView.setAdapter(orderStatusRecyclerViewAdapter)
        }
        orderStatusRecyclerViewAdapter.differ.submitList(status)

    }

    private fun filterOrders(order_status: String) {
        when (order_status) {
            ALL -> {
                orderRecyclerViewAdapter.differ.submitList(original_orders)
            }
            else -> {

                var filteredOrders = original_orders.filter {
                    it.status.lowercase() == order_status.lowercase()
                }

                orderRecyclerViewAdapter.differ.submitList(filteredOrders)
            }

        }
       // orderRecyclerViewAdapter.notifyDataSetChanged()

    }

    fun setLoading() {
        binding.apply {
            recyclerView.visibility = View.GONE
            ordersErrorContainer.root.visibility = View.GONE
            shimmer.visibility = View.VISIBLE
            shimmer.startShimmer()
        }
    }

    fun onRefresh() {
        checkOrdersType()
    }

    fun setUpErrorForm(error_type: String) {
        binding.apply {
            recyclerView.visibility = View.GONE
            stopShimmer()
            ordersErrorContainer.apply {
                root.visibility = View.VISIBLE
                tryAgain.visibility = View.VISIBLE
                errorImage.visibility = View.VISIBLE
                errorBtn.visibility = View.GONE
                tryAgain.setText(
                    viewModel.getLangResources().getString(R.string.try_again)
                )
                tryAgain.setOnClickListener {
                    onRefresh()
                }
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