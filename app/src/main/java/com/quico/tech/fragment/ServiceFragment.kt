package com.quico.tech.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.quico.tech.R
import com.quico.tech.adapter.MaintenanceRecyclerViewAdapter
import com.quico.tech.data.Constant
import com.quico.tech.data.Maintenance
import com.quico.tech.databinding.FragmentServiceBinding
import com.quico.tech.model.Service
import com.quico.tech.utils.Resource
import com.quico.tech.viewmodel.SharedViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class ServiceFragment : Fragment() {

    private var _binding: FragmentServiceBinding? = null
    private val binding get() = _binding!!
    private lateinit var maintenanceRecyclerViewAdapter: MaintenanceRecyclerViewAdapter
    private val viewModel: SharedViewModel by viewModels()
    private var TAG="SERVICES_RESPONSE"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentServiceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            title.text = viewModel.getLangResources().getString(R.string.maintenance_and_recovery)
            if (viewModel.getLanguage().equals(Constant.AR))
                backArrow.scaleX = -1f

            backArrow.setOnClickListener {
                activity?.onBackPressed()
            }
        }
        onRefresh()
        setUpMaintenanceAdapter()
        subscribeServicesList()
        viewModel.getServices()
    }


    fun subscribeServicesList(){
        lifecycleScope.launch {
            viewModel.services.collect { response ->
                when (response) {
                    is Resource.Success -> {
                        binding.apply {
                            swipeRefreshLayout.setRefreshing(false)
                            swipeRefreshLayout.setEnabled(false)
                            serviceListErrorContainer.root.visibility=View.GONE
                            recyclerView.visibility = View.VISIBLE
                        }
                        stopShimmer()
                        response.data?.let { servicesResponse ->
                            if (servicesResponse.result!!.isNullOrEmpty())
                                setUpErrorForm(Constant.NO_SERVICES)
                            else {
                                maintenanceRecyclerViewAdapter.differ.submitList(servicesResponse.result!!)
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


    private fun setUpMaintenanceAdapter() {
        binding.apply {

            maintenanceRecyclerViewAdapter = MaintenanceRecyclerViewAdapter()
            var service = ArrayList<Service>()

          /*  maintenanceList.add(
                Maintenance(
                    1, viewModel.getLangResources().getString(R.string.repair),
                    viewModel.getLangResources().getString(R.string.repair_description),
                    R.drawable.repair
                )
            )

            maintenanceList.add(
                Maintenance(
                    2, viewModel.getLangResources().getString(R.string.data_recovery),
                    viewModel.getLangResources().getString(R.string.data_recovery_description),
                    R.drawable.repair
                )
            )

            maintenanceList.add(
                Maintenance(
                    3, viewModel.getLangResources().getString(R.string.it_services),
                    viewModel.getLangResources().getString(R.string.it_services_description),
                    R.drawable.repair
                )
            )

            maintenanceList.add(
                Maintenance(
                    4, viewModel.getLangResources().getString(R.string.maintenance_services),
                    viewModel.getLangResources()
                        .getString(R.string.maintenance_services_description),
                    R.drawable.repair
                )
            )*/

            recyclerView.layoutManager =
                LinearLayoutManager(
                    activity,
                    LinearLayoutManager.VERTICAL,
                    false
                )
            recyclerView.setItemAnimator(DefaultItemAnimator())
            recyclerView.setAdapter(maintenanceRecyclerViewAdapter)

            maintenanceRecyclerViewAdapter.differ.submitList(service)
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
            serviceListErrorContainer.root.visibility = View.GONE
            shimmer.visibility = View.VISIBLE
            shimmer.startShimmer()
            // swipeRefreshLayout.setRefreshing(true)


        }
    }

    fun onRefresh() {
        binding.apply {
            swipeRefreshLayout.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
                setLoading() // later we will remove it because the observable will call it
                 viewModel.getServices() // get User id
            })
        }
    }

    fun setUpErrorForm(error_type: String) {
        binding.apply {
            swipeRefreshLayout.setRefreshing(false)
            recyclerView.visibility = View.GONE
            swipeRefreshLayout.setEnabled(true)
            stopShimmer()
            serviceListErrorContainer.apply {
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