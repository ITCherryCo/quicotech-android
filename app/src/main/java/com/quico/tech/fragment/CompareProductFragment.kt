package com.quico.tech.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.quico.tech.adapter.SpecificationRecyclerViewAdapter
import com.quico.tech.data.Constant.ITEM_ID
import com.quico.tech.databinding.FragmentCompareProductBinding
import com.quico.tech.model.Specification
import com.quico.tech.viewmodel.SharedViewModel


class CompareProductFragment : Fragment() {
    private var item_id: Int = 0

    private var _binding: FragmentCompareProductBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SharedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            item_id = it.getInt(ITEM_ID)
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

        setUpSpecificationsAdapter()
        Log.d(ITEM_ID,item_id.toString())

    }


    private fun setUpSpecificationsAdapter() {
        binding.apply {
            var specificationRecyclerViewAdapter = SpecificationRecyclerViewAdapter()
            val specifications = ArrayList<Specification>()

            // filterImage.setEnabled(true)
            specifications.add(Specification(1))
            specifications.add(Specification(1))
            specifications.add(Specification(1))
            specifications.add(Specification(1))
            specifications.add(Specification(1))
            specifications.add(Specification(1))
            specifications.add(Specification(1))
            specifications.add(Specification(1))
            specifications.add(Specification(1))

            recyclerView.layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false
            )
            recyclerView.setItemAnimator(DefaultItemAnimator())
            recyclerView.setAdapter(specificationRecyclerViewAdapter)
            specificationRecyclerViewAdapter.differ.submitList(specifications)

        }
    }


    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CompareProductFragment().apply {

            }
    }
}