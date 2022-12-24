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
import com.quico.tech.adapter.ServiceRecyclerViewAdapter
import com.quico.tech.data.Constant
import com.quico.tech.databinding.ActivityServiceListBinding
import com.quico.tech.model.Service
import com.quico.tech.utils.Resource
import com.quico.tech.viewmodel.SharedViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ServiceListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityServiceListBinding
    private lateinit var serviceRecyclerViewAdapter: ServiceRecyclerViewAdapter
    private val viewModel: SharedViewModel by viewModels()

    var TAG = "SERVICES_RESPONSE"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityServiceListBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.apply {

            backArrow.setOnClickListener {
                onBackPressed()
            }
        }

        setUpText()
        setLoading()
//        onRefresh()
//        viewModel.getServices(1)
//        subscribeServices()
    }

    private fun setUpText(){
        binding.apply {
            title.text = viewModel.getLangResources().getString(R.string.services)

            if (viewModel.getLanguage().equals(Constant.AR))
                backArrow.scaleX = -1f
        }
    }

    fun subscribeServices(){
        lifecycleScope.launch {
            viewModel.services.collect { response ->
                when (response) {
                    is Resource.Success -> {
                        binding.apply {
                            swipeRefreshLayout.setRefreshing(false)
                            swipeRefreshLayout.setEnabled(false)
                            errorContainer.setVisibility(View.GONE)
                            recyclerView.visibility = View.VISIBLE
                        }

                        response.data?.let { cartResponse ->
                            if (cartResponse.services.isEmpty())
                                setUpErrorForm(Constant.NO_SERVICES)
                            else {
                                serviceRecyclerViewAdapter.differ.submitList(cartResponse.services)
                                binding.recyclerView.setVisibility(View.VISIBLE)
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

    private fun setUpServicesAdapter() {
        binding.apply {
            serviceRecyclerViewAdapter = ServiceRecyclerViewAdapter()
            var services = ArrayList<Service>()
            stopShimmer()
            recyclerView.visibility = View.VISIBLE
            swipeRefreshLayout.setRefreshing(false)

            services.add(Service(1))
            services.add(Service(1))
            services.add(Service(1))
            services.add(Service(1))
            services.add(Service(1))
            services.add(Service(1))
            services.add(Service(1))
            services.add(Service(1))

            recyclerView.layoutManager = GridLayoutManager(this@ServiceListActivity, 2)
            recyclerView.setItemAnimator(DefaultItemAnimator())
            recyclerView.setAdapter(serviceRecyclerViewAdapter)
            serviceRecyclerViewAdapter.differ.submitList(services)
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
            errorContainer.visibility = View.GONE
            shimmer.visibility = View.VISIBLE
            shimmer.startShimmer()
           // swipeRefreshLayout.setRefreshing(true)

            lifecycleScope.launch {
                delay(3000)
                setUpServicesAdapter()
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
            errorContainer.visibility = View.VISIBLE
            errorImage.setImageResource(android.R.color.transparent)
            stopShimmer()
            errorMsg1.visibility = View.GONE
            swipeRefreshLayout.setEnabled(true)

            when (error_type) {
                Constant.CONNECTION -> {
                    errorMsg2.setText(
                        viewModel.getLangResources().getString(R.string.check_connection)
                    )
                }
                Constant.NO_SERVICES -> {
                    errorMsg2.text = viewModel.getLangResources().getString(R.string.no_services)
                    errorImage.setImageResource(R.drawable.empty_item)
                }

                Constant.ERROR -> {
                    errorMsg2.setText(viewModel.getLangResources().getString(R.string.error_msg))
                }
            }
        }
    }
}