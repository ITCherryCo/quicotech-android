package com.quico.tech.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.quico.tech.R
import com.quico.tech.adapter.CartRecyclerViewAdapter
import com.quico.tech.data.Constant
import com.quico.tech.databinding.ActivityCartBinding
import com.quico.tech.model.Item
import com.quico.tech.viewmodel.SharedViewModel

class CartActivity : AppCompatActivity() {
    private lateinit var binding : ActivityCartBinding
    private lateinit var cartRecyclerViewAdapter : CartRecyclerViewAdapter
    private val viewModel: SharedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

         binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpCartAdapter()
        setUpText()
        binding.apply {
            checkoutBtn.setOnClickListener {
                startActivity(Intent(this@CartActivity, PaymentMethodActivity::class.java))
            }

            backArrow.setOnClickListener {
                onBackPressed()
            }
        }
    }

    private fun setUpText(){
        binding.apply {
            title.text = viewModel.getLangResources().getString(R.string.cart)
            totalText.text = viewModel.getLangResources().getString(R.string.total_tax_includes)
            checkoutBtn.text = viewModel.getLangResources().getString(R.string.checkout)

            if (viewModel.getLanguage().equals(Constant.AR))
                backArrow.scaleX = -1f
        }
    }

    fun setUpCartAdapter() {
        // call the adapter for item list
        binding.apply {
            cartRecyclerViewAdapter = CartRecyclerViewAdapter(viewModel)
            var items = ArrayList<Item>()
            items.add(Item(1, ""))
            items.add(Item(1, ""))
            items.add(Item(1, ""))
            items.add(Item(1, ""))
            items.add(Item(1, ""))


                recyclerView.layoutManager =
                    LinearLayoutManager(this@CartActivity, LinearLayoutManager.VERTICAL, false)
                recyclerView.setItemAnimator(DefaultItemAnimator())
                recyclerView.setAdapter(cartRecyclerViewAdapter)

            cartRecyclerViewAdapter.differ.submitList(items)
        }
    }

    fun setUpErrorForm(error_type:String){

        binding.apply {

        }
    }
}