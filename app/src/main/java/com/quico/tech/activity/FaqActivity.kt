package com.quico.tech.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.quico.tech.R
import com.quico.tech.adapter.FaqRecyclerViewAdapter
import com.quico.tech.data.Constant
import com.quico.tech.databinding.ActivityFaqBinding
import com.quico.tech.model.FAQ
import com.quico.tech.utils.Common
import com.quico.tech.viewmodel.SharedViewModel

class FaqActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFaqBinding
    private lateinit var faqRecyclerViewAdapter: FaqRecyclerViewAdapter
    private val viewModel: SharedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFaqBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initStatusBar()
        setUpText()
        setUpFAQAdapter()
    }

    fun initStatusBar(){
        Common.setSystemBarColor(this, R.color.white)
        Common.setSystemBarLight(this)
    }
    private fun setUpText() {
        binding.apply {
            backArrow.setOnClickListener {
                onBackPressed()
            }

            title.text = viewModel.getLangResources().getString(R.string.faq)
            faqText.text =
                viewModel.getLangResources().getString(R.string.frequently_asked_questions)

            if (viewModel.getLanguage().equals(Constant.AR))
                backArrow.scaleX = -1f
        }
    }

    fun setUpFAQAdapter() {
        // call the adapter for item list
        binding.apply {

            faqRecyclerViewAdapter = FaqRecyclerViewAdapter(viewModel)
            var faqList = ArrayList<FAQ>()
            faqList.add(
                FAQ(
                    viewModel.getLangResources().getString(R.string.available_shipping_method),
                    viewModel.getLangResources()
                        .getString(R.string.available_shipping_method_description), false
                )
            )

            faqList.add(
                FAQ(
                    viewModel.getLangResources().getString(R.string.delivery_estimation_time),
                    viewModel.getLangResources()
                        .getString(R.string.delivery_estimation_time_description), false
                )
            )

            faqList.add(
                FAQ(
                    viewModel.getLangResources().getString(R.string.how_to_track_order),
                    viewModel.getLangResources()
                        .getString(R.string.how_to_track_order_description), false
                )
            )

            faqList.add(
                FAQ(
                    viewModel.getLangResources().getString(R.string.need_account_to_place_order),
                    viewModel.getLangResources()
                        .getString(R.string.need_account_to_place_order_description), false
                )
            )

            faqList.add(
                FAQ(
                    viewModel.getLangResources().getString(R.string.need_account_to_place_order),
                    viewModel.getLangResources()
                        .getString(R.string.need_account_to_place_order_description), false
                )
            )

            recyclerView.layoutManager =
                LinearLayoutManager(this@FaqActivity, LinearLayoutManager.VERTICAL, false)
            recyclerView.setItemAnimator(DefaultItemAnimator())
            recyclerView.setAdapter(faqRecyclerViewAdapter)

            faqRecyclerViewAdapter.differ.submitList(faqList)
        }
    }
}