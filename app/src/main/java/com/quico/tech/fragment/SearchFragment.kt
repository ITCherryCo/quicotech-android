package com.quico.tech.fragment

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Html
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.quico.tech.R
import com.quico.tech.adapter.BrandRecyclerViewAdapter
import com.quico.tech.adapter.ColorRecyclerViewAdapter
import com.quico.tech.adapter.ProductRecyclerViewAdapter
import com.quico.tech.data.Constant
import com.quico.tech.data.Constant.EMPTY_SEARCH
import com.quico.tech.data.Constant.PRODUCT_TAG
import com.quico.tech.databinding.FilterBottomSheetBinding
import com.quico.tech.databinding.FragmentSearchBinding
import com.quico.tech.model.*
import com.quico.tech.utils.Common
import com.quico.tech.utils.Resource
import com.quico.tech.viewmodel.SharedViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SharedViewModel by viewModels()
    private lateinit var productRecyclerViewAdapter: ProductRecyclerViewAdapter
    private var brands = ArrayList<Brand>()
    private var memory_sizes = ArrayList<Brand>()
    private var conditions = ArrayList<Brand>()
    private var colors = ArrayList<com.quico.tech.model.Color>()
    private var categories = ArrayList<Category>()
    private var current_page = 1
    private var total_pages = 1
    private var search_text = ""
    private var products = ArrayList<Product>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpProductsAdapter()
        setUpText()
        setUpSearchView()
        setUpProductsAdapter()
        subscribeProducts()
    }

    private fun setUpText() {
        binding.apply {

            searchToolbar.title.text = viewModel.getLangResources().getString(R.string.filters)
            //searchView.queryHint = viewModel.getLangResources().getString(R.string.search_hint)
            searchView.setQueryHint(Html.fromHtml("<font color = #5F5F5F>" + viewModel.getLangResources().getString(R.string.search_hint) + "</font>"));

            searchView.requestFocus()
            //filterText.text = viewModel.getLangResources().getString(R.string.filter)
            searchToolbar.heartImage.visibility = View.GONE
            filterImage.setOnClickListener {
                setUpFilerBottomSheet()
            }

            searchToolbar.backArrow.visibility=View.GONE
            Common.setSystemBarColor(requireActivity(), R.color.home_background_grey)

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
                        setUpErrorForm(EMPTY_SEARCH)
                    }
                    else if(searchText.trim { it<=' ' }.length>2) {
                        productRecyclerView.visibility = View.VISIBLE
                        viewModel.searchProducts(SearchBodyParameters(NameParams(search_text)))
                    }
                    return false
                }
            })
        }
    }


    private fun subscribeProducts() {
        lifecycleScope.launch {
            viewModel.search_products.collect { response ->
                when (response) {
                    is Resource.Success -> {
                        binding.apply {
                            stopProductShimmer()
                            productRecyclerView.visibility = View.VISIBLE
                        }

                        response.data?.let { itemsResponse ->
                            if (itemsResponse.result.isNullOrEmpty()) {
                               // setUpErrorForm(Constant.NO_ITEMS)
                            } else {
                                products.clear()
                                products.addAll(itemsResponse.result)
                                productRecyclerViewAdapter.differ.submitList(products)
                            }
                        }
                        Log.d(PRODUCT_TAG, "SUCCESS")
                    }

                    is Resource.Error -> {
                        response.message?.let { message ->
                            Log.d(PRODUCT_TAG, "ERROR $message")
                            setUpErrorForm(Constant.ERROR)
                        }
                    }

                    is Resource.Connection -> {
                        Log.d(PRODUCT_TAG, "ERROR CONNECTION")
                        setUpErrorForm(Constant.CONNECTION)
                    }

                    is Resource.Loading -> {
                        setProductsLoading()
                        Log.d(PRODUCT_TAG, "LOADING")
                    }
                }
            }
        }
    }

    fun setUpProductsAdapter() {
        binding.apply {
            productRecyclerViewAdapter = ProductRecyclerViewAdapter(false,false,
                false,viewModel,null)
            //stopProductShimmer()
            // filterImage.setEnabled(true)

         /*   items.add(Product("P1", resources.getDrawable(R.drawable.product_image_test), 9.9))
            items.add(Product("P2", resources.getDrawable(R.drawable.product_image_test), 22.9))
            items.add(Product("P3", resources.getDrawable(R.drawable.product_image_test), 43.9))
            items.add(Product("P4", resources.getDrawable(R.drawable.product_image_test), 45.22))
            items.add(Product("P5", resources.getDrawable(R.drawable.product_image_test), 93.9))
            items.add(Product("P6", resources.getDrawable(R.drawable.product_image_test), 49.9))
            items.add(Product("P7", resources.getDrawable(R.drawable.product_image_test), 19.9))
            items.add(Product("P8", resources.getDrawable(R.drawable.product_image_test), 59.9))
            items.add(Product("P9", resources.getDrawable(R.drawable.product_image_test), 69.9))
*/
            productRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
            productRecyclerView.setItemAnimator(DefaultItemAnimator())
            productRecyclerView.setAdapter(productRecyclerViewAdapter)
            productRecyclerViewAdapter.differ.submitList(products)
        }
    }

    private fun stopCategoryShimmer() {
        binding.apply {
            categoryShimmer.visibility = View.GONE
            categoryShimmer.stopShimmer()
        }
    }


    private fun stopProductShimmer() {
        binding.apply {
            productShimmer.visibility = View.GONE
            lifecycleScope.launch {
                delay(1000)
                productShimmer.stopShimmer()
            }
        }
    }


  /*  fun setCategoriesLoading() {
        binding.apply {
            categoryContainer.visibility = View.GONE
            productRecyclerView.visibility = View.GONE
            progressBar.visibility = View.GONE
            categoryShimmer.visibility = View.VISIBLE
           // categoryShimmer.startShimmer()
            // filterImage.setEnabled(false)

            lifecycleScope.launch {
                delay(2000)
                setUpCategoryAdapter()
                setProductsLoading()
            }
        }
    }*/

    fun setProductsLoading() {
        binding.apply {
            itemsErrorContainer.root.visibility = View.GONE
            productRecyclerView.visibility = View.GONE
            progressBar.visibility = View.GONE
            productShimmer.visibility = View.VISIBLE
            productShimmer.startShimmer()
            // filterImage.setEnabled(false)

            /*lifecycleScope.launch {
                delay(1500)
                setUpItemsAdapter()
                setUpCategoryAdapter()
            }*/
        }
    }

//    fun onRefresh() {
//        setLoading() // later we will remove it because the observable will call it
//        // viewModel.search(1) // get User id
//    }

  /*  private fun setUpCategoryAdapter() {
        binding.apply {
            stopCategoryShimmer()
            categoriesText.text = viewModel.getLangResources().getString(R.string.categories)

            categoryContainer.visibility = View.VISIBLE
//            categories.add(Category("Servers", resources.getDrawable(R.drawable.server)))
//            categories.add(Category("Computers", resources.getDrawable(R.drawable.computer)))
//            categories.add(Category("Mobiles", resources.getDrawable(R.drawable.aurdino)))
//            categories.add(Category("Servers", resources.getDrawable(R.drawable.games)))
//            categories.add(Category("Computers", resources.getDrawable(R.drawable.computer)))
//            categories.add(Category("Mobiles", resources.getDrawable(R.drawable.aurdino)))
//            categories.add(Category("Servers", resources.getDrawable(R.drawable.server)))
//            categories.add(Category("Computers", resources.getDrawable(R.drawable.computer)))
//            categories.add(Category("Mobiles", resources.getDrawable(R.drawable.server)))

            if (categories.isEmpty()) {
                categoryContainer.visibility = View.GONE
            } else {
                categoryContainer.visibility = View.VISIBLE
                categoriesText.text = viewModel.getLangResources().getString(R.string.categories)

                var categoryRecyclerViewAdapter = CategoryRecyclerViewAdapter(true)
                binding.apply {

                    categoryRecyclerView.layoutManager = LinearLayoutManager(
                        requireContext(),
                        LinearLayoutManager.HORIZONTAL,
                        false
                    )
                    categoryRecyclerView.setItemAnimator(DefaultItemAnimator())
                    categoryRecyclerView.setAdapter(categoryRecyclerViewAdapter)
                }
                categoryRecyclerViewAdapter.differ.submitList(categories)
            }
        }
    }*/


    private fun setUpFilerBottomSheet() {

        var bottomSheetDialog = BottomSheetDialog(requireContext())
        val filter_binding: FilterBottomSheetBinding =
            FilterBottomSheetBinding.inflate(LayoutInflater.from(context))
        bottomSheetDialog.setContentView(filter_binding.root)
        bottomSheetDialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        bottomSheetDialog.show()
        filter_binding.apply {
            priceText.text = viewModel.getLangResources().getString(R.string.price)
            filterBtn.text = viewModel.getLangResources().getString(R.string.filter)

         /*   brands.add(Brand(1, "Brand Name",null))
            brands.add(Brand(1, "Brand Name",null))
            brands.add(Brand(1, "Brand Name",null))
            brands.add(Brand(1, "Brand Name",null))
            brands.add(Brand(1, "Brand Name",null))
            brands.add(Brand(1, "Brand Name",null))
            brands.add(Brand(1, "Brand Name",null))
            brands.add(Brand(1, "Brand Name",null))*/

            if (brands.isEmpty()) {
                brandContainer.visibility = View.GONE
            } else {
                brandContainer.visibility = View.VISIBLE
                brandsText.text = viewModel.getLangResources().getString(R.string.brands)

                var brandSelectionRecyclerViewAdapter = BrandRecyclerViewAdapter()
                binding.apply {
                    val layoutManager =
                        GridLayoutManager(
                            requireContext(),
                            2,
                            LinearLayoutManager.HORIZONTAL,
                            false
                        )

                    layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                        override fun getSpanSize(position: Int): Int {
                            return if (position == -1) 2 else 1
                        }
                    }
                    brandRecyclerView.layoutManager = layoutManager
                    brandRecyclerView.setItemAnimator(DefaultItemAnimator())
                    brandRecyclerView.setAdapter(brandSelectionRecyclerViewAdapter)
                }
                brandSelectionRecyclerViewAdapter.differ.submitList(brands)
            }


            colors.add(com.quico.tech.model.Color(1, "#9A3622"))
            colors.add(com.quico.tech.model.Color(2, "#FBB73E"))
            colors.add(com.quico.tech.model.Color(3, "#707070"))
            colors.add(com.quico.tech.model.Color(4, "#4B40D7"))
            colors.add(com.quico.tech.model.Color(5, "#707070"))
            colors.add(com.quico.tech.model.Color(6, "#C640D7"))
            colors.add(com.quico.tech.model.Color(7, "#40D7C6"))
            colors.add(com.quico.tech.model.Color(8, "#2C905E"))

            if (colors.isEmpty()) {
                colorContainer.visibility = View.GONE
            } else {
                colorContainer.visibility = View.VISIBLE
                colorsText.text = viewModel.getLangResources().getString(R.string.colors)

                var colorRecyclerViewAdapter = ColorRecyclerViewAdapter()
                binding.apply {
                    colorRecyclerView.layoutManager = LinearLayoutManager(
                        requireContext(),
                        LinearLayoutManager.HORIZONTAL,
                        false
                    )
                    colorRecyclerView.setItemAnimator(DefaultItemAnimator())
                    colorRecyclerView.setAdapter(colorRecyclerViewAdapter)
                }
                colorRecyclerViewAdapter.differ.submitList(colors)
            }


           /* memory_sizes.add(Brand(1, "125 GB",null))
            memory_sizes.add(Brand(1, "125 GB",null))
            memory_sizes.add(Brand(1, "125 GB",null))
            memory_sizes.add(Brand(1, "125 GB",null))
            memory_sizes.add(Brand(1, "125 GB",null))
            memory_sizes.add(Brand(1, "125 GB",null))*/

            if (memory_sizes.isEmpty()) {
                sizeContainer.visibility = View.GONE
            } else {
                sizeContainer.visibility = View.VISIBLE
                sizeText.text = viewModel.getLangResources().getString(R.string.memory_size)

                var brandSelectionRecyclerViewAdapter = BrandRecyclerViewAdapter()
                binding.apply {

                    sizeRecyclerViewAdapter.layoutManager = LinearLayoutManager(
                        requireContext(),
                        LinearLayoutManager.HORIZONTAL,
                        false
                    )
                    sizeRecyclerViewAdapter.setItemAnimator(DefaultItemAnimator())
                    sizeRecyclerViewAdapter.setAdapter(brandSelectionRecyclerViewAdapter)
                }
                brandSelectionRecyclerViewAdapter.differ.submitList(memory_sizes)
            }

          /*  conditions.add(Brand(1, "new",null))
            conditions.add(Brand(1, "used",null))*/

            if (memory_sizes.isEmpty()) {
                conditionsContainer.visibility = View.GONE
            } else {
                conditionsContainer.visibility = View.VISIBLE
                conditionsText.text = viewModel.getLangResources().getString(R.string.conditions)

                var brandSelectionRecyclerViewAdapter = BrandRecyclerViewAdapter()
                binding.apply {

                    conditionRecyclerViewAdapter.layoutManager = LinearLayoutManager(
                        requireContext(),
                        LinearLayoutManager.HORIZONTAL,
                        false
                    )
                    conditionRecyclerViewAdapter.setItemAnimator(DefaultItemAnimator())
                    conditionRecyclerViewAdapter.setAdapter(brandSelectionRecyclerViewAdapter)
                }
                brandSelectionRecyclerViewAdapter.differ.submitList(conditions)
            }
        }
    }

    private fun onRefresh(){
        setProductsLoading()
        binding.apply {
            viewModel.searchProducts(SearchBodyParameters(NameParams( search_text)))
        }
    }

    fun setUpErrorForm(error_type: String) {
        binding.apply {
            stopProductShimmer()
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


    companion object {

        fun newInstance(param1: String, param2: String) =
            SearchFragment().apply {

            }
    }
}