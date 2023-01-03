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
import com.quico.tech.adapter.CategoryAllRecyclerViewAdapter
import com.quico.tech.adapter.ServiceRecyclerViewAdapter
import com.quico.tech.data.Constant
import com.quico.tech.databinding.ActivityCategoryAllBinding
import com.quico.tech.model.Category
import com.quico.tech.model.Product
import com.quico.tech.model.Service
import com.quico.tech.utils.Common
import com.quico.tech.utils.Resource
import com.quico.tech.viewmodel.SharedViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CategoryAllActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCategoryAllBinding
    private lateinit var categoryAllRecyclerViewAdapter: CategoryAllRecyclerViewAdapter
    private val viewModel: SharedViewModel by viewModels()
    var TAG = "CATEGORIES_RESPONSE"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCategoryAllBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initToolbar()

        binding.apply {
            Categorytitle.text = viewModel.getLangResources().getString(R.string.categories)
            if (viewModel.getLanguage().equals(Constant.AR))
                backArrow.scaleX = -1f

            backArrow.setOnClickListener {
                onBackPressed()
            }
        }

        onRefresh()
        setUpCategoriesAdapter()
        subscribeCategoriesList()
        viewModel.getAllCategories()
    }

    fun initToolbar(){
        binding.apply {
            Common.setSystemBarColor(this@CategoryAllActivity, R.color.home_background_grey)
            Common.setSystemBarLight(this@CategoryAllActivity)
            bagIcon.setOnClickListener {
                startActivity(Intent(this@CategoryAllActivity, CartActivity::class.java))
            }
        }
    }

    fun subscribeCategoriesList(){
        lifecycleScope.launch {
            viewModel.categories.collect { response ->
                when (response) {
                    is Resource.Success -> {
                        binding.apply {
                            swipeRefreshLayout.setRefreshing(false)
                            swipeRefreshLayout.setEnabled(false)
                            errorContainerCategoryAll.root.visibility=View.GONE
                            recyclerView.visibility = View.VISIBLE
                        }
                        stopShimmer()
                        response.data?.let { categoriesResponse ->
                            if (categoriesResponse.result!!.isNullOrEmpty())
                                setUpErrorForm(Constant.NO_SERVICES)
                            else {
                                categoryAllRecyclerViewAdapter.differ.submitList(categoriesResponse.result!!)
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


    private fun setUpCategoriesAdapter() {
        binding.apply {
            categoryAllRecyclerViewAdapter = CategoryAllRecyclerViewAdapter()
            var categories = ArrayList<Category>()


            recyclerView.layoutManager = GridLayoutManager(this@CategoryAllActivity, 2)
            recyclerView.setItemAnimator(DefaultItemAnimator())
            recyclerView.setAdapter(categoryAllRecyclerViewAdapter)
            categoryAllRecyclerViewAdapter.differ.submitList(categories)
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
            errorContainerCategoryAll.errorContainer.visibility = View.GONE
            shimmer.visibility = View.VISIBLE
            shimmer.startShimmer()
        }
    }

    fun onRefresh() {
        binding.apply {
            swipeRefreshLayout.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
                setLoading() // later we will remove it because the observable will call it
                viewModel.getAllCategories() // get User id
            })
        }
    }

    fun setUpErrorForm(error_type: String) {
        binding.apply {
            swipeRefreshLayout.setRefreshing(false)
            recyclerView.visibility = View.GONE
            swipeRefreshLayout.setEnabled(true)
            stopShimmer()
            errorContainerCategoryAll.apply {
                errorMsg1.visibility = View.GONE
                root.visibility = View.VISIBLE
                errorBtn.visibility = View.GONE
                tryAgain.visibility = View.GONE
                errorImage.setImageResource(android.R.color.transparent)
                errorImage.setImageResource(R.drawable.empty_item)

                when (error_type) {
                    Constant.CONNECTION -> {
                        errorMsg2.setText(
                            viewModel.getLangResources().getString(R.string.check_connection)
                        )
                    }
                    Constant.NO_SERVICES -> {
                        errorMsg2.text =
                            viewModel.getLangResources().getString(R.string.no_services)
                        errorImage.setImageResource(R.drawable.empty_item)
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