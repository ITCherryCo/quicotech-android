package com.quico.tech.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.quico.tech.R
import com.quico.tech.adapter.BrandSearchRecyclerViewAdapter
import com.quico.tech.adapter.ProductRecyclerViewAdapter
import com.quico.tech.adapter.SpecificationRecyclerViewAdapter
import com.quico.tech.databinding.ActivityCompareProductBinding
import com.quico.tech.databinding.ActivityCompareSearchBinding
import com.quico.tech.model.Brand
import com.quico.tech.model.Specification
import com.quico.tech.viewmodel.SharedViewModel

class CompareProductActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCompareProductBinding
    private val viewModel: SharedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCompareProductBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpSpecificationsAdapter()
    }

    private fun setUpSpecificationsAdapter(){
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

            firstContainer.recyclerView.layoutManager = LinearLayoutManager(
                this@CompareProductActivity,
                LinearLayoutManager.VERTICAL,
                false
            )
            firstContainer.recyclerView.setItemAnimator(DefaultItemAnimator())
            firstContainer.recyclerView.setAdapter(specificationRecyclerViewAdapter)
            specificationRecyclerViewAdapter.differ.submitList(specifications)

            secondContainer.recyclerView.layoutManager = LinearLayoutManager(
                this@CompareProductActivity,
                LinearLayoutManager.VERTICAL,
                false
            )
            secondContainer.recyclerView.setItemAnimator(DefaultItemAnimator())
            secondContainer.recyclerView.setAdapter(specificationRecyclerViewAdapter)
        }
        }
    }
