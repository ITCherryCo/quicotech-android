package com.quico.tech.fragment

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.quico.tech.R
import com.quico.tech.activity.CompareSearchActivity
import com.quico.tech.adapter.BrandRecyclerViewAdapter
import com.quico.tech.adapter.CategoryRecyclerViewAdapter
import com.quico.tech.adapter.ColorRecyclerViewAdapter
import com.quico.tech.adapter.ProductRecyclerViewAdapter
import com.quico.tech.databinding.FilterBottomSheetBinding
import com.quico.tech.databinding.FragmentSearchBinding
import com.quico.tech.model.Brand
import com.quico.tech.model.Category
import com.quico.tech.model.Product
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

        setUpItemsAdapter()
        setUpText()
        setCategoriesLoading()
//        viewModel.search(1)
    }

    private fun setUpText() {
        binding.apply {

            searchToolbar.title.text = viewModel.getLangResources().getString(R.string.filters)
            filterText.text = viewModel.getLangResources().getString(R.string.filter)
            searchToolbar.heartImage.visibility = View.GONE
            filterImage.setOnClickListener {
                setUpFilerBottomSheet()
            }
            searchToolbar.cartImage.setOnClickListener {
                startActivity(Intent(requireContext(),CompareSearchActivity::class.java))
            }
        }
    }

    fun setUpItemsAdapter() {
        binding.apply {
            productRecyclerViewAdapter = ProductRecyclerViewAdapter(false,false,null)
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

            productRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
            productRecyclerView.setItemAnimator(DefaultItemAnimator())
            productRecyclerView.setAdapter(productRecyclerViewAdapter)
            productRecyclerViewAdapter.differ.submitList(items)
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
            productShimmer.stopShimmer()
        }
    }


    fun setCategoriesLoading() {
        binding.apply {
            categoryContainer.visibility = View.GONE
            productRecyclerView.visibility = View.GONE
            progressBar.visibility = View.GONE
            categoryShimmer.visibility = View.VISIBLE
            categoryShimmer.startShimmer()
            // filterImage.setEnabled(false)

            lifecycleScope.launch {
                delay(2000)
                setUpCategoryAdapter()
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
            // filterImage.setEnabled(false)

            lifecycleScope.launch {
                delay(1500)
                setUpItemsAdapter()
                setUpCategoryAdapter()
            }
        }
    }

//    fun onRefresh() {
//        setLoading() // later we will remove it because the observable will call it
//        // viewModel.search(1) // get User id
//    }

    private fun setUpCategoryAdapter() {
        binding.apply {
            stopCategoryShimmer()
            categoriesText.text = viewModel.getLangResources().getString(R.string.categories)

            categoryContainer.visibility = View.VISIBLE
            categories.add(Category("Servers", resources.getDrawable(R.drawable.server)))
            categories.add(Category("Computers", resources.getDrawable(R.drawable.computer)))
            categories.add(Category("Mobiles", resources.getDrawable(R.drawable.aurdino)))
            categories.add(Category("Servers", resources.getDrawable(R.drawable.games)))
            categories.add(Category("Computers", resources.getDrawable(R.drawable.computer)))
            categories.add(Category("Mobiles", resources.getDrawable(R.drawable.aurdino)))
            categories.add(Category("Servers", resources.getDrawable(R.drawable.server)))
            categories.add(Category("Computers", resources.getDrawable(R.drawable.computer)))
            categories.add(Category("Mobiles", resources.getDrawable(R.drawable.server)))

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
    }


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

            brands.add(Brand(1, "Brand Name",null))
            brands.add(Brand(1, "Brand Name",null))
            brands.add(Brand(1, "Brand Name",null))
            brands.add(Brand(1, "Brand Name",null))
            brands.add(Brand(1, "Brand Name",null))
            brands.add(Brand(1, "Brand Name",null))
            brands.add(Brand(1, "Brand Name",null))
            brands.add(Brand(1, "Brand Name",null))

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


            memory_sizes.add(Brand(1, "125 GB",null))
            memory_sizes.add(Brand(1, "125 GB",null))
            memory_sizes.add(Brand(1, "125 GB",null))
            memory_sizes.add(Brand(1, "125 GB",null))
            memory_sizes.add(Brand(1, "125 GB",null))
            memory_sizes.add(Brand(1, "125 GB",null))

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

            conditions.add(Brand(1, "new",null))
            conditions.add(Brand(1, "used",null))

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

    /*  fun setUpErrorForm(error_type: String) {
          binding.apply {
              recyclerView.visibility = View.GONE
              errorContainer.visibility = View.VISIBLE
              progressBar.visibility = View.GONE

              stopShimmer()
              tryAgain.text =
                  viewModel.getLangResources().getString(R.string.try_again)

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
                  }

                  Constant.ERROR -> {
                      errorMsg1.text =
                          viewModel.getLangResources().getString(R.string.error)

                      errorMsg2.text = viewModel.getLangResources().getString(R.string.error_msg)
                  }
              }
          }
      }*/


    companion object {

        fun newInstance(param1: String, param2: String) =
            SearchFragment().apply {

            }
    }
}