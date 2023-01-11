package com.quico.tech.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.quico.tech.R
import com.quico.tech.adapter.BrandSearchRecyclerViewAdapter
import com.quico.tech.adapter.ProductRecyclerViewAdapter
import com.quico.tech.adapter.SubCategoryRecyclerViewAdapter
import com.quico.tech.data.Constant
import com.quico.tech.databinding.ActivityCategoryDetailBinding
import com.quico.tech.model.*
import com.quico.tech.utils.Common
import com.quico.tech.utils.Resource
import com.quico.tech.viewmodel.SharedViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CategoryDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCategoryDetailBinding
    private val viewModel: SharedViewModel by viewModels()
    private lateinit var productRecyclerViewAdapter: ProductRecyclerViewAdapter
    private lateinit var subCategoryRecyclerViewAdapter: SubCategoryRecyclerViewAdapter
    private var category_id: Int = 0
    private var category_name: String = ""
    private var current_page = 1
    private var total_pages = 1
    private var search_text = ""
    private var can_scroll = false
    val products = ArrayList<Product>()
    var TAG = "CATEGORY_DETAILS_RESPONSE"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initToolbar()

        binding.apply {
            title.text = viewModel.getLangResources().getString(R.string.single_category)

            if (viewModel.getLanguage().equals(Constant.AR))
                backArrow.scaleX = -1f

            backArrow.setOnClickListener {
                onBackPressed()
            }

            searchView.setQueryHint(
                Html.fromHtml(
                    "<font color = #5F5F5F>" + viewModel.getLangResources()
                        .getString(R.string.search_hint) + "</font>"
                )
            );
            searchView.requestFocus()

            category_id = intent?.extras?.getInt(Constant.CATEGORY_ID)!!
            category_name = intent?.extras?.getString(Constant.CATEGORY_NAME)!!

            category_id.let { category_id ->
                if (category_id != 0) {
                    setUpProductsByCategoryAdapter()
                    setUpSubCategoriesAdapter()
                    subscribeGetProductsByCategoryList()
                    products.clear()
                    addScrollListener()
                    viewModel.getProductsByCategory(category_id,PaginationBodyParameters(PaginationParams(current_page)))
                }
            }

        }
    }

    fun initToolbar(){
        binding.apply {
            Common.setSystemBarColor(this@CategoryDetailActivity, R.color.home_background_grey)
            Common.setSystemBarLight(this@CategoryDetailActivity)
            cartImage.setOnClickListener {
                startActivity(Intent(this@CategoryDetailActivity, CartActivity::class.java))
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.apply {
            viewModel.user?.let { user ->
                if (user.have_items_in_cart)
                    cartImage.setImageResource(R.drawable.cart_full)
                else
                    cartImage.setImageResource(R.drawable.bag)
            }
        }
    }

    fun subscribeGetProductsByCategoryList(){
        lifecycleScope.launch {
            viewModel.productsByCategory.collect { response ->
                when (response) {
                    is Resource.Success -> {
                        binding.apply {
                            stopShimmer()
                            progressBar.visibility = View.GONE
                            categoryDetailsErrorContainer.root.visibility = View.GONE
                        }

                        response.data?.let { productsByCategoryResponse ->
                            if (productsByCategoryResponse.result?.products.isNullOrEmpty() || productsByCategoryResponse.result?.subcategories.isNullOrEmpty())
                                setUpErrorForm(Constant.NO_PRODUCTS_BY_CATEGORY)
                            else {

                                productsByCategoryResponse.result?.pagination?.let {
                                    total_pages = it.total_pages
                                }
                                if (current_page == 1) {
                                    products.clear()
                                }

                                // if current page = 1 we must clear the array else we must add the new products to old array
                                delay(100)

                                products.addAll(productsByCategoryResponse.result?.products!!)
                                productRecyclerViewAdapter.differ.submitList(productsByCategoryResponse.result.products)
                                productRecyclerViewAdapter.notifyDataSetChanged()
                                binding.productRecyclerView.setVisibility(View.VISIBLE)

                                subCategoryRecyclerViewAdapter.differ.submitList(productsByCategoryResponse.result.subcategories!!)
                                binding.subCategoryRecyclerView.setVisibility(View.VISIBLE)

                                can_scroll = true
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
                                Common.setUpSessionProgressDialog(this@CategoryDetailActivity)
                            }
                        }
                    }

                    is Resource.Connection -> {
                        Log.d(TAG, "ERROR CONNECTION")
                        setUpErrorForm(Constant.CONNECTION)
                    }

                    is Resource.Loading -> {
                        setLoading()
                        Log.d(Constant.PRODUCT_TAG, "LOADING")
                    }

                    is Resource.LoadingWithProducts -> {
                        setLoadingWithProduct()
                        Log.d(Constant.PRODUCT_TAG, "LOADING")
                    }
                }
            }
        }
    }


    fun setUpSubCategoriesAdapter() {
        binding.apply {
            subCategoryRecyclerViewAdapter = SubCategoryRecyclerViewAdapter()
            val subCategories = ArrayList<SubCategory>()

            subCategoryRecyclerView.layoutManager = LinearLayoutManager(
                this@CategoryDetailActivity,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            subCategoryRecyclerView.setItemAnimator(DefaultItemAnimator())
            subCategoryRecyclerView.setAdapter(subCategoryRecyclerViewAdapter)
            subCategoryRecyclerViewAdapter.differ.submitList(subCategories)
        }
    }

    fun setUpProductsByCategoryAdapter() {
        binding.apply {
            productRecyclerViewAdapter = ProductRecyclerViewAdapter(false,false,false,viewModel,null)
            productRecyclerView.layoutManager = GridLayoutManager(this@CategoryDetailActivity, 2)
            productRecyclerView.setItemAnimator(DefaultItemAnimator())
            productRecyclerView.setAdapter(productRecyclerViewAdapter)
            productRecyclerViewAdapter.differ.submitList(products)
        }
    }

    private fun addScrollListener() {
        binding.apply {

            productRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val layoutManager = recyclerView.layoutManager as GridLayoutManager?
                    val lastVisible = layoutManager!!.findLastVisibleItemPosition()
                    Log.d("LAST_VISIBLE_ITEM",lastVisible.toString())
                    if (lastVisible >= products.size - 2 && current_page < total_pages) {
                        if (can_scroll) {
                            can_scroll = false
                            current_page++
                            viewModel.getProductsByCategory(category_id,
                                PaginationBodyParameters(
                                    PaginationParams(
                                        current_page
                                    )
                                )
                            )
                            productRecyclerView.smoothScrollToPosition(lastVisible + 1)
                        }
                    }
                }
            })
        }
    }


    private fun stopShimmer() {
        binding.apply {
            productAndSubShimmer.visibility = View.GONE
            productAndSubShimmer.stopShimmer()
            nestedScrollView.visibility = View.GONE
        }
    }

    fun setLoading() {
        binding.apply {
            productRecyclerView.visibility = View.GONE
            subCategoryRecyclerView.visibility = View.GONE
            progressBar.visibility = View.GONE
            productAndSubShimmer.visibility = View.VISIBLE
            productAndSubShimmer.startShimmer()
            nestedScrollView.visibility = View.VISIBLE
            categoryDetailsErrorContainer.root.visibility = View.GONE
        }
    }

    fun setLoadingWithProduct() {
        binding.apply {
            categoryDetailsErrorContainer.root.visibility = View.GONE
            productRecyclerView.visibility = View.VISIBLE
            progressBar.visibility = View.VISIBLE
            nestedScrollView.visibility = View.GONE
        }
    }

    fun onRefresh() {
        setLoading()
        binding.apply {
            viewModel.getProductsByCategory(category_id,PaginationBodyParameters(PaginationParams(current_page)))
        }
    }

    fun setUpErrorForm(error_type: String) {
        binding.apply {
            productRecyclerView.visibility = View.GONE
            subCategoryRecyclerView.visibility = View.GONE
            progressBar.visibility = View.GONE
            stopShimmer()

            categoryDetailsErrorContainer.apply {
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
                    Constant.NO_PRODUCTS_BY_CATEGORY -> {
                        errorMsg1.text =
                            viewModel.getLangResources().getString(R.string.products_by_category)

                        errorMsg2.text =
                            viewModel.getLangResources()
                                .getString(R.string.no_products_by_category)
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