package com.quico.tech.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.quico.tech.R
import com.quico.tech.activity.*
import com.quico.tech.adapter.BrandHomeRecyclerViewAdapter
import com.quico.tech.adapter.CategoryRecyclerViewAdapter
import com.quico.tech.adapter.ProductRecyclerViewAdapter
import com.quico.tech.data.Constant
import com.quico.tech.databinding.FragmentHomeBinding
import com.quico.tech.model.Brand
import com.quico.tech.model.Category
import com.quico.tech.model.Product
import com.quico.tech.utils.Common


class HomeFragment : Fragment() {
    private lateinit var binding : FragmentHomeBinding
    private lateinit var categoryRecyclerViewAdapter: CategoryRecyclerViewAdapter
    private lateinit var productRecyclerViewAdapter: ProductRecyclerViewAdapter
    private lateinit var brandHomeRecyclerViewAdapter: BrandHomeRecyclerViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        init()
        getCategories();
        getBrands()
        getHotDeals();
        getBestSellingProducts();
        getOffersProducts();
        return binding.getRoot()
    }

    fun init(){
        initToolbar();
        binding.apply {
            homeContent.viewAllCategoriesLabel.setOnClickListener {
                startActivity(Intent(context, CategoryAllActivity::class.java))
            }
            homeContent.viewAllBrandsLabel.setOnClickListener {
                startActivity(Intent(context, BrandAllActivity::class.java))
            }

        }
    }

    fun initToolbar(){
        binding.apply {
            homeContent.toolbarInclude.toolbarHome.title = getString(R.string.app_name)
            //Common.changeOverflowMenuIconColor(homeContent.toolbarInclude.toolbarHome, resources.getColor(R.color.color_primary_purple))
            Common.setSystemBarColor(context as Activity, R.color.white)
            Common.setSystemBarLight(context as Activity)
            homeContent.toolbarInclude.bagIcon.setOnClickListener {
                startActivity(Intent(context, CartActivity::class.java))
            }

            homeContent.toolbarInclude.vipStarIcon.setOnClickListener {
                startActivity(
                    Intent(activity, GeneralTermsActivity::class.java).putExtra(
                        Constant.ACTIVITY_TYPE,
                        Constant.VIP_BENEFITS
                    )
                )
            }

            (activity as HomeActivity?)?.setSupportActionBar(homeContent.toolbarInclude.toolbarHome)
            setHasOptionsMenu(true)
        }
    }

    fun getCategories(){
        binding.apply {

            categoryRecyclerViewAdapter = CategoryRecyclerViewAdapter(false)
            val categories = ArrayList<Category>()
//            categories.add(Category("Servers",resources.getDrawable(R.drawable.server)))
//            categories.add(Category("Computers",resources.getDrawable(R.drawable.computer)))
//            categories.add(Category("Mobiles",resources.getDrawable(R.drawable.aurdino)))
//            categories.add(Category("Servers",resources.getDrawable(R.drawable.games)))
//            categories.add(Category("Computers",resources.getDrawable(R.drawable.computer)))
//            categories.add(Category("Mobiles",resources.getDrawable(R.drawable.aurdino)))
//            categories.add(Category("Servers",resources.getDrawable(R.drawable.server)))
//            categories.add(Category("Computers",resources.getDrawable(R.drawable.computer)))
//            categories.add(Category("Mobiles",resources.getDrawable(R.drawable.server)))

            homeContent.recyclerViewCategories.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
            homeContent.recyclerViewCategories.setItemAnimator(DefaultItemAnimator())
            homeContent.recyclerViewCategories.setAdapter(categoryRecyclerViewAdapter)

            categoryRecyclerViewAdapter.differ.submitList(categories)

        }
    }

    fun getBrands(){
        binding.apply {

            brandHomeRecyclerViewAdapter = BrandHomeRecyclerViewAdapter()
            val brands = ArrayList<Brand>()
          /*  brands.add(Brand(1,"Canon",R.drawable.brand_canon))
            brands.add(Brand(2,"Canon",R.drawable.brand_canon))
            brands.add(Brand(3,"Canon",R.drawable.brand_canon))
            brands.add(Brand(4,"Canon",R.drawable.brand_canon))
            brands.add(Brand(5,"Canon",R.drawable.brand_canon))
            brands.add(Brand(6,"Canon",R.drawable.brand_canon))
            brands.add(Brand(7,"Canon",R.drawable.brand_canon))
            brands.add(Brand(8,"Canon",R.drawable.brand_canon))*/


            homeContent.recyclerViewBrands.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
            homeContent.recyclerViewBrands.setItemAnimator(DefaultItemAnimator())
            homeContent.recyclerViewBrands.setAdapter(brandHomeRecyclerViewAdapter)

            brandHomeRecyclerViewAdapter.differ.submitList(brands)

        }
    }

    fun getHotDeals(){
        binding.apply {
            productRecyclerViewAdapter = ProductRecyclerViewAdapter(true,false,null)
            val hotDealsProducts = ArrayList<Product>()
            hotDealsProducts.add(Product("P1",  9.9))
            hotDealsProducts.add(Product("P2", 22.9))
            hotDealsProducts.add(Product("P3", 43.9))
            hotDealsProducts.add(Product("P4", 45.22))
            hotDealsProducts.add(Product("P5", 93.9))
            hotDealsProducts.add(Product("P6",  49.9))
            hotDealsProducts.add(Product("P7",  19.9))
            hotDealsProducts.add(Product("P8",  59.9))
            hotDealsProducts.add(Product("P9",  69.9))

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
            bestSellingProducts.add(Product("P1",  9.9))
            bestSellingProducts.add(Product("P2", 22.9))
            bestSellingProducts.add(Product("P3", 43.9))
            bestSellingProducts.add(Product("P4", 45.22))
            bestSellingProducts.add(Product("P5", 93.9))
            bestSellingProducts.add(Product("P6",  49.9))
            bestSellingProducts.add(Product("P7",  19.9))
            bestSellingProducts.add(Product("P8",  59.9))
            bestSellingProducts.add(Product("P9",  69.9))

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
            offersProducts.add(Product("P1",  9.9))
            offersProducts.add(Product("P2", 22.9))
            offersProducts.add(Product("P3", 43.9))
            offersProducts.add(Product("P4", 45.22))
            offersProducts.add(Product("P5", 93.9))
            offersProducts.add(Product("P6",  49.9))
            offersProducts.add(Product("P7",  19.9))
            offersProducts.add(Product("P8",  59.9))
            offersProducts.add(Product("P9",  69.9))

            homeContent.recyclerViewOffers.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
            homeContent.recyclerViewOffers.setItemAnimator(DefaultItemAnimator())
            homeContent.recyclerViewOffers.setAdapter(productRecyclerViewAdapter)

            productRecyclerViewAdapter.differ.submitList(offersProducts)
        }
    }


}