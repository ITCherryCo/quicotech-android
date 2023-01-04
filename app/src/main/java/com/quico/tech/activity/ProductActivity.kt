package com.quico.tech.activity

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.quico.tech.R
import com.quico.tech.adapter.CardRecyclerViewAdapter
import com.quico.tech.adapter.MaintenanceRecyclerViewAdapter
import com.quico.tech.adapter.ProductDetailsRecyclerViewAdapter
import com.quico.tech.adapter.ProductImageAdapter
import com.quico.tech.data.Constant
import com.quico.tech.data.Constant.PRODUCT_ID
import com.quico.tech.data.Constant.PRODUCT_NAME
import com.quico.tech.databinding.ActivityProductBinding
import com.quico.tech.model.Card
import com.quico.tech.model.Product
import com.quico.tech.model.ProductDetails
import com.quico.tech.utils.Common
import com.quico.tech.utils.Resource
import com.quico.tech.viewmodel.SharedViewModel
import kotlinx.coroutines.launch


class ProductActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProductBinding
    private val viewModel: SharedViewModel by viewModels()
    private lateinit var productDetailsRecyclerViewAdapter: ProductDetailsRecyclerViewAdapter
    private var showText = false
    private var product_id: Int? = 0
    private var product_name: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initToolbar()

        product_id = intent?.extras?.getInt(PRODUCT_ID)
        product_name = intent?.extras?.getString(PRODUCT_NAME)!!
        setUpText()

        product_id?.let { product_id ->
            if (product_id != 0)
                viewModel.getProduct(product_id)
        }
        onRefresh()
        subscribeProduct()
    }

    fun initToolbar() {
        binding.apply {
            Common.setSystemBarColor(this@ProductActivity, R.color.home_background_grey)
            Common.setSystemBarLight(this@ProductActivity)
            toolbarProductDetails.title.text =
                viewModel.getLangResources().getString(R.string.single_offer_title)
        }
    }


    private fun setUpText() {
        binding.apply {
            newText.text = viewModel.getLangResources().getString(R.string.new_text)
            bestSellerText.text = viewModel.getLangResources().getString(R.string.best_seller)
            compareBtn.text = viewModel.getLangResources().getString(R.string.compare)
            addToCartBtn.text = viewModel.getLangResources().getString(R.string.add_to_cart)
            showHideText.text = viewModel.getLangResources().getString(R.string.show_more)
            detailsText.text = viewModel.getLangResources().getString(R.string.details)
            name.text = product_name
            toolbarProductDetails.title.text = product_name

            showHideText.setPaintFlags(oldPrice.getPaintFlags() or Paint.UNDERLINE_TEXT_FLAG) // draw line on old price

            if (viewModel.getLanguage().equals(Constant.AR))
                toolbarProductDetails.backArrow.scaleX = -1f

            // oldPrice.setPaintFlags(oldPrice.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG) // draw line on old price

            compareBtn.setOnClickListener {
                //startActivity(Intent(this@ProductActivity,CompareSearchActivity::class.java))
                startActivity(
                    Intent(this@ProductActivity, CompareProductActivity::class.java)
                        .putExtra(Constant.ITEM_ID, 1)
                )
            }

            toolbarProductDetails.cartImage.setOnClickListener {
                startActivity(Intent(this@ProductActivity, CartActivity::class.java))
            }

            detailsContainer.visibility = View.GONE

        }
    }

    fun subscribeProduct() {
        lifecycleScope.launch {
            viewModel.product.collect { response ->
                when (response) {
                    is Resource.Success -> {
                        response.data?.let { servicesResponse ->
                            binding.apply {
                                swipeRefreshLayout.setRefreshing(false)
                                swipeRefreshLayout.setEnabled(false)
                                mainContainer.visibility = View.VISIBLE
                            }

                            if (servicesResponse.result == null)
                                setUpErrorForm(Constant.NO_ITEM)
                            else {
                                setProductData(servicesResponse.result)
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

    private fun setProductData(product: Product) {
        binding.apply {
            if (!product.images.isNullOrEmpty())
                setImages(product.images)

            bestSellerText.visibility = View.GONE
            newText.visibility = View.GONE
            vipText.visibility = View.GONE

            if (product.is_on_sale) {
                bestSellerText.visibility = View.VISIBLE
                bestSellerText.text = viewModel.getLangResources().getString(R.string.best_seller)
            }

            if (product.is_vip) {
                vipText.visibility = View.VISIBLE
                vipText.text = viewModel.getLangResources().getString(R.string.best_seller)
            }
            if (product.is_vip || product.is_on_sale) {
                oldPrice.visibility = View.VISIBLE
                newPrice.visibility = View.VISIBLE
                oldPrice.text = "$ ${product.regular_price.toString()}"
                newPrice.text = "$ ${product.new_price.toString()}"
                oldPrice.setBackground(getResources().getDrawable(R.drawable.red_line))
            }
            else{
                oldPrice.visibility = View.GONE
                newPrice.text = "$ ${product.regular_price.toString()}"
            }

            name.text = product.name
            description.text = product.description

            if (!product.specifications.isNullOrEmpty()){
                showHideText.visibility = View.VISIBLE
                setUpDetailsAdapter(product.specifications)
                showHideText.setOnClickListener {
                    showText = !showText

                    if (showText) {
                        detailsContainer.visibility = View.VISIBLE
                        showHideText.text = viewModel.getLangResources().getString(R.string.see_less)

                    } else {
                        detailsContainer.visibility = View.GONE
                        showHideText.text = viewModel.getLangResources().getString(R.string.show_more)

                    }
                }
            }
            else {
                showHideText.visibility = View.VISIBLE
                detailsContainer.visibility = View.GONE
            }
        }
    }

    private fun minusQty(){

    }

    private fun setImages(images: ArrayList<String>) {

        // var images =  arrayOf(R.drawable.product_image_test, R.drawable.computer_all, R.drawable.headphones)

        binding.apply {
            var productImageAdapter = ProductImageAdapter(images)
            viewPager.setAdapter(productImageAdapter)
            productImageAdapter.registerAdapterDataObserver(indicator.getAdapterDataObserver())
            indicator.setViewPager(viewPager)
        }
    }

    private fun setUpDetailsAdapter( details:ArrayList<ArrayList<String>>) {
        binding.apply {
            productDetailsRecyclerViewAdapter = ProductDetailsRecyclerViewAdapter()

            recyclerView.layoutManager =
                LinearLayoutManager(this@ProductActivity, LinearLayoutManager.VERTICAL, false)
            recyclerView.setItemAnimator(DefaultItemAnimator())
            recyclerView.setAdapter(productDetailsRecyclerViewAdapter)
            productDetailsRecyclerViewAdapter.differ.submitList(details)
        }
    }

    fun setLoading() {
        binding.apply {
            swipeRefreshLayout.setRefreshing(true)
            mainContainer.visibility = View.GONE
            productErrorContainer.root.visibility = View.GONE
        }
    }

    fun onRefresh() {
        binding.apply {
            swipeRefreshLayout.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
                    setLoading()
                    viewModel.getProduct(product_id!!) // get User id
            })
        }
    }

    fun setUpErrorForm(error_type: String) {
        binding.apply {
            mainContainer.visibility = View.GONE
            swipeRefreshLayout.setRefreshing(false)
            swipeRefreshLayout.setEnabled(true)

            productErrorContainer.apply {
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
                    Constant.NO_ITEM -> {
                        errorMsg2.text =
                            viewModel.getLangResources().getString(R.string.no_item)
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