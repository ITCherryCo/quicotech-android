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

            wishlistToolbar.title.text = viewModel.getLangResources().getString(R.string.wishlist)
            wishlistToolbar.heartImage.visibility = View.GONE
            wishlistToolbar.backArrow.visibility = View.GONE

            wishlistToolbar.cartImage.setOnClickListener {
                startActivity(Intent(requireContext(), CompareSearchActivity::class.java))
            }
        }
    }

    fun setUpItemsAdapter() {
        binding.apply {
            productRecyclerViewAdapter = ProductRecyclerViewAdapter(false, false, null)
            val items = ArrayList<Product>()
            stopShimmer()
            recyclerView.visibility = View.VISIBLE
            // filterImage.setEnabled(true)

            items.add(Product("P1",  9.9))
            items.add(Product("P2", 22.9))
            items.add(Product("P3", 43.9))
            items.add(Product("P4", 45.22))
            items.add(Product("P5", 93.9))
            items.add(Product("P6",  49.9))
            items.add(Product("P7",  19.9))
            items.add(Product("P8",  59.9))
            items.add(Product("P9",  69.9))

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
            stopShimmer()
            favoriteErrorContainer.apply {
                errorContainer.visibility = View.VISIBLE
                tryAgain.visibility = View.VISIBLE
                errorImage.visibility = View.VISIBLE
                errorImage.setImageResource(android.R.color.transparent)
                errorBtn.text = viewModel.getLangResources().getString(R.string.start_shopping)
                tryAgain.setText(
                    viewModel.getLangResources().getString(R.string.try_again)
                )
                tryAgain.setOnClickListener {  }

                when (error_type) {
                    Constant.CONNECTION -> {
                        errorMsg1.text =
                            viewModel.getLangResources().getString(R.string.connection)

                        errorMsg2.text =
                            viewModel.getLangResources().getString(R.string.check_connection)

                    }
                    Constant.NO_ITEMS -> {
                        errorMsg1.text =
                            viewModel.getLangResources().getString(R.string.no_search_result)

                        errorMsg2.text =
                            viewModel.getLangResources().getString(R.string.no_search_result_msg)
                        errorImage.setImageResource(R.drawable.no_favorite)

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

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            WishlistFragment().apply {
            }
    }
}