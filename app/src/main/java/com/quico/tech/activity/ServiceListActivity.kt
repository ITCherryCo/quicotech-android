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
import com.quico.tech.model.ServiceType
import com.quico.tech.utils.Common
import com.quico.tech.utils.Resource
import com.quico.tech.viewmodel.SharedViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ServiceListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityServiceListBinding
    private lateinit var serviceRecyclerViewAdapter: ServiceRecyclerViewAdapter
    private val viewModel: SharedViewModel by viewModels()
    private var service_type_id:Int=0

    var TAG = "SERVICES_RESPONSE"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityServiceListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        service_type_id = intent.extras!!.getInt(Constant.SERVICE_ID)

        binding.apply {

            backArrow.setOnClickListener {
                onBackPressed()
            }
        }

        initStatusBar()
        setUpText()
        subscribeServices()
        setUpServicesAdapter()
        onRefresh()
        viewModel.getServiceTypes(service_type_id)
    }

    fun initStatusBar(){
        Common.setSystemBarColor(this, R.color.white)
        Common.setSystemBarLight(this)
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
            viewModel.service_types.collect { response ->
                when (response) {
                    is Resource.Success -> {
                        binding.apply {
                            swipeRefreshLayout.setRefreshing(false)
                            swipeRefreshLayout.setEnabled(false)
                            serviceErrorContainer.root.visibility=View.GONE
                            recyclerView.visibility = View.VISIBLE
                        }
                        stopShimmer()

                        response.data?.let { servicesTypeResponse ->
                            if (servicesTypeResponse.result!!.isEmpty())
                                setUpErrorForm(Constant.NO_SERVICES)
                            else {
                                serviceRecyclerViewAdapter.differ.submitList(servicesTypeResponse.result)
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
            var services = ArrayList<ServiceType>()
            recyclerView.visibility = View.VISIBLE
            swipeRefreshLayout.setRefreshing(false)

          /*  services.add(Service(1))
            services.add(Service(1))
            services.add(Service(1))
            services.add(Service(1))
            services.add(Service(1))
            services.add(Service(1))
            services.add(Service(1))
            services.add(Service(1))*/

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
            serviceErrorContainer.root.visibility = View.GONE
            shimmer.visibility = View.VISIBLE
            shimmer.startShimmer()
           // swipeRefreshLayout.setRefreshing(true)

           /* lifecycleScope.launch {
                delay(3000)
                setUpServicesAdapter()
            }*/
        }
    }

    fun onRefresh() {
        binding.apply {
            swipeRefreshLayout.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
                setLoading() // later we will remove it because the observable will call it
                 viewModel.getServiceTypes(service_type_id) // get User id
            })
        }
    }

    fun setUpErrorForm(error_type: String) {
        binding.apply {
            swipeRefreshLayout.setRefreshing(false)
            recyclerView.visibility = View.GONE
            swipeRefreshLayout.setEnabled(true)
            stopShimmer()
            serviceErrorContainer.apply {
                errorMsg1.visibility = View.GONE
                root.visibility = View.VISIBLE
                errorBtn.visibility = View.GONE
                tryAgain.visibility = View.GONE
                errorImage.setImageResource(android.R.color.transparent)
                errorImage.setImageResource(R.drawable.empty_item)

                when (error_type) {
                    Constant.CONNECTION -> {
                        errorMsg2.setText(
                            viewModel.getLangResources().getString(R.string.check_connection)
                        )
                    }
                    Constant.NO_SERVICES -> {
                        errorMsg2.text =
                            viewModel.getLangResources().getString(R.string.no_services)
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