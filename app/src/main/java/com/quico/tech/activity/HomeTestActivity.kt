package com.quico.tech.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.quico.tech.R
import com.quico.tech.adapter.CartRecyclerViewAdapter
import com.quico.tech.databinding.ActivityCartBinding
import com.quico.tech.databinding.ActivityHomeTestBinding
import com.quico.tech.model.Item
import com.quico.tech.utils.Common
import com.quico.tech.viewmodel.SharedViewModel

class HomeTestActivity : AppCompatActivity() {
    private lateinit var binding :ActivityHomeTestBinding
    private val viewModel: SharedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeTestBinding.inflate(layoutInflater)
        setContentView(binding.root)
       // init()
        binding.apply {

            Toast.makeText(this@HomeTestActivity, viewModel.getLanguage() + " " + viewModel.getStoreId(), Toast.LENGTH_SHORT).show()
            cartBtn.setOnClickListener {
                startActivity(Intent(this@HomeTestActivity, CartActivity::class.java))
            }

            profileBtn.setOnClickListener {
                startActivity(Intent(this@HomeTestActivity, ProfileActivity::class.java))
            }
        }
    }


}