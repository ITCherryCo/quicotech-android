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
import com.quico.tech.data.Constant
import com.quico.tech.databinding.ActivityCompareSearchBinding
import com.quico.tech.model.Brand
import com.quico.tech.model.Product
import com.quico.tech.viewmodel.SharedViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CompareSearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCompareSearchBinding
    private val viewModel: SharedViewModel by viewModels()
    private lateinit var productRecyclerViewAdapter: ProductRecyclerViewAdapter
    private lateinit var brandSearchRecyclerViewAdapter: BrandSearchRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCompareSearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            compareProductBtn.setOnClickListener {
                startActivity(
                    Intent(this@CompareSearchActivity, CompareProductActivity::class.java)
                    .putExtra(Constant.VERIFICATION_TYPE, Constant.EMAIL)
                    .putExtra(Constant.VERIFICATION_TYPE, Constant.EMAIL)
                )
            }
            backArrow.setOnClickListener {
                onBackPressed()
            }
        }
        setUpText()
        setBrandsLoading()
        // setUpCartAdapter()

        //onRefresh()
//        subscribeAddresses()
    }

    private fun setUpText() {
        binding.apply {

            title.text = viewModel.getLangResources().getString(R.string.filters)
            instructionText.text = viewModel.getLangResources().getString(R.string.select_item_to_compare)
            searchView.queryHint = viewModel.getLangResources().getString(R.string.search_hint)
        }
    }

    fun setUpBrandsAdapter() {
        binding.apply {
            brandSearchRecyclerViewAdapter = BrandSearchRecyclerViewAdapter()
            val brands = ArrayList<Brand>()
            stopProductShimmer()
            brandRecyclerView.visibility = View.VISIBLE
            // filterImage.setEnabled(true)
            brands.add(Brand(1, "all",null))
            brands.add(Brand(2, "Brand Name", R.drawable.dji))
            brands.add(Brand(3, "Brand Name", R.drawable.evo))
            brands.add(Brand(4, "Brand Name", R.drawable.sony))
            brands.add(Brand(5, "Brand Name", R.drawable.canon))
            brands.add(Brand(6, "Brand Name", R.drawable.nikon))
            brands.add(Brand(7, "Brand Name", R.drawable.dji))

            brandRecyclerView.layoutManager = LinearLayoutManager(
                this@CompareSearchActivity,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            brandRecyclerView.setItemAnimator(DefaultItemAnimator())
            brandRecyclerView.setAdapter(brandSearchRecyclerViewAdapter)
            brandSearchRecyclerViewAdapter.differ.submitList(brands)
        }
    }
    fun setUpItemsAdapter() {
        binding.apply {
            productRecyclerViewAdapter = ProductRecyclerViewAdapter(false,true,object :ProductRecyclerViewAdapter.OnProductSelect{
                override fun onProductSelect(product: Product?) {
                    product?.let {
                        compareProductBtn.setEnabled(true)
                    }
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

            productRecyclerView.layoutManager = GridLayoutManager(this@CompareSearchActivity, 2)
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

    fun setBrandsLoading() {
        binding.apply {
            compareProductBtn.setEnabled(false)
            brandShimmer.visibility = View.GONE
            productRecyclerView.visibility = View.GONE
            progressBar.visibility = View.GONE
            brandShimmer.visibility = View.VISIBLE
            brandShimmer.startShimmer()
            // filterImage.setEnabled(false)

            lifecycleScope.launch {
                delay(2000)
                setUpBrandsAdapter()

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
                setUpItemsAdapter()
            }
        }
    }
}