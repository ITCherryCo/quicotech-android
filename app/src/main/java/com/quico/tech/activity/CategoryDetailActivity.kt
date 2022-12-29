package com.quico.tech.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.quico.tech.R
import com.quico.tech.adapter.BrandSearchRecyclerViewAdapter
import com.quico.tech.adapter.ProductRecyclerViewAdapter
import com.quico.tech.adapter.SubCategoryRecyclerViewAdapter
import com.quico.tech.data.Constant
import com.quico.tech.databinding.ActivityCategoryDetailBinding
import com.quico.tech.databinding.ActivityCompareSearchBinding
import com.quico.tech.model.Brand
import com.quico.tech.model.Product
import com.quico.tech.model.SubCategory
import com.quico.tech.utils.Common
import com.quico.tech.viewmodel.SharedViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CategoryDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCategoryDetailBinding
    private val viewModel: SharedViewModel by viewModels()
    private lateinit var productRecyclerViewAdapter: ProductRecyclerViewAdapter
    private lateinit var subCategoryRecyclerViewAdapter: SubCategoryRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            backArrow.setOnClickListener {
                onBackPressed()
            }
        }
        initToolbar()
        setUpText()
        setSubCategoryLoading()
        // setUpCartAdapter()

        //onRefresh()
//        subscribeAddresses()
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

    private fun setUpText() {
        binding.apply {

            title.text = viewModel.getLangResources().getString(R.string.single_category)
            searchView.queryHint = viewModel.getLangResources().getString(R.string.search_hint)
        }
    }

    fun setUpSubCategoryAdapter() {
        binding.apply {
            subCategoryRecyclerViewAdapter = SubCategoryRecyclerViewAdapter()
            val subCategories = ArrayList<SubCategory>()
            stopProductShimmer()
            subCategoryRecyclerView.visibility = View.VISIBLE
            // filterImage.setEnabled(true)
            subCategories.add(SubCategory("SUBCATEGORY"))
            subCategories.add(SubCategory("SUBCATEGORY"))
            subCategories.add(SubCategory("SUBCATEGORY"))
            subCategories.add(SubCategory("SUBCATEGORY"))
            subCategories.add(SubCategory("SUBCATEGORY"))
            subCategories.add(SubCategory("SUBCATEGORY"))
            subCategories.add(SubCategory("SUBCATEGORY"))

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

    fun setUpProductsAdapter() {
        binding.apply {
            productRecyclerViewAdapter = ProductRecyclerViewAdapter(false,false,object :ProductRecyclerViewAdapter.OnProductSelect{
                override fun onProductSelect(product: Product?) {

                }
            })
            val items = ArrayList<Product>()
            stopProductShimmer()
            productRecyclerView.visibility = View.VISIBLE
            // filterImage.setEnabled(true)

            items.add(Product("P1", resources.getDrawable(R.drawable.product_image_test), 9.9f))
            items.add(Product("P2", resources.getDrawable(R.drawable.product_image_test), 22.9f))
            items.add(Product("P3", resources.getDrawable(R.drawable.product_image_test), 43.9f))
            items.add(Product("P4", resources.getDrawable(R.drawable.product_image_test), 45.22f))
            items.add(Product("P5", resources.getDrawable(R.drawable.product_image_test), 93.9f))
            items.add(Product("P6", resources.getDrawable(R.drawable.product_image_test), 49.9f))
            items.add(Product("P7", resources.getDrawable(R.drawable.product_image_test), 19.9f))
            items.add(Product("P8", resources.getDrawable(R.drawable.product_image_test), 59.9f))
            items.add(Product("P9", resources.getDrawable(R.drawable.product_image_test), 69.9f))

            productRecyclerView.layoutManager = GridLayoutManager(this@CategoryDetailActivity, 2)
            productRecyclerView.setItemAnimator(DefaultItemAnimator())
            productRecyclerView.setAdapter(productRecyclerViewAdapter)
            productRecyclerViewAdapter.differ.submitList(items)
        }
    }

    private fun stopBrandShimmer() {
        binding.apply {
            brandShimmer.visibility = View.GONE
            brandShimmer.stopShimmer()
        }
    }


    private fun stopProductShimmer() {
        binding.apply {
            productShimmer.visibility = View.GONE
            productShimmer.stopShimmer()
        }
    }

    fun setSubCategoryLoading() {
        binding.apply {
            brandShimmer.visibility = View.GONE
            productRecyclerView.visibility = View.GONE
            progressBar.visibility = View.GONE
            brandShimmer.visibility = View.VISIBLE
            brandShimmer.startShimmer()
            // filterImage.setEnabled(false)

            lifecycleScope.launch {
                delay(2000)
                setUpSubCategoryAdapter()

                stopBrandShimmer()
                setProductsLoading()
            }
        }
    }

    fun setProductsLoading() {
        binding.apply {
            productRecyclerView.visibility = View.GONE
            progressBar.visibility = View.GONE
            productShimmer.visibility = View.VISIBLE
            productShimmer.startShimmer()

            lifecycleScope.launch {
                delay(1500)
                setUpProductsAdapter()
            }
        }
    }
}