package com.quico.tech.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.quico.tech.R
import com.quico.tech.databinding.ActivityAddressBinding
import com.quico.tech.databinding.ActivityAddressListBinding
import com.quico.tech.viewmodel.SharedViewModel

class AddressActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddressBinding
    private val viewModel: SharedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}