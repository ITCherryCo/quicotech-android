package com.quico.tech.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import com.quico.tech.R
import com.quico.tech.adapter.ProductRecyclerViewAdapter
import com.quico.tech.data.Constant
import com.quico.tech.data.Constant.SEARCH_TEXT
import com.quico.tech.databinding.ActivitySearchHomeBinding
import com.quico.tech.model.SearchParams
import com.quico.tech.model.Product
import com.quico.tech.model.SearchBodyParameters
import com.quico.tech.utils.Common
import com.quico.tech.utils.Resource
import com.quico.tech.viewmodel.SharedViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchHomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchHomeBinding
    private val viewModel: SharedViewModel by viewModels()
    private lateinit var productRecyclerViewAdapter: ProductRecyclerViewAdapter
    private var search_text = ""
    private var products = ArrayList<Product>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySearchHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        setUpText()
        setUpProductsAdapter()
        subscribeProducts()
        setUpSearchView()
        products.clear()
        if (!SEARCH_TEXT.isNullOrEmpty()) {

            binding.searchView.setQuery(SEARCH_TEXT,true)
           // viewModel.searchProducts(SearchBodyParameters(NameParams(search_text)))
        }

    }

    fun init() {
        //Remove Statusbar
        Common.setSystemBarColor(this@SearchHomeActivity, R.color.home_background_grey)
        Common.setSystemBarLight(this@SearchHomeActivity)
    }

    private fun setUpText() {
        binding.apply {


            //searchView.queryHint = viewModel.getLangResources().getString(R.string.search_hint)
            searchView.setQueryHint(Html.fromHtml("<font color = #5F5F5F>" + viewModel.getLangResources().getString(R.string.search_hint) + "</font>"));

            searchView.requestFocus()
            searchHomeToolbar.apply {
                title.text = viewModel.getLangResources().getString(R.string.search)
                heartImage.visibility = View.GONE
                if (viewModel.getLanguage().equals(Constant.AR))
                    backArrow.scaleX = -1f

                backArrow.setOnClickListener {
                    onBackPressed()
                }

                cartImage.setOnClickListener {
                    startActivity(Intent(this@SearchHomeActivity,CartActivity::class.java))
                }
            }
        }
    }

    private fun setUpSearchView(){
        binding.apply {
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
                android.widget.SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(searchText: String): Boolean {

                    search_text = searchText
                    if (searchText.isEmpty()){
                        setUpErrorForm(Constant.EMPTY_SEARCH)
                        SEARCH_TEXT=""
                        products.clear()
                    }
                    else if(searchText.trim().length>2) {
                      //  search_text = searchText.trim()
                        search_text = searchText.trim()

                        lifecycleScope.launch {
                          //  delay(100)
                            viewModel.searchProducts(
                                SearchBodyParameters(
                                    SearchParams(
                                        1,
                                        search_text
                                    )
                                )
                            )
                        }
                    }
                    return false
                }
            })
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        SEARCH_TEXT=""
    }

    private fun subscribeProducts() {
        lifecycleScope.launch {
            viewModel.search_products.collect { response ->
                when (response) {
                    is Resource.Success -> {
                        binding.apply {
                            stopShimmer()
                        }

                        response.data?.let { itemsResponse ->
                            if (itemsResponse.result?.products.isNullOrEmpty()) {
                                 setUpErrorForm(Constant.NO_ITEMS)
                            } else {
                                products.clear()
                                products.addAll(itemsResponse.result.products)
                                delay(200)
                                productRecyclerViewAdapter.differ.submitList(itemsResponse.result.products)
                             //   productRecyclerViewAdapter.differ.submitList(products)
                                binding.productRecyclerView.visibility = View.VISIBLE
                            }
                        }
                        Log.d(Constant.PRODUCT_TAG, "SUCCESS")
                    }

                    is Resource.Error -> {
                        response.message?.let { message ->
                            Log.d(Constant.PRODUCT_TAG, "ERROR $message")
                            setUpErrorForm(Constant.ERROR)
                        }
                    }

                    is Resource.Connection -> {
                        Log.d(Constant.PRODUCT_TAG, "ERROR CONNECTION")
                        setUpErrorForm(Constant.CONNECTION)
                    }

                    is Resource.Loading -> {
                        setLoading()
                        Log.d(Constant.PRODUCT_TAG, "LOADING")
                    }
                }
            }
        }
    }

    fun setUpProductsAdapter() {
        binding.apply {
            productRecyclerViewAdapter = ProductRecyclerViewAdapter(false,false,false,viewModel,null)

            productRecyclerView.layoutManager = GridLayoutManager(this@SearchHomeActivity, 2)
            productRecyclerView.setItemAnimator(DefaultItemAnimator())
            productRecyclerView.setAdapter(productRecyclerViewAdapter)
            productRecyclerViewAdapter.differ.submitList(products)
        }
    }

    fun setLoading() {
        binding.apply {
            itemsErrorContainer.root.visibility = View.GONE
            productRecyclerView.visibility = View.GONE
            progressBar.visibility = View.GONE
            productShimmer.visibility = View.VISIBLE
            productShimmer.startShimmer()
        }
    }

    private fun stopShimmer() {
        binding.apply {
            productShimmer.visibility = View.GONE
            lifecycleScope.launch {
                delay(1000)
                productShimmer.stopShimmer()
            }
        }
    }

    private fun onRefresh(){
        setLoading()
        binding.apply {
            lifecycleScope.launch {
                delay(100)
                viewModel.searchProducts(SearchBodyParameters(SearchParams(search_text)))
            }
        }
    }

    fun setUpErrorForm(error_type: String) {
        binding.apply {
            stopShimmer()
            productRecyclerView.visibility = View.GONE

            itemsErrorContainer.apply {
                root.visibility = View.VISIBLE
                tryAgain.visibility = View.VISIBLE
                errorMsg1.visibility = View.VISIBLE
                errorBtn.visibility = View.GONE
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
                    Constant.EMPTY_SEARCH -> {
                        errorMsg1.visibility = View.GONE
                        tryAgain.visibility = View.GONE

                        errorMsg2.text =
                            viewModel.getLangResources().getString(R.string.empty_search)
                    }

                    Constant.NO_ITEMS -> {
                        errorMsg1.visibility = View.GONE

                        errorMsg2.text =
                            viewModel.getLangResources().getString(R.string.no_items)
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