package com.quico.tech.activity

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CompoundButton
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
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
import com.quico.tech.model.*
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
    private lateinit var viewModel2: SharedViewModel
    private var is_vip_price: Boolean = false
    private var quantity: Int = 1

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

            toolbarProductDetails.backArrow.setOnClickListener {
                onBackPressed()
            }
            // oldPrice.setPaintFlags(oldPrice.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG) // draw line on old price


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
            selectPriceContainer.visibility = View.GONE
            userVipContainer.visibility = View.GONE

            product_id = product.id
            if (product.is_on_sale) {
                bestSellerText.visibility = View.VISIBLE
                bestSellerText.text = viewModel.getLangResources().getString(R.string.best_seller)
            }

            name.text = product.name
            description.text = product.description

            if (!product.specifications.isNullOrEmpty()) {
                showHideText.visibility = View.VISIBLE
                setUpDetailsAdapter(product.specifications)
                showHideText.setOnClickListener {
                    showText = !showText

                    if (showText) {
                        detailsContainer.visibility = View.VISIBLE
                        showHideText.text =
                            viewModel.getLangResources().getString(R.string.see_less)

                    } else {
                        detailsContainer.visibility = View.GONE
                        showHideText.text =
                            viewModel.getLangResources().getString(R.string.show_more)

                    }
                }
            } else {
                showHideText.visibility = View.VISIBLE
                detailsContainer.visibility = View.GONE
            }

            compareBtn.setOnClickListener {

                startActivity(
                    Intent(this@ProductActivity, CompareProductActivity::class.java)
                        .putExtra(Constant.ITEM_ID, product.id)
                )
            }


            if (product.is_vip) {
                salePriceContainer.visibility = View.GONE
                vipText.visibility = View.VISIBLE
                vipText.text = viewModel.getLangResources().getString(R.string.vip_price)

                if (viewModel.user != null) {
                    viewModel.user?.let { user ->
                        if (user.is_vip) {
                            userVipContainer.visibility = View.VISIBLE
                            selectPriceContainer.visibility = View.GONE
                            vipBadge.text =
                                viewModel.getLangResources().getString(R.string.vip_price)
                            price.text = "$ ${product.new_price.toString()}"
                        } else {
                            selectPriceContainer.visibility = View.VISIBLE
                            userVipContainer.visibility = View.GONE
                            managePriceType()
                        }
                    }
                }
            } else if (product.is_on_sale) {
                salePriceContainer.visibility = View.VISIBLE
                userVipContainer.visibility = View.GONE
                selectPriceContainer.visibility = View.GONE
                bestSellerText.visibility = View.VISIBLE
                bestSellerText.text = viewModel.getLangResources().getString(R.string.best_seller)

                oldPrice.text = "$ ${product.regular_price.toString()}"
                newPrice.text = "$ ${product.new_price.toString()}"
                oldPrice.setBackground(getResources().getDrawable(R.drawable.red_line))
            } else {
                oldPrice.visibility = View.GONE
                newPrice.text = "$ ${product.regular_price.toString()}"
            }
            plusMinusQty()
            addToCartBtn.setOnClickListener {
                // if item is out of stock tell him
                // else check if user logged in
                checkUser()
            }
        }
    }

    private fun checkUser() {
        if (viewModel.user == null) {
            Common.setUpChoicesAlert(
                this@ProductActivity,
                viewModel.getLangResources().getString(R.string.login),
                viewModel.getLangResources()
                    .getString(R.string.please_login),
                viewModel.getLangResources().getString(R.string.cancel),
                viewModel.getLangResources().getString(R.string.login),
                object : Common.ResponseChoices {
                    override fun onConfirm() {
                        startActivity(
                            Intent(
                                this@ProductActivity,
                                LoginActivity::class.java
                            )
                        )
                    }

                    override fun onCancel() {
                    }
                }
            )
        } else {
            if (!viewModel.vip_subsription) {
                //subscribe
                // then add to cart
            } else {
                addToCart(is_vip_price, product_id!!, quantity)
            }
        }
    }

    private fun managePriceType() {
        binding.apply {
            regularRadioBtn.setOnCheckedChangeListener(object :
                CompoundButton.OnCheckedChangeListener {
                override fun onCheckedChanged(p0: CompoundButton?, checked: Boolean) {
                    if (checked) {
                        is_vip_price = false
                        vipRadioBtn.setChecked(false)
                    }
                }
            })

            vipRadioBtn.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
                override fun onCheckedChanged(p0: CompoundButton?, checked: Boolean) {
                    if (checked) {
                        is_vip_price = true
                        regularRadioBtn.setChecked(false)
                    }
                }
            })
        }
    }

    private fun plusMinusQty() {
        binding.apply {
            plus.setOnClickListener {
                quantity++
                qty.text = quantity.toString()
            }

            minus.setOnClickListener {
                if (quantity > 1) {
                    quantity--
                    qty.text = quantity.toString()
                }
            }
        }
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

    private fun setUpDetailsAdapter(specifications: ArrayList<Specifications>) {
        binding.apply {
            productDetailsRecyclerViewAdapter = ProductDetailsRecyclerViewAdapter()

            recyclerView.layoutManager =
                LinearLayoutManager(this@ProductActivity, LinearLayoutManager.VERTICAL, false)
            recyclerView.setItemAnimator(DefaultItemAnimator())
            recyclerView.setAdapter(productDetailsRecyclerViewAdapter)
            productDetailsRecyclerViewAdapter.differ.submitList(specifications)
        }
    }

    private fun addToCart(is_vip_price: Boolean, product_id: Int, quantity: Int) {

        var cartBodyParameters: CartBodyParameters? = null
        if (viewModel.user!!.is_vip)
            cartBodyParameters = CartBodyParameters(CartParams(product_id, quantity))
        else
            cartBodyParameters = CartBodyParameters(CartParams(is_vip_price, product_id, quantity))

        Common.setUpProgressDialog(this)
        viewModel.addToCart(cartBodyParameters,
            object : SharedViewModel.ResponseStandard {
                override fun onSuccess(
                    success: Boolean,
                    resultTitle: String,
                    message: String
                ) {
                    // later add progress bar to view
                    Common.cancelProgressDialog()
                    Toast.makeText(
                        this@ProductActivity,
                        message,
                        Toast.LENGTH_LONG
                    ).show()
                }

                override fun onFailure(
                    success: Boolean,
                    resultTitle: String,
                    message: String
                ) {
                    Common.cancelProgressDialog()
                    Common.setUpAlert(
                        this@ProductActivity, false,
                        viewModel.getLangResources()
                            .getString(R.string.error),
                        message,
                        viewModel.getLangResources().getString(R.string.ok),
                        null
                    )
                }
            })
    }

    private fun subscribeToVip(vip_id: Int) {

        var cartBodyParameters = CartBodyParameters(CartParams(vip_id))

        Common.setUpProgressDialog(this)
        viewModel.subscribeToVip(cartBodyParameters,
            object : SharedViewModel.ResponseStandard {
                override fun onSuccess(
                    success: Boolean,
                    resultTitle: String,
                    message: String
                ) {
                    // later add progress bar to view
                    Common.cancelProgressDialog()
                    Toast.makeText(
                        this@ProductActivity,
                        message,
                        Toast.LENGTH_LONG
                    ).show()
                    addToCart(is_vip_price,product_id!!,quantity)
                }

                override fun onFailure(
                    success: Boolean,
                    resultTitle: String,
                    message: String
                ) {
                    Common.cancelProgressDialog()
                    Common.setUpAlert(
                        this@ProductActivity, false,
                        viewModel.getLangResources()
                            .getString(R.string.error),
                        message,
                        viewModel.getLangResources().getString(R.string.ok),
                        null
                    )
                }
            })
    }


    private fun addToWishList() {

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