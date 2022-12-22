package com.quico.tech.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.quico.tech.R
import com.quico.tech.adapter.AddressSelectionRecyclerViewAdapter
import com.quico.tech.adapter.CategoryRecyclerViewAdapter
import com.quico.tech.adapter.ProductRecyclerViewAdapter
import com.quico.tech.databinding.ActivityHomeBinding
import com.quico.tech.databinding.FragmentHomeBinding
import com.quico.tech.model.Address
import com.quico.tech.model.Category
import com.quico.tech.model.Product


class HomeFragment : Fragment() {
    private lateinit var binding : FragmentHomeBinding
    private lateinit var categoryRecyclerViewAdapter: CategoryRecyclerViewAdapter
    private lateinit var productRecyclerViewAdapter: ProductRecyclerViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        getCategories();
        getHotDeals();
        getBestSellingProducts();
        getOffersProducts();
        return binding.getRoot()
    }

    fun getCategories(){
        binding.apply {

            categoryRecyclerViewAdapter = CategoryRecyclerViewAdapter(false)
            val categories = ArrayList<Category>()
            categories.add(Category("Servers",resources.getDrawable(R.drawable.server)))
            categories.add(Category("Computers",resources.getDrawable(R.drawable.computer)))
            categories.add(Category("Mobiles",resources.getDrawable(R.drawable.aurdino)))
            categories.add(Category("Servers",resources.getDrawable(R.drawable.games)))
            categories.add(Category("Computers",resources.getDrawable(R.drawable.computer)))
            categories.add(Category("Mobiles",resources.getDrawable(R.drawable.aurdino)))
            categories.add(Category("Servers",resources.getDrawable(R.drawable.server)))
            categories.add(Category("Computers",resources.getDrawable(R.drawable.computer)))
            categories.add(Category("Mobiles",resources.getDrawable(R.drawable.server)))

            homeContent.recyclerViewCategories.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
            homeContent.recyclerViewCategories.setItemAnimator(DefaultItemAnimator())
            homeContent.recyclerViewCategories.setAdapter(categoryRecyclerViewAdapter)

            categoryRecyclerViewAdapter.differ.submitList(categories)


        }
    }

    fun getHotDeals(){
        binding.apply {
            productRecyclerViewAdapter = ProductRecyclerViewAdapter(true,false,null)
            val hotDealsProducts = ArrayList<Product>()
            hotDealsProducts.add(Product("P1",resources.getDrawable(R.drawable.product_image_test),9.9f))
            hotDealsProducts.add(Product("P2",resources.getDrawable(R.drawable.product_image_test),22.9f))
            hotDealsProducts.add(Product("P3",resources.getDrawable(R.drawable.product_image_test),43.9f))
            hotDealsProducts.add(Product("P4",resources.getDrawable(R.drawable.product_image_test),45.22f))
            hotDealsProducts.add(Product("P5",resources.getDrawable(R.drawable.product_image_test),93.9f))
            hotDealsProducts.add(Product("P6",resources.getDrawable(R.drawable.product_image_test),49.9f))
            hotDealsProducts.add(Product("P7",resources.getDrawable(R.drawable.product_image_test),19.9f))
            hotDealsProducts.add(Product("P8",resources.getDrawable(R.drawable.product_image_test),59.9f))
            hotDealsProducts.add(Product("P9",resources.getDrawable(R.drawable.product_image_test),69.9f))

            homeContent.recyclerViewHotDeals.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
            homeContent.recyclerViewHotDeals.setItemAnimator(DefaultItemAnimator())
            homeContent.recyclerViewHotDeals.setAdapter(productRecyclerViewAdapter)

            productRecyclerViewAdapter.differ.submitList(hotDealsProducts)
        }
    }

    fun getBestSellingProducts(){
        binding.apply {
            productRecyclerViewAdapter = ProductRecyclerViewAdapter(true,false,null)
            val bestSellingProducts = ArrayList<Product>()
            bestSellingProducts.add(Product("P1",resources.getDrawable(R.drawable.product_image_test),9.9f))
            bestSellingProducts.add(Product("P2",resources.getDrawable(R.drawable.product_image_test),22.9f))
            bestSellingProducts.add(Product("P3",resources.getDrawable(R.drawable.product_image_test),43.9f))
            bestSellingProducts.add(Product("P4",resources.getDrawable(R.drawable.product_image_test),45.22f))
            bestSellingProducts.add(Product("P5",resources.getDrawable(R.drawable.product_image_test),93.9f))
            bestSellingProducts.add(Product("P6",resources.getDrawable(R.drawable.product_image_test),49.9f))
            bestSellingProducts.add(Product("P7",resources.getDrawable(R.drawable.product_image_test),19.9f))
            bestSellingProducts.add(Product("P8",resources.getDrawable(R.drawable.product_image_test),59.9f))
            bestSellingProducts.add(Product("P9",resources.getDrawable(R.drawable.product_image_test),69.9f))

            homeContent.recyclerViewBestSelling.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
            homeContent.recyclerViewBestSelling.setItemAnimator(DefaultItemAnimator())
            homeContent.recyclerViewBestSelling.setAdapter(productRecyclerViewAdapter)

            productRecyclerViewAdapter.differ.submitList(bestSellingProducts)
        }
    }

    fun getOffersProducts(){
        binding.apply {
            productRecyclerViewAdapter = ProductRecyclerViewAdapter(true,false,null)
            val offersProducts = ArrayList<Product>()
            offersProducts.add(Product("P1",resources.getDrawable(R.drawable.product_image_test),9.9f))
            offersProducts.add(Product("P2",resources.getDrawable(R.drawable.product_image_test),22.9f))
            offersProducts.add(Product("P3",resources.getDrawable(R.drawable.product_image_test),43.9f))
            offersProducts.add(Product("P4",resources.getDrawable(R.drawable.product_image_test),45.22f))
            offersProducts.add(Product("P5",resources.getDrawable(R.drawable.product_image_test),93.9f))
            offersProducts.add(Product("P6",resources.getDrawable(R.drawable.product_image_test),49.9f))
            offersProducts.add(Product("P7",resources.getDrawable(R.drawable.product_image_test),19.9f))
            offersProducts.add(Product("P8",resources.getDrawable(R.drawable.product_image_test),59.9f))
            offersProducts.add(Product("P9",resources.getDrawable(R.drawable.product_image_test),69.9f))

            homeContent.recyclerViewOffers.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
            homeContent.recyclerViewOffers.setItemAnimator(DefaultItemAnimator())
            homeContent.recyclerViewOffers.setAdapter(productRecyclerViewAdapter)

            productRecyclerViewAdapter.differ.submitList(offersProducts)
        }
    }
}