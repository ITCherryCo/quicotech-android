package com.quico.tech.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.quico.tech.databinding.ActivityHomeTestBinding
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

            cartBtn.setOnClickListener {
                startActivity(Intent(this@HomeTestActivity, CartActivity::class.java))
            }

            profileBtn.setOnClickListener {
                startActivity(Intent(this@HomeTestActivity, ProfileActivity::class.java))
            }

            maintenanceBtn.setOnClickListener {
                startActivity(Intent(this@HomeTestActivity, MaintenanceListActivity::class.java))
            }
        }
    }

}