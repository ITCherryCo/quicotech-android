package com.quico.tech.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.quico.tech.R
import com.quico.tech.adapter.CategoryAllRecyclerViewAdapter
import com.quico.tech.adapter.ProductRecyclerViewAdapter
import com.quico.tech.data.Constant
import com.quico.tech.databinding.ActivityBestSellingAllBinding
import com.quico.tech.databinding.ActivityCategoryAllBinding
import com.quico.tech.model.Category
import com.quico.tech.model.Product
import com.quico.tech.utils.Common
import com.quico.tech.utils.Resource
import com.quico.tech.viewmodel.SharedViewModel
import kotlinx.coroutines.launch

class BestSellingAllActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBestSellingAllBinding
    private lateinit var bestSellingAllRecyclerViewAdapter: ProductRecyclerViewAdapter
    private val viewModel: SharedViewModel by viewModels()
    var TAG = "BEST_SELLING_RESPONSE"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBestSellingAllBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initToolbar()

        binding.apply {
            BestSellingtitle.text = viewModel.getLangResources().getString(R.string.best_selling)
            if (viewModel.getLanguage().equals(Constant.AR))
                backArrow.scaleX = -1f

            backArrow.setOnClickListener {
                onBackPressed()
            }
        }

        setUpBestSellingAllProductsAdapter()
    }

    fun initToolbar(){
        binding.apply {
            Common.setSystemBarColor(this@BestSellingAllActivity, R.color.home_background_grey)
            Common.setSystemBarLight(this@BestSellingAllActivity)
            bagIcon.setOnClickListener {
                startActivity(Intent(this@BestSellingAllActivity, CartActivity::class.java))
            }
        }
    }

    override fun onResume() {
        super.onResume()
        onRefresh()
        subscribeBestSellingAllProductsList()
        viewModel.getAllBestSellingProducts()
    }


    fun subscribeBestSellingAllProductsList(){
        lifecycleScope.launch {
            viewModel.allBestSellingProducts.collect { response ->
                when (response) {
                    is Resource.Success -> {
                        binding.apply {
                            stopShimmer()
                            binding.swipeRefreshLayout.setRefreshing(false)
                        }

                        response.data?.let { bestSellingResponse ->
                            if (bestSellingResponse.result.isNullOrEmpty())
                                setUpErrorForm(Constant.NO_BEST_SELLING)
                            else {
                                bestSellingAllRecyclerViewAdapter.differ.submitList(bestSellingResponse.result!!)
                                binding.recyclerView.setVisibility(View.VISIBLE)
                            }
                        }
                        Log.d(TAG, "SUCCESS")
                    }

                    is Resource.Error -> {
                        response.message?.let { message ->
                            Log.d(TAG, "ERROR $message")
                            setUpErrorForm(Constant.ERROR)
                            if (message.equals(getString(R.string.session_expired))) {
                                viewModel.resetSession()
                                Common.setUpSessionProgressDialog(this@BestSellingAllActivity)
                            }
                        }
                    }

                    is Resource.Connection -> {
                        Log.d(TAG, "ERROR CONNECTION")
                        setUpErrorForm(Constant.CONNECTION)
                    }

                    is Resource.Loading -> {
                        setLoading()
                        Log.d(TAG, "LOADING")
                    }
                }
            }
        }
    }


    private fun setUpBestSellingAllProductsAdapter() {
        binding.apply {
            bestSellingAllRecyclerViewAdapter = ProductRecyclerViewAdapter(false, false, false, viewModel, null)
            var bestSellingAllProductsList = ArrayList<Product>()

            recyclerView.layoutManager = GridLayoutManager(this@BestSellingAllActivity, 2)
            recyclerView.setItemAnimator(DefaultItemAnimator())
            recyclerView.setAdapter(bestSellingAllRecyclerViewAdapter)
            bestSellingAllRecyclerViewAdapter.differ.submitList(bestSellingAllProductsList)
        }
    }


    private fun stopShimmer() {
        binding.apply {
            shimmer.visibility = View.GONE
            shimmer.stopShimmer()
        }
    }

    fun setLoading() {
        binding.apply {
            errorContainerBestSellingAll.root.visibility = View.GONE
            shimmer.visibility = View.VISIBLE
            shimmer.startShimmer()
            recyclerView.visibility = View.GONE
        }
    }

    fun onRefresh() {
        binding.apply {
            swipeRefreshLayout.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
                setLoading() // later we will remove it because the observable will call it
                viewModel.getAllBestSellingProducts() // get User id
            })
        }
    }

    fun setUpErrorForm(error_type: String) {
        binding.apply {
            swipeRefreshLayout.setRefreshing(false)
            recyclerView.visibility = View.GONE
            stopShimmer()

            errorContainerBestSellingAll.apply {
                errorMsg1.visibility = View.GONE
                tryAgain.visibility = View.VISIBLE
                errorBtn.visibility = View.GONE
                root.visibility = View.VISIBLE
                errorImage.setImageResource(android.R.color.transparent)
                errorImage.setImageResource(R.drawable.empty_item)

                tryAgain.setText(
                    viewModel.getLangResources().getString(R.string.try_again)
                )
                tryAgain.setOnClickListener {
                    onRefresh()
                }

                when (error_type) {
                    Constant.CONNECTION -> {
                        errorMsg1.text =
                            viewModel.getLangResources().getString(R.string.connection)

                        errorMsg2.text =
                            viewModel.getLangResources().getString(R.string.check_connection)

                    }
                    Constant.NO_BEST_SELLING -> {
                        errorMsg1.text =
                            viewModel.getLangResources().getString(R.string.best_selling)

                        errorMsg2.text =
                            viewModel.getLangResources().getString(R.string.no_best_selling_products)
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