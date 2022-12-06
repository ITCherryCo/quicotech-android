package com.quico.tech.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.quico.tech.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            editImage.setOnClickListener {
                startActivity(Intent(this@ProfileActivity, EditProfileActivity::class.java))
            }

            cardImage.setOnClickListener {
                startActivity(Intent(this@ProfileActivity, CardListActivity::class.java))
            }

            shippingImage.setOnClickListener {
                startActivity(Intent(this@ProfileActivity, ShippingAddressListActivity::class.java))
            }
        }
    }
}