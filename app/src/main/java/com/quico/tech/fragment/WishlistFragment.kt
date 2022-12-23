package com.quico.tech.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import com.quico.tech.R
import com.quico.tech.activity.CompareSearchActivity
import com.quico.tech.adapter.ProductRecyclerViewAdapter
import com.quico.tech.data.Constant
import com.quico.tech.databinding.FragmentSearchBinding
import com.quico.tech.databinding.FragmentWishlistBinding
import com.quico.tech.model.Product
import com.quico.tech.viewmodel.SharedViewModel
import kotlinx.coroutines.delay
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
        setLoading()
    }

    private fun setUpText() {
        binding.apply {

            toolbar.title.text = viewModel.getLangResources().getString(R.string.wishlist)
            toolbar.heartImage.visibility = View.GONE
            toolbar.backArrow.visibility = View.GONE

            toolbar.cartImage.setOnClickListener {
                startActivity(Intent(requireContext(), CompareSearchActivity::class.java))
            }
        }
    }

    fun setUpItemsAdapter() {
        binding.apply {
            productRecyclerViewAdapter = ProductRecyclerViewAdapter(false,false,null)
            val items = ArrayList<Product>()
            stopShimmer()
            recyclerView.visibility = View.VISIBLE
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
    fun setLoading() {
        binding.apply {
            recyclerView.visibility = View.GONE
            shimmer.visibility = View.VISIBLE
            shimmer.startShimmer()
            // filterImage.setEnabled(false)

            lifecycleScope.launch {
                delay(2000)
                setUpItemsAdapter()
                setUpItemsAdapter()
            }
        }
    }

    fun setUpErrorForm(error_type: String) {
        binding.apply {
            recyclerView.visibility = View.GONE
            errorContainer.errorContainer.visibility = View.VISIBLE
            stopShimmer()
            errorContainer.tryAgain.visibility = View.VISIBLE
            errorContainer.errorImage.visibility = View.VISIBLE
            errorContainer.errorImage.setImageResource(android.R.color.transparent)


            when (error_type) {
                Constant.CONNECTION -> {
                    errorContainer.errorMsg1.text =
                        viewModel.getLangResources().getString(R.string.connection)

                    errorContainer.errorMsg2.text =
                        viewModel.getLangResources().getString(R.string.check_connection)

                }
                Constant.NO_ITEMS -> {
                    errorContainer.errorMsg1.text =
                        viewModel.getLangResources().getString(R.string.no_search_result)

                    errorContainer.errorMsg2.text =
                        viewModel.getLangResources().getString(R.string.no_search_result_msg)
                    errorContainer.errorImage.setImageResource(R.drawable.no_favorite)

                }

                Constant.ERROR -> {
                    errorContainer. errorMsg1.text =
                        viewModel.getLangResources().getString(R.string.error)

                    errorContainer.errorMsg2.text = viewModel.getLangResources().getString(R.string.error_msg)
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