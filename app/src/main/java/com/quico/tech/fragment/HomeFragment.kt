package com.quico.tech.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.quico.tech.R
import com.quico.tech.activity.*
import com.quico.tech.adapter.*
import com.quico.tech.data.Constant
import com.quico.tech.databinding.FragmentHomeBinding
import com.quico.tech.model.Brand
import com.quico.tech.model.Category
import com.quico.tech.model.Product
import com.quico.tech.utils.Common
import com.quico.tech.utils.Resource
import com.quico.tech.viewmodel.SharedViewModel
import kotlinx.coroutines.launch


class HomeFragment : Fragment() {
    private lateinit var binding : FragmentHomeBinding
    private lateinit var categoryRecyclerViewAdapter: CategoryRecyclerViewAdapter
    private lateinit var hotDealsRecyclerViewAdapter: HotDealsRecyclerViewAdapter
    private lateinit var bestSellingRecyclerViewAdapter: BestSellingRecyclerViewAdapter
    private lateinit var offersRecyclerViewAdapter: OffersProductsRecyclerViewAdapter
    private lateinit var brandHomeRecyclerViewAdapter: BrandHomeRecyclerViewAdapter
    private val viewModel: SharedViewModel by viewModels()
    var TAG = "HOME_RESPONSE"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        init()
        onRefresh()
        setUpHomeAdapter()
        subscribeHomeList()
        viewModel.getHomeData()
        return binding.getRoot()
    }

    fun init(){
        Constant.TEMPORAR_USER = null
        initToolbar();
        binding.apply {
            viewAllCategoriesLabel.setOnClickListener {
                startActivity(Intent(context, CategoryAllActivity::class.java))
            }
            viewAllBrandsLabel.setOnClickListener {
                startActivity(Intent(context, BrandAllActivity::class.java))
            }

        }
    }

    fun initToolbar(){
        binding.apply {
            toolbarHomeInclude.toolbarHome.title = getString(R.string.app_name)
            //Common.changeOverflowMenuIconColor(homeContent.toolbarInclude.toolbarHome, resources.getColor(R.color.color_primary_purple))
            Common.setSystemBarColor(context as Activity, R.color.white)
            Common.setSystemBarLight(context as Activity)
            toolbarHomeInclude.bagIcon.setOnClickListener {
                startActivity(Intent(context, CartActivity::class.java))
            }

            toolbarHomeInclude.vipStarIcon.setOnClickListener {
                startActivity(
                    Intent(activity, GeneralTermsActivity::class.java).putExtra(
                        Constant.ACTIVITY_TYPE,
                        Constant.VIP_BENEFITS
                    )
                )
            }

            (activity as HomeActivity?)?.setSupportActionBar(toolbarHomeInclude.toolbarHome)
            setHasOptionsMenu(true)
        }
    }


    fun setUpHomeAdapter(){
        lifecycleScope.launch {
            viewModel.homeData.collect { response ->
                when (response) {
                    is Resource.Success -> {
                        binding.apply {
                            swipeRefreshLayout.setRefreshing(false)
                            recyclerViewCategories.visibility = View.VISIBLE
                            recyclerViewBrands.visibility = View.VISIBLE
                            recyclerViewBestSelling.visibility = View.VISIBLE
                            recyclerViewHotDeals.visibility = View.VISIBLE
                            recyclerViewOffers.visibility = View.VISIBLE
                            lnyCategoryHeader.visibility = View.VISIBLE
                            lnyBrandHeader.visibility = View.VISIBLE
                            lnyHotDealsheader.visibility = View.VISIBLE
                            lnyBestSellingsheader.visibility = View.VISIBLE
                            lnyOfferssheader.visibility = View.VISIBLE
                            lnyCategoryCard.visibility = View.VISIBLE
                        }
                        stopShimmer()
                        response.data?.let { homeDataResponse ->
                            if (homeDataResponse.result == null)
                                setUpErrorForm(Constant.NO_HOME_DATA)
                            else {
                                categoryRecyclerViewAdapter.differ.submitList(homeDataResponse.result.categories!!)
                                brandHomeRecyclerViewAdapter.differ.submitList(homeDataResponse.result.brands!!)
                                hotDealsRecyclerViewAdapter.differ.submitList(homeDataResponse.result.hot_deals!!)
                                bestSellingRecyclerViewAdapter.differ.submitList(homeDataResponse.result.best_selling!!)
                                offersRecyclerViewAdapter.differ.submitList(homeDataResponse.result.offers!!)
                                binding.recyclerViewCategories.setVisibility(View.VISIBLE)
                                binding.recyclerViewBrands.setVisibility(View.VISIBLE)
                                binding.recyclerViewOffers.setVisibility(View.VISIBLE)
                                binding.recyclerViewHotDeals.setVisibility(View.VISIBLE)
                                binding.recyclerViewBestSelling.setVisibility(View.VISIBLE)
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

    private fun stopShimmer() {
        binding.apply {
            shimmer.visibility = View.GONE
            shimmer.stopShimmer()
        }
    }

    fun setLoading() {
        binding.apply {
            recyclerViewCategories.visibility = View.GONE
            recyclerViewBrands.visibility = View.GONE
            recyclerViewBestSelling.visibility = View.GONE
            recyclerViewHotDeals.visibility = View.GONE
            recyclerViewOffers.visibility = View.GONE
            lnyCategoryHeader.visibility = View.GONE
            lnyBrandHeader.visibility = View.GONE
            lnyHotDealsheader.visibility = View.GONE
            lnyBestSellingsheader.visibility = View.GONE
            lnyOfferssheader.visibility = View.GONE
            lnyCategoryCard.visibility = View.GONE
            homeErrorContainer.root.visibility = View.GONE
            shimmer.visibility = View.VISIBLE
            shimmer.startShimmer()
            swipeRefreshLayout.setRefreshing(false)
        }
    }


    fun onRefresh() {
        binding.apply {
            swipeRefreshLayout.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
                setLoading() // later we will remove it because the observable will call it
                viewModel.getHomeData() // get User id
            })
        }
    }

    fun subscribeHomeList(){
        setUpCategoriesAdapter()
        setUpBrandsAdapter()
        setUpHotDealsAdapter()
        setBestSellingProductsAdapter()
        setOffersProductsAdapter()
    }

    fun setUpCategoriesAdapter(){
        binding.apply {
            categoryRecyclerViewAdapter = CategoryRecyclerViewAdapter(false)
            var categories = ArrayList<Category>()

            recyclerViewCategories.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
            recyclerViewCategories.setItemAnimator(DefaultItemAnimator())
            recyclerViewCategories.setAdapter(categoryRecyclerViewAdapter)

            categoryRecyclerViewAdapter.differ.submitList(categories)
        }
    }

    fun setUpBrandsAdapter(){
        binding.apply {
            brandHomeRecyclerViewAdapter = BrandHomeRecyclerViewAdapter()
            val brands = ArrayList<Brand>()

            recyclerViewBrands.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
            recyclerViewBrands.setItemAnimator(DefaultItemAnimator())
            recyclerViewBrands.setAdapter(brandHomeRecyclerViewAdapter)
            brandHomeRecyclerViewAdapter.differ.submitList(brands)
        }
    }

    fun setUpHotDealsAdapter(){
        binding.apply {
            hotDealsRecyclerViewAdapter = HotDealsRecyclerViewAdapter()
            val hotDealsProducts = ArrayList<Product>()

            recyclerViewHotDeals.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
            recyclerViewHotDeals.setItemAnimator(DefaultItemAnimator())
            recyclerViewHotDeals.setAdapter(hotDealsRecyclerViewAdapter)

            hotDealsRecyclerViewAdapter.differ.submitList(hotDealsProducts)
        }
    }

    fun setBestSellingProductsAdapter(){
        binding.apply {
            bestSellingRecyclerViewAdapter = BestSellingRecyclerViewAdapter()
            val bestSellingProducts = ArrayList<Product>()

            recyclerViewBestSelling.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
            recyclerViewBestSelling.setItemAnimator(DefaultItemAnimator())
            recyclerViewBestSelling.setAdapter(bestSellingRecyclerViewAdapter)

            bestSellingRecyclerViewAdapter.differ.submitList(bestSellingProducts)
        }
    }

    fun setOffersProductsAdapter(){
        binding.apply {
            offersRecyclerViewAdapter = OffersProductsRecyclerViewAdapter()
            val offersProducts = ArrayList<Product>()

            recyclerViewOffers.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
            recyclerViewOffers.setItemAnimator(DefaultItemAnimator())
            recyclerViewOffers.setAdapter(offersRecyclerViewAdapter)

            offersRecyclerViewAdapter.differ.submitList(offersProducts)
        }
    }

    fun setUpErrorForm(error_type: String) {
        binding.apply {
            swipeRefreshLayout.setRefreshing(false)
            recyclerViewCategories.visibility = View.GONE
            recyclerViewBrands.visibility = View.GONE
            recyclerViewBestSelling.visibility = View.GONE
            recyclerViewHotDeals.visibility = View.GONE
            recyclerViewOffers.visibility = View.GONE
            lnyCategoryHeader.visibility = View.GONE
            lnyBrandHeader.visibility = View.GONE
            lnyHotDealsheader.visibility = View.GONE
            lnyBestSellingsheader.visibility = View.GONE
            lnyOfferssheader.visibility = View.GONE
            lnyCategoryCard.visibility = View.GONE

            stopShimmer()
            swipeRefreshLayout.setEnabled(true)
            homeErrorContainer.apply {
                errorMsg1.visibility = View.GONE
                root.visibility = View.VISIBLE
                errorBtn.visibility = View.GONE
                tryAgain.visibility = View.VISIBLE
                errorImage.visibility = View.VISIBLE
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
                        errorMsg2.setText(
                            viewModel.getLangResources().getString(R.string.check_connection)
                        )
                    }
                    Constant.NO_HOME_DATA -> {
                        errorMsg2.text =
                            viewModel.getLangResources().getString(R.string.no_home_data)
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