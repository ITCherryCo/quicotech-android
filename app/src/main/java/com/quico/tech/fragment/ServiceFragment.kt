package com.quico.tech.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.quico.tech.R
import com.quico.tech.adapter.MaintenanceRecyclerViewAdapter
import com.quico.tech.data.Constant
import com.quico.tech.data.Maintenance
import com.quico.tech.databinding.FragmentServiceBinding
import com.quico.tech.viewmodel.SharedViewModel


class ServiceFragment : Fragment() {

    private var _binding: FragmentServiceBinding? = null
    private val binding get() = _binding!!
    private lateinit var maintenanceRecyclerViewAdapter: MaintenanceRecyclerViewAdapter
    private val viewModel: SharedViewModel by viewModels()

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
        setUpMaintenanceAdapter()
    }

    private fun setUpMaintenanceAdapter() {
        binding.apply {

            maintenanceRecyclerViewAdapter = MaintenanceRecyclerViewAdapter()
            var maintenanceList = ArrayList<Maintenance>()

            maintenanceList.add(
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
            )

            recyclerView.layoutManager =
                LinearLayoutManager(
                    activity,
                    LinearLayoutManager.VERTICAL,
                    false
                )
            recyclerView.setItemAnimator(DefaultItemAnimator())
            recyclerView.setAdapter(maintenanceRecyclerViewAdapter)

            maintenanceRecyclerViewAdapter.differ.submitList(maintenanceList)
        }
    }


}