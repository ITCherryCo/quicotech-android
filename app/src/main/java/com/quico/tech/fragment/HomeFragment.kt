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
        return binding.getRoot()
    }

    fun getCategories(){
        binding.apply {

            categoryRecyclerViewAdapter = CategoryRecyclerViewAdapter()
            val categories = ArrayList<Category>()
            categories.add(Category("Servers",resources.getDrawable(R.drawable.server)))
            categories.add(Category("Computers",resources.getDrawable(R.drawable.server)))
            categories.add(Category("Mobiles",resources.getDrawable(R.drawable.server)))
            categories.add(Category("Servers",resources.getDrawable(R.drawable.server)))
            categories.add(Category("Computers",resources.getDrawable(R.drawable.server)))
            categories.add(Category("Mobiles",resources.getDrawable(R.drawable.server)))
            categories.add(Category("Servers",resources.getDrawable(R.drawable.server)))
            categories.add(Category("Computers",resources.getDrawable(R.drawable.server)))
            categories.add(Category("Mobiles",resources.getDrawable(R.drawable.server)))

            homeContent.recyclerViewCategories.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
            homeContent.recyclerViewCategories.setItemAnimator(DefaultItemAnimator())
            homeContent.recyclerViewCategories.setAdapter(categoryRecyclerViewAdapter)

            categoryRecyclerViewAdapter.differ.submitList(categories)


        }
    }

    fun getHotDeals(){
        binding.apply {

            productRecyclerViewAdapter = ProductRecyclerViewAdapter()
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
}