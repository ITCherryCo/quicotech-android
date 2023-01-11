package com.quico.tech.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.quico.tech.R
import com.quico.tech.adapter.CartRecyclerViewAdapter
import com.quico.tech.data.Constant
import com.quico.tech.data.Constant.EMPTY_CART
import com.quico.tech.databinding.ActivityCartBinding
import com.quico.tech.model.Item
import com.quico.tech.model.Product
import com.quico.tech.utils.Common
import com.quico.tech.utils.Resource
import com.quico.tech.viewmodel.SharedViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CartActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCartBinding
    private lateinit var cartRecyclerViewAdapter: CartRecyclerViewAdapter
    private val viewModel: SharedViewModel by viewModels()
    private var products = ArrayList<Product>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Constant.PRODUCT_LIST = null
        Constant.TEMPORAR_ADDRESS = null

        setUpCartAdapter()
        setUpText()
        onRefresh()
        initStatusBar()
        subscribeProducts()
        viewModel.user?.let {
            viewModel.loadCart(false)
        }
        if (viewModel.user == null)
            setUpErrorForm(Constant.EMPTY_CART)

        binding.apply {
            checkoutBtn.setOnClickListener {
                //startActivity(Intent(this@CartActivity, PaymentMethodActivity::class.java))
                Constant.PRODUCT_LIST = cartRecyclerViewAdapter.getSelectedProduct()
                startActivity(Intent(this@CartActivity, AddressListActivity::class.java))
            }

            backArrow.setOnClickListener {
                onBackPressed()
            }
        }
    }

    fun initStatusBar() {
        Common.setSystemBarColor(this, R.color.white)
        Common.setSystemBarLight(this)
    }

    private fun setUpText() {
        binding.apply {
            title.text = viewModel.getLangResources().getString(R.string.cart)
            totalText.text = viewModel.getLangResources().getString(R.string.total_tax_includes)
            checkoutBtn.text = viewModel.getLangResources().getString(R.string.checkout)
            cartErrorContainer.errorBtn.text =
                viewModel.getLangResources().getString(R.string.start_shopping)

            if (viewModel.getLanguage().equals(Constant.AR))
                backArrow.scaleX = -1f
        }
    }

    private fun subscribeProducts() {
        lifecycleScope.launch {
            viewModel.cart_items.collect { response ->
                when (response) {
                    is Resource.Success -> {
                        binding.apply {
                            stopShimmer()
                            cartErrorContainer.root.visibility = View.GONE
                            swipeRefreshLayout.setRefreshing(false)
                            checkoutBtn.visibility = View.VISIBLE
                        }

                        response.data?.let { itemsResponse ->
                            if (itemsResponse.result.isNullOrEmpty()) {
                                viewModel.user = viewModel.user?.copy(have_items_in_cart = false)
                                setUpErrorForm(Constant.EMPTY_CART)
                            } else {
                                viewModel.user = viewModel.user?.copy(have_items_in_cart = true)
                                var total_price = 0.0
                                /*  itemsResponse.result.forEach { product ->
                                      if (!product.in_stock!! || product.quantity!! > product.quantity_available!!) {
                                          binding.checkoutBtn.setEnabled(false)
                                      }
                                  }*/
                                //var total_price= itemsResponse.result.map { it.new_price }.sum()

                                viewModel.user?.let { user ->
                                    total_price = itemsResponse.result.map { product ->
                                        if (user.is_vip || viewModel.vip_subsription) {
                                            if (product.is_vip || product.is_on_sale)
                                                product.new_price * product.quantity!!
                                            else
                                                product.regular_price * product.quantity!!
                                        } else {
                                            if (product.is_on_sale)
                                                product.new_price * product.quantity!!
                                            else
                                                product.regular_price * product.quantity!!
                                        }
                                    }.sum()
                                }


                                var total_unavailable_product =
                                    itemsResponse.result.filter { product -> !product.is_vip_charge_product && (!product.in_stock!! || product.quantity!! > product.quantity_available!!) }
                                        .count()
                                var vip_charge_existence =
                                    itemsResponse.result.filter { product -> product.is_vip_charge_product }
                                        .count()

                                if (itemsResponse.result.size == 1 && vip_charge_existence == 1){
                                    binding.apply {
                                        checkoutBtn.setEnabled(false)
                                        alertMsg.visibility = View.VISIBLE
                                        alertMsg.text = viewModel.getLangResources().getString(R.string.only_vip_charge_msg)
                                    }
                                }
                                else {
                                    if (total_unavailable_product > 0)
                                        binding.checkoutBtn.setEnabled(false)
                                    else
                                        binding.checkoutBtn.setEnabled(true)
                                }
                                Log.d("TOTAL_SUM_PRICE", total_price.toString())
                                setUpCartInfo(total_price)
                                // binding.checkoutBtn.setEnabled(true)
                                binding.recyclerView.visibility = View.VISIBLE
                                cartRecyclerViewAdapter.differ.submitList(itemsResponse.result!!)
                            }
                        }
                        Log.d(Constant.CART_TAG, "SUCCESS")
                    }

                    is Resource.Error -> {
                        response.message?.let { message ->
                            Log.d(Constant.CART_TAG, "ERROR $message")
                            setUpErrorForm(Constant.ERROR)
                            if (message.equals(getString(R.string.session_expired))) {
                                viewModel.resetSession()
                                Common.setUpSessionProgressDialog(this@CartActivity)

                            }
                        }
                    }

                    is Resource.Connection -> {
                        Log.d(Constant.CART_TAG, "ERROR CONNECTION")
                        setUpErrorForm(Constant.CONNECTION)
                    }

                    is Resource.Loading -> {
                        setLoading()
                        Log.d(Constant.CART_TAG, "LOADING")
                    }
                }
            }
        }
    }


    private fun setUpCartAdapter() {
        // call the adapter for item list
        binding.apply {
            cartRecyclerViewAdapter = CartRecyclerViewAdapter(viewModel)


            var items = ArrayList<Product>()

            recyclerView.layoutManager =
                LinearLayoutManager(this@CartActivity, LinearLayoutManager.VERTICAL, false)
            recyclerView.setItemAnimator(DefaultItemAnimator())
            recyclerView.setAdapter(cartRecyclerViewAdapter)

            cartRecyclerViewAdapter.differ.submitList(items)
        }
    }

    private fun stopShimmer() {
        binding.apply {
            shimmer.visibility = View.GONE
            shimmer.stopShimmer()
        }
    }

    private fun setLoading() {
        binding.apply {
            recyclerView.visibility = View.GONE
            cartErrorContainer.root.visibility = View.GONE
            totalContainer.visibility = View.GONE
            checkoutBtn.setEnabled(false)
            shimmer.visibility = View.VISIBLE
            shimmer.startShimmer()
            // swipeRefreshLayout.setRefreshing(true)
        }
    }

    private fun onRefresh() {
        binding.apply {
            swipeRefreshLayout.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
                if (viewModel.user == null)
                    setUpErrorForm(EMPTY_CART)
                else {
                    viewModel.user?.let {
                        viewModel.loadCart(false)
                    }
                }
            })
        }
    }

    private fun setUpCartInfo(total_price: Double) {
        binding.apply {
            totalContainer.visibility = View.VISIBLE
            total.text = "$ ${total_price}"
            // set total
        }
    }

    private fun setUpErrorForm(error_type: String) {

        binding.apply {
            swipeRefreshLayout.setRefreshing(false)
            stopShimmer()
            recyclerView.visibility = View.GONE
            totalContainer.visibility = View.GONE
            checkoutBtn.visibility = View.GONE

            checkoutBtn.setEnabled(false)

            cartErrorContainer.apply {
                root.visibility = View.VISIBLE
                errorMsg1.visibility = View.GONE
                tryAgain.visibility = View.GONE
                errorMsg2.text = viewModel.getLangResources().getString(R.string.try_again)
                errorImage.setImageResource(android.R.color.transparent)
                errorImage.setImageResource(R.drawable.empty_item)

                when (error_type) {
                    Constant.CONNECTION -> {
                        errorMsg2.text =
                            viewModel.getLangResources().getString(R.string.check_connection)
                    }

                    Constant.EMPTY_CART -> {
                        errorMsg1.visibility = View.VISIBLE
                        errorMsg1.text =
                            viewModel.getLangResources().getString(R.string.no_items_in_list)
                        errorMsg2.text =
                            viewModel.getLangResources().getString(R.string.dont_have_shop_item)
                        errorImage.setImageResource(R.drawable.empty_item)
                        errorBtn.visibility = View.VISIBLE

                        errorBtn.setOnClickListener {
                            onBackPressed()
                        }
                    }

                    Constant.ERROR -> {
                        errorBtn.visibility = View.GONE
                        errorMsg2.text = viewModel.getLangResources().getString(R.string.error_msg)
                    }
                }
            }
        }
    }
}