package com.quico.tech.fragment

import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.quico.tech.R
import com.quico.tech.adapter.SearchItemRecyclerViewAdapter
import com.quico.tech.adapter.SpecificationRecyclerViewAdapter
import com.quico.tech.data.Constant
import com.quico.tech.data.Constant.FRAGMENT_POSITION
import com.quico.tech.data.Constant.ITEM_ID
import com.quico.tech.databinding.FragmentCompareProductBinding
import com.quico.tech.model.SearchParams
import com.quico.tech.model.Product
import com.quico.tech.model.SearchBodyParameters
import com.quico.tech.model.Specifications
import com.quico.tech.utils.Resource
import com.quico.tech.viewmodel.SharedViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class CompareProductFragment : Fragment() {
    private var product_id: Int = 0
    private var fragment_position: Int = 1

    private var _binding: FragmentCompareProductBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SharedViewModel by viewModels()
    private lateinit var searchItemRecyclerViewAdapter: SearchItemRecyclerViewAdapter
    private var search_text = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            product_id = it.getInt(ITEM_ID)
            fragment_position = it.getInt(FRAGMENT_POSITION)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentCompareProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d(ITEM_ID, product_id.toString())
        setUpText()
        setUpProductsAdapter()
        setUpSearchView()
        subscribeProduct()
        subscribeProducts()
        if (product_id != 0) {
            binding.searchRecyclerView.visibility = View.GONE
            binding.searchView.setQuery("", false)
            viewModel.getProduct(product_id)
        }
    }

    private fun setUpText() {

        binding.apply {
            compareText.text =
                viewModel.getLangResources().getString(com.quico.tech.R.string.compare_with)
            enterProductText.text =
                viewModel.getLangResources().getString(com.quico.tech.R.string.enter_product_name)
            searchView.setQueryHint(
                Html.fromHtml(
                    "<font color = #5F5F5F>" + viewModel.getLangResources().getString(
                        com.quico.tech.R.string.search_hint
                    ) + "</font>"
                )
            );
            //searchView.requestFocus()
            val search_text = searchView.findViewById(searchView.context.resources.getIdentifier("android:id/search_src_text", null, null)
            ) as AutoCompleteTextView

            //searchView.maxWidth=100

            // search_text.setTextColor(Color.WHITE)
            // search_text.setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimensionPixelSize(R.dimen.text_small).toFloat())
            search_text.textSize = 10f
            productContainer.visibility = View.GONE
        }
    }

    private fun setUpSearchView() {
        binding.apply {
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
                android.widget.SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(searchText: String): Boolean {
                    search_text = searchText
                    if (searchText.isEmpty()) {
                        searchRecyclerView.visibility = View.GONE
                    } else if (searchText.trim { it <= ' ' }.length > 2) {
                        search_text = searchText.trim()

                        lifecycleScope.launch {
                            delay(100)
                            viewModel.searchCompareProducts(
                                SearchBodyParameters(
                                    SearchParams(
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


    private fun subscribeProducts() {
        lifecycleScope.launch {
            viewModel.search_compare_products.collect { response ->
                when (response) {
                    is Resource.Success -> {
                        response.data?.let { itemsResponse ->
                            if (itemsResponse.result.isNullOrEmpty()) {
                                // setUpErrorForm(Constant.NO_ITEMS)
                            } else {
                                searchItemRecyclerViewAdapter.differ.submitList(itemsResponse.result)
                                binding.searchRecyclerView.visibility = View.VISIBLE
                            }
                        }
                        Log.d(Constant.PRODUCT_TAG, "SUCCESS")
                    }

                    is Resource.Error -> {
                        binding.searchRecyclerView.visibility = View.GONE
                        response.message?.let { message ->
                            Log.d(Constant.PRODUCT_TAG, "ERROR $message")
                        }
                    }

                    is Resource.Connection -> {
                        binding.searchRecyclerView.visibility = View.GONE
                        Log.d(Constant.PRODUCT_TAG, "ERROR CONNECTION")
                    }

                    is Resource.Loading -> {
                        binding.searchRecyclerView.visibility = View.GONE
                        Log.d(Constant.PRODUCT_TAG, "LOADING")
                    }
                }
            }
        }
    }


    fun subscribeProduct() {
        lifecycleScope.launch {
            viewModel.product.collect { response ->
                when (response) {
                    is Resource.Success -> {
                        response.data?.let { servicesResponse ->
                            binding.apply {
                                productContainer.visibility = View.VISIBLE
                                compareErrorContainer.root.visibility = View.GONE
                                progressBar.visibility = View.GONE
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
            if (!product.images.isNullOrEmpty()) {
                Glide
                    .with(this@CompareProductFragment)
                    .load(product.images[0])
                    .fitCenter()
                    .error(R.drawable.empty_item)
                    .into(imageView)
            }
            else
                imageView.setImageResource(R.drawable.empty_item)

            name.text = product.name

            if (product.is_vip || product.is_on_sale) {

                price.text = "$ ${product.new_price.toString()}"
            } else {
                price.text = "$ ${product.regular_price.toString()}"
            }

            if (!product.specifications.isNullOrEmpty()) {
                specificationRecyclerView.visibility = View.VISIBLE
                 setUpSpecificationsAdapter(product.specifications)

            } else {
                specificationRecyclerView.visibility = View.GONE

            }
        }
    }

    private fun setUpSpecificationsAdapter(specifications: ArrayList<Specifications>) {
        binding.apply {
            var specificationRecyclerViewAdapter = SpecificationRecyclerViewAdapter()

            specificationRecyclerView.layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false
            )
            specificationRecyclerView.setItemAnimator(DefaultItemAnimator())
            specificationRecyclerView.setAdapter(specificationRecyclerViewAdapter)
            specificationRecyclerViewAdapter.differ.submitList(specifications)

        }
    }

    private fun setUpProductsAdapter() {
        binding.apply {

            searchItemRecyclerViewAdapter =
                SearchItemRecyclerViewAdapter(fragment_position, viewLifecycleOwner)
            var products = ArrayList<Product>()


            searchRecyclerView.layoutManager =
                LinearLayoutManager(
                    activity,
                    LinearLayoutManager.VERTICAL,
                    false
                )
            searchRecyclerView.setItemAnimator(DefaultItemAnimator())
            searchRecyclerView.setAdapter(searchItemRecyclerViewAdapter)

            searchItemRecyclerViewAdapter.differ.submitList(products)
        }
    }

    fun setLoading() {
        binding.apply {
            productContainer.visibility = View.GONE
            compareErrorContainer.root.visibility = View.GONE
            progressBar.visibility = View.VISIBLE
        }
    }

    fun onRefresh() {
        binding.apply {
            setLoading()
            if (product_id != 0)
                viewModel.getProduct(product_id)
        }
    }

    fun setUpErrorForm(error_type: String) {
        binding.apply {
            progressBar.visibility = View.GONE
            productContainer.visibility = View.GONE

            compareErrorContainer.apply {
                errorMsg1.visibility = View.GONE
                root.visibility = View.VISIBLE
                errorBtn.visibility = View.GONE
                tryAgain.visibility = View.VISIBLE
                errorImage.setImageResource(android.R.color.transparent)
                errorImage.setImageResource(R.drawable.empty_item)
                tryAgain.text = viewModel.getLangResources().getString(R.string.try_again)
                tryAgain.setOnClickListener {
                    onRefresh()
                }

                when (error_type) {
                    Constant.CONNECTION -> {
                        errorMsg2.text =
                            viewModel.getLangResources().getString(R.string.check_connection)
                    }
                    Constant.NO_ITEM -> {
                        errorMsg2.text =
                            viewModel.getLangResources().getString(R.string.no_item)
                        errorImage.setImageResource(R.drawable.empty_item)
                    }

                    Constant.ERROR -> {
                        errorMsg2.text =
                            viewModel.getLangResources().getString(R.string.error_msg)
                    }
                }
            }
        }
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CompareProductFragment().apply {

            }
    }
}