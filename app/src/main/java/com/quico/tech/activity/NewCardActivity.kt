package com.quico.tech.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.quico.tech.R
import com.quico.tech.databinding.ActivityCardListBinding
import com.quico.tech.databinding.ActivityEditProfileBinding
import com.quico.tech.databinding.ActivityNewCardBinding

class NewCardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewCardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewCardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {

            backArrow.setOnClickListener {
                onBackPressed()
            }
        }
    }
}