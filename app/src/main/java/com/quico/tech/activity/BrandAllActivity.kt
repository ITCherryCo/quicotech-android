package com.quico.tech.activity

import android.app.Activity
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
import com.quico.tech.adapter.BrandAllRecyclerViewAdapter
import com.quico.tech.adapter.ServiceRecyclerViewAdapter
import com.quico.tech.data.Constant
import com.quico.tech.databinding.ActivityBrandAllBinding
import com.quico.tech.model.Brand
import com.quico.tech.model.Product
import com.quico.tech.model.Service
import com.quico.tech.utils.Common
import com.quico.tech.utils.Resource
import com.quico.tech.viewmodel.SharedViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class BrandAllActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBrandAllBinding
    private lateinit var brandAllRecyclerViewAdapter: BrandAllRecyclerViewAdapter
    private val viewModel: SharedViewModel by viewModels()
    var TAG = "BRANDS_RESPONSE"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBrandAllBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initToolbar()

        binding.apply {
            Brandtitle.text = viewModel.getLangResources().getString(R.string.brands)
            if (viewModel.getLanguage().equals(Constant.AR))
                backArrow.scaleX = -1f

            backArrow.setOnClickListener {
                onBackPressed()
            }
        }

        onRefresh()
        setUpBrandsAdapter()
        subscribeBrandsList()
        viewModel.getAllBrands()
    }

    fun initToolbar(){
        binding.apply {
            Common.setSystemBarColor(this@BrandAllActivity, R.color.home_background_grey)
            Common.setSystemBarLight(this@BrandAllActivity)
            bagIcon.setOnClickListener {
                startActivity(Intent(this@BrandAllActivity, CartActivity::class.java))
            }
        }
    }

    fun subscribeBrandsList(){
        lifecycleScope.launch {
            viewModel.brands.collect { response ->
                when (response) {
                    is Resource.Success -> {
                        binding.apply {
                            swipeRefreshLayout.setRefreshing(false)
                            swipeRefreshLayout.setEnabled(false)
                            errorContainerBrandAll.root.visibility=View.GONE
                            recyclerView.visibility = View.VISIBLE
                        }
                        stopShimmer()
                        response.data?.let { brandsResponse ->
                            if (brandsResponse.result!!.isNullOrEmpty())
                                setUpErrorForm(Constant.NO_BRANDS)
                            else {
                                brandAllRecyclerViewAdapter.differ.submitList(brandsResponse.result!!)
                                binding.recyclerView.setVisibility(View.VISIBLE)
                            }
                        }
                        Log.d(TAG, "SUCCESS")
                    }

                    is Resource.Error -> {
                        response.message?.let { message ->
                            Log.d(TAG, "ERROR $message")
                            setUpErrorForm(Constant.ERROR)
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


    private fun setUpBrandsAdapter() {
        binding.apply {
            brandAllRecyclerViewAdapter = BrandAllRecyclerViewAdapter()
            var brands = ArrayList<Brand>()


            recyclerView.layoutManager = GridLayoutManager(this@BrandAllActivity, 2)
            recyclerView.setItemAnimator(DefaultItemAnimator())
            recyclerView.setAdapter(brandAllRecyclerViewAdapter)
            brandAllRecyclerViewAdapter.differ.submitList(brands)
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
            recyclerView.visibility = View.GONE
            errorContainerBrandAll.errorContainer.visibility = View.GONE
            shimmer.visibility = View.VISIBLE
            shimmer.startShimmer()
        }
    }

    fun onRefresh() {
        binding.apply {
            swipeRefreshLayout.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
                setLoading() // later we will remove it because the observable will call it
                viewModel.getAllBrands() // get User id
            })
        }
    }

    fun setUpErrorForm(error_type: String) {
        binding.apply {
            swipeRefreshLayout.setRefreshing(false)
            recyclerView.visibility = View.GONE
            errorContainerBrandAll.errorContainer.visibility = View.VISIBLE
            errorContainerBrandAll.errorImage.setImageResource(android.R.color.transparent)
            stopShimmer()
            errorContainerBrandAll.errorMsg1.visibility = View.GONE
            swipeRefreshLayout.setEnabled(true)

            when (error_type) {
                Constant.CONNECTION -> {
                    errorContainerBrandAll.errorMsg2.setText(
                        viewModel.getLangResources().getString(R.string.check_connection)
                    )
                }
                Constant.NO_BRANDS -> {
                    errorContainerBrandAll.errorMsg2.text = viewModel.getLangResources().getString(R.string.no_brands)
                    errorContainerBrandAll.errorImage.setImageResource(R.drawable.empty_item)
                }

                Constant.ERROR -> {
                    errorContainerBrandAll.errorMsg2.setText(viewModel.getLangResources().getString(R.string.error_msg))
                }
            }
        }
    }
}