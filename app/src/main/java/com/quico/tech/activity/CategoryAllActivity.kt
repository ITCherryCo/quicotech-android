package com.quico.tech.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.quico.tech.R
import com.quico.tech.adapter.CategoryAllRecyclerViewAdapter
import com.quico.tech.adapter.ServiceRecyclerViewAdapter
import com.quico.tech.data.Constant
import com.quico.tech.databinding.ActivityCategoryAllBinding
import com.quico.tech.model.Category
import com.quico.tech.model.Product
import com.quico.tech.model.Service
import com.quico.tech.utils.Resource
import com.quico.tech.viewmodel.SharedViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CategoryAllActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCategoryAllBinding
    private lateinit var categoryAllRecyclerViewAdapter: CategoryAllRecyclerViewAdapter
    private val viewModel: SharedViewModel by viewModels()

    var TAG = "CATEGORIES_RESPONSE"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCategoryAllBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.apply {

            backArrow.setOnClickListener {
                onBackPressed()
            }
        }

        setUpText()
        setLoading()
    }

    private fun setUpText(){
        binding.apply {
            title.text = viewModel.getLangResources().getString(R.string.categories)

            if (viewModel.getLanguage().equals(Constant.AR))
                backArrow.scaleX = -1f
        }
    }

    private fun setUpCategoriesAdapter() {
        binding.apply {
            categoryAllRecyclerViewAdapter = CategoryAllRecyclerViewAdapter()
            var categories = ArrayList<Category>()
            stopShimmer()
            recyclerView.visibility = View.VISIBLE
            swipeRefreshLayout.setRefreshing(false)

            categories.add(Category("Mobiles",resources.getDrawable(R.drawable.mobiles_all)))
            categories.add(Category("Servers",resources.getDrawable(R.drawable.server_all)))
            categories.add(Category("Computers",resources.getDrawable(R.drawable.computer_all)))
            categories.add(Category("Mobiles",resources.getDrawable(R.drawable.mobiles_all)))
            categories.add(Category("Servers",resources.getDrawable(R.drawable.server_all)))
            categories.add(Category("Computers",resources.getDrawable(R.drawable.computer_all)))
            categories.add(Category("Mobiles",resources.getDrawable(R.drawable.mobiles_all)))
            categories.add(Category("Servers",resources.getDrawable(R.drawable.server_all)))
            categories.add(Category("Computers",resources.getDrawable(R.drawable.computer_all)))

            recyclerView.layoutManager = GridLayoutManager(this@CategoryAllActivity, 2)
            recyclerView.setItemAnimator(DefaultItemAnimator())
            recyclerView.setAdapter(categoryAllRecyclerViewAdapter)
            categoryAllRecyclerViewAdapter.differ.submitList(categories)
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
            errorContainerCategoryAll.errorContainer.visibility = View.GONE
            shimmer.visibility = View.VISIBLE
            shimmer.startShimmer()
            // swipeRefreshLayout.setRefreshing(true)

            lifecycleScope.launch {
                delay(3000)
                setUpCategoriesAdapter()
            }
        }
    }

    fun onRefresh() {
        binding.apply {
            swipeRefreshLayout.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
                setLoading() // later we will remove it because the observable will call it
                // viewModel.getOrders(1, orders_type) // get User id
            })
        }
    }

    fun setUpErrorForm(error_type: String) {
        binding.apply {
            swipeRefreshLayout.setRefreshing(false)
            recyclerView.visibility = View.GONE
            errorContainerCategoryAll.errorContainer.visibility = View.VISIBLE
            errorContainerCategoryAll.errorImage.setImageResource(android.R.color.transparent)
            stopShimmer()
            errorContainerCategoryAll.errorMsg1.visibility = View.GONE
            swipeRefreshLayout.setEnabled(true)

            when (error_type) {
                Constant.CONNECTION -> {
                    errorContainerCategoryAll.errorMsg2.setText(
                        viewModel.getLangResources().getString(R.string.check_connection)
                    )
                }
                Constant.NO_Categories -> {
                    errorContainerCategoryAll.errorMsg2.text = viewModel.getLangResources().getString(R.string.no_categories)
                    errorContainerCategoryAll.errorImage.setImageResource(R.drawable.empty_item)
                }

                Constant.ERROR -> {
                    errorContainerCategoryAll.errorMsg2.setText(viewModel.getLangResources().getString(R.string.error_msg))
                }
            }
        }
    }
}