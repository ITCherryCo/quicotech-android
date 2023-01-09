package com.quico.tech.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import com.quico.tech.R
import com.quico.tech.activity.CartActivity
import com.quico.tech.adapter.ProductRecyclerViewAdapter
import com.quico.tech.data.Constant
import com.quico.tech.data.Constant.NO_ITEMS
import com.quico.tech.databinding.FragmentWishlistBinding
import com.quico.tech.model.Product
import com.quico.tech.utils.Common
import com.quico.tech.utils.Resource
import com.quico.tech.viewmodel.SharedViewModel
import kotlinx.coroutines.launch

class WishlistFragment : Fragment() {
    private var _binding: FragmentWishlistBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SharedViewModel by viewModels()
    private lateinit var productRecyclerViewAdapter: ProductRecyclerViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentWishlistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpText()
        setUpItemsAdapter()
        subscribeWishlist()
        viewModel.user?.let {
            viewModel.viewWishlist(false)
        }
        if (viewModel.user == null)
            setUpErrorForm(Constant.NO_ITEMS)
    }

    private fun setUpText() {
        binding.apply {

            wishlistToolbar.title.text = viewModel.getLangResources().getString(R.string.wishlist)
            wishlistToolbar.heartImage.visibility = View.GONE
            wishlistToolbar.backArrow.visibility = View.GONE

            wishlistToolbar.cartImage.setOnClickListener {
                startActivity(Intent(requireContext(), CartActivity::class.java))
            }
            Common.setSystemBarColor(requireActivity(), R.color.home_background_grey)
        }
    }

    private fun subscribeWishlist() {
        lifecycleScope.launch {
            viewModel.wishlist.collect { response ->
                when (response) {
                    is Resource.Success -> {
                        binding.apply {
                            stopShimmer()
                            recyclerView.visibility = View.VISIBLE
                        }

                        response.data?.let { itemsResponse ->
                            if (itemsResponse.result.isNullOrEmpty()) {
                                 setUpErrorForm(Constant.NO_ITEMS)
                            } else {

                                productRecyclerViewAdapter.differ.submitList(itemsResponse.result)
                            }
                        }
                        Log.d(Constant.PRODUCT_TAG, "SUCCESS")
                    }

                    is Resource.Error -> {
                        response.message?.let { message ->
                            Log.d(Constant.PRODUCT_TAG, "ERROR $message")
                            setUpErrorForm(Constant.ERROR)
                            if (message.equals(getString(R.string.session_expired))) {
                                viewModel.resetSession()
                                Common.setUpSessionProgressDialog(requireContext())
                            }
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

    fun setUpItemsAdapter() {
        binding.apply {
            productRecyclerViewAdapter = ProductRecyclerViewAdapter(false, false, true,viewModel,null)
            val items = ArrayList<Product>()

            recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
            recyclerView.setItemAnimator(DefaultItemAnimator())
            recyclerView.setAdapter(productRecyclerViewAdapter)
            productRecyclerViewAdapter.differ.submitList(items)
        }
    }

    private fun stopShimmer() {
        binding.apply {
            shimmer.visibility = View.GONE
            shimmer.stopShimmer()
        }
    }

    private fun setLoading() {
        binding.apply {
            recyclerView.visibility = View.GONE
            shimmer.visibility = View.VISIBLE
            shimmer.startShimmer()
        }
    }

    private fun onRefresh() {
        setLoading()
        binding.apply {
            if (viewModel.user == null)
                setUpErrorForm(NO_ITEMS)
            else
                viewModel.viewWishlist(false)
        }
    }

    private fun setUpErrorForm(error_type: String) {
        binding.apply {
            recyclerView.visibility = View.GONE
            stopShimmer()
            favoriteErrorContainer.apply {
                root.visibility = View.VISIBLE
                tryAgain.visibility = View.GONE
                errorImage.visibility = View.VISIBLE
                errorImage.setImageResource(android.R.color.transparent)
                errorImage.setImageResource(R.drawable.empty_item)

                errorBtn.text = viewModel.getLangResources().getString(R.string.start_shopping)
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
                        errorImage.setImageResource(R.drawable.empty_item)
                        tryAgain.visibility = View.VISIBLE

                    }

                    Constant.NO_ITEMS -> {
                        errorMsg1.text =
                            viewModel.getLangResources().getString(R.string.no_items_in_list)

                        errorMsg2.text =
                            viewModel.getLangResources().getString(R.string.dont_have_shop_item)
                        errorImage.setImageResource(R.drawable.no_favorite)

                    }

                    Constant.ERROR -> {
                        errorMsg1.text =
                            viewModel.getLangResources().getString(R.string.error)

                        errorMsg2.text =
                            viewModel.getLangResources().getString(R.string.error_msg)
                        tryAgain.visibility = View.VISIBLE

                    }
                }
            }
        }
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            WishlistFragment().apply {
            }
    }
}