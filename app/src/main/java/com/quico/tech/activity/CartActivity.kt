package com.quico.tech.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.quico.tech.R
import com.quico.tech.adapter.CartRecyclerViewAdapter
import com.quico.tech.data.Constant
import com.quico.tech.databinding.ActivityCartBinding
import com.quico.tech.model.Item
import com.quico.tech.utils.Common
import com.quico.tech.viewmodel.SharedViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CartActivity : AppCompatActivity() {
    private lateinit var binding : ActivityCartBinding
    private lateinit var cartRecyclerViewAdapter : CartRecyclerViewAdapter
    private val viewModel: SharedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

         binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setLoading()
       // setUpCartAdapter()
        setUpText()
        initStatusBar()
        binding.apply {
            checkoutBtn.setOnClickListener {
                startActivity(Intent(this@CartActivity, PaymentMethodActivity::class.java))
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
    private fun setUpText(){
        binding.apply {
            title.text = viewModel.getLangResources().getString(R.string.cart)
            totalText.text = viewModel.getLangResources().getString(R.string.total_tax_includes)
            checkoutBtn.text = viewModel.getLangResources().getString(R.string.checkout)
            cartErrorContainer.errorBtn.text = viewModel.getLangResources().getString(R.string.start_shopping)

            if (viewModel.getLanguage().equals(Constant.AR))
                backArrow.scaleX = -1f
        }
    }

   private fun setUpCartAdapter() {
        // call the adapter for item list
        binding.apply {
            cartRecyclerViewAdapter = CartRecyclerViewAdapter(viewModel)
            stopShimmer()
            recyclerView.visibility=View.VISIBLE
            checkoutBtn.setEnabled(true)
            swipeRefreshLayout.setRefreshing(false)

            var items = ArrayList<Item>()
            items.add(Item(1, ""))
            items.add(Item(1, ""))
            items.add(Item(1, ""))
            items.add(Item(1, ""))
            items.add(Item(1, ""))

                recyclerView.layoutManager =
                    LinearLayoutManager(this@CartActivity, LinearLayoutManager.VERTICAL, false)
                recyclerView.setItemAnimator(DefaultItemAnimator())
                recyclerView.setAdapter(cartRecyclerViewAdapter)

            cartRecyclerViewAdapter.differ.submitList(items)
        }
    }

  private fun stopShimmer(){
        binding.apply {
            shimmer.visibility = View.GONE
            shimmer.stopShimmer()
        }
    }

    private fun setLoading() {
        binding.apply {
            recyclerView.visibility=View.GONE
            cartErrorContainer.root.visibility=View.GONE
            totalContainer.visibility = View.GONE
            checkoutBtn.setEnabled(false)
            shimmer.visibility = View.VISIBLE
            shimmer.startShimmer()
           // swipeRefreshLayout.setRefreshing(true)

            lifecycleScope.launch {
                delay(3000)
                setUpCartAdapter()
            }
        }
    }

   private fun onRefresh() {
        binding.apply {
            swipeRefreshLayout.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
                setLoading() // later we will remove it because the observable will call it
               // viewModel.carts()
            })
        }
    }

   private fun setUpCartInfo(){
        binding.apply {
            totalContainer.visibility = View.VISIBLE
            // set total
        }
    }

   private fun setUpErrorForm(error_type: String) {

        binding.apply {
            swipeRefreshLayout.setRefreshing(false)
            stopShimmer()
            recyclerView.visibility = View.GONE
            totalContainer.visibility = View.GONE
            checkoutBtn.setEnabled(false)
            swipeRefreshLayout.setEnabled(true)
            cartErrorContainer.apply {
                root.visibility = View.VISIBLE
                errorMsg1.visibility = View.GONE
                errorImage.setImageResource(android.R.color.transparent)

                when (error_type) {
                    Constant.CONNECTION -> {
                        errorMsg2.setText(
                            viewModel.getLangResources().getString(R.string.check_connection)
                        )
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
                        errorMsg2.setText(
                            viewModel.getLangResources().getString(R.string.error_msg)
                        )
                    }
                }
            }
        }
    }
}