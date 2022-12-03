package com.quico.tech.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.quico.tech.R
import com.quico.tech.databinding.ActivityEditProfileBinding
import com.quico.tech.databinding.ActivityNewCardBinding
import com.quico.tech.databinding.ActivityShippingAddressBinding

class ShippingAddressActivity : AppCompatActivity() {
    private lateinit var binding: ActivityShippingAddressBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityShippingAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {

//            backArrow.setOnClickListener {
//                onBackPressed()
//            }
        }
    }
}