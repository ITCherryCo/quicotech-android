package com.quico.tech.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.quico.tech.adapter.CartRecyclerViewAdapter
import com.quico.tech.databinding.ActivityCartBinding
import com.quico.tech.model.Item

class CartActivity : AppCompatActivity() {
    private lateinit var binding : ActivityCartBinding
    private lateinit var cartRecyclerViewAdapter : CartRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

         binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpCartAdapter()
        binding.apply {
            checkoutBtn.setOnClickListener {
                startActivity(Intent(this@CartActivity, PaymentMethodActivity::class.java))
            }

            backArrow.setOnClickListener {
                onBackPressed()
            }
        }
    }



    fun setUpCartAdapter() {
        // call the adapter for item list

        binding.apply {

            cartRecyclerViewAdapter = CartRecyclerViewAdapter()
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