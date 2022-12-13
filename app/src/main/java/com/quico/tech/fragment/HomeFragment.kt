package com.quico.tech.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.quico.tech.R
import com.quico.tech.adapter.AddressSelectionRecyclerViewAdapter
import com.quico.tech.adapter.CategoryRecyclerViewAdapter
import com.quico.tech.databinding.ActivityHomeBinding
import com.quico.tech.databinding.FragmentHomeBinding
import com.quico.tech.model.Address
import com.quico.tech.model.Category


class HomeFragment : Fragment() {
    private lateinit var binding : FragmentHomeBinding
    private lateinit var categoryRecyclerViewAdapter: CategoryRecyclerViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        getCategories();
        return binding.getRoot()
    }

    fun getCategories(){
        binding.apply {

            categoryRecyclerViewAdapter = CategoryRecyclerViewAdapter()
            val categories = ArrayList<Category>()
            categories.add(Category("Servers",resources.getDrawable(R.drawable.server)))
            categories.add(Category("Computers",resources.getDrawable(R.drawable.server)))
            categories.add(Category("Mobiles",resources.getDrawable(R.drawable.server)))
            categories.add(Category("Servers",resources.getDrawable(R.drawable.server)))
            categories.add(Category("Computers",resources.getDrawable(R.drawable.server)))
            categories.add(Category("Mobiles",resources.getDrawable(R.drawable.server)))
            categories.add(Category("Servers",resources.getDrawable(R.drawable.server)))
            categories.add(Category("Computers",resources.getDrawable(R.drawable.server)))
            categories.add(Category("Mobiles",resources.getDrawable(R.drawable.server)))

            homeContent.recyclerViewCategories.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
            homeContent.recyclerViewCategories.setItemAnimator(DefaultItemAnimator())
            homeContent.recyclerViewCategories.setAdapter(categoryRecyclerViewAdapter)

            categoryRecyclerViewAdapter.differ.submitList(categories)

        }
    }

}