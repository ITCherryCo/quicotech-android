package com.quico.tech.activity

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import com.quico.tech.R
import com.quico.tech.data.Constant.DONE_ORDERS
import com.quico.tech.data.Constant.ONGOING_ORDERS
import com.quico.tech.data.Constant.ORDERS_TYPE
import com.quico.tech.databinding.ActivityProfileBinding
import com.quico.tech.viewmodel.SharedViewModel

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private val viewModel: SharedViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpText()
        binding.apply {
            editImage.setOnClickListener {
                startActivity(Intent(this@ProfileActivity, EditProfileActivity::class.java))
            }

            cardImage.setOnClickListener {
                startActivity(Intent(this@ProfileActivity, CardListActivity::class.java))
            }

            shippingImage.setOnClickListener {
                startActivity(Intent(this@ProfileActivity, ShippingAddressActivity::class.java))
               // startActivity(Intent(this@ProfileActivity, ShippingAddressListActivity::class.java))
            }

            ongoingOrderImage.setOnClickListener {
                startActivity(Intent(this@ProfileActivity, OrderListActivity::class.java)
                    .putExtra(ORDERS_TYPE,ONGOING_ORDERS))
            }

            doneOrderImage.setOnClickListener {
                startActivity(Intent(this@ProfileActivity, OrderListActivity::class.java)
                    .putExtra(ORDERS_TYPE,DONE_ORDERS))
            }

            termsImage.setOnClickListener {
                startActivity(Intent(this@ProfileActivity, GeneralWebInfoActivity::class.java))
            }

            faqImage.setOnClickListener {
                startActivity(Intent(this@ProfileActivity, FaqActivity::class.java))
            }
            settingsImage.setOnClickListener {
                startActivity(Intent(this@ProfileActivity, SettingsActivity::class.java))
            }
        }
    }

    private fun setUpText() {
        binding.apply {
            title.text = viewModel.getLangResources().getString(R.string.profile)
            cardText.text = viewModel.getLangResources().getString(R.string.card)
            shippingAddressesText.text = viewModel.getLangResources().getString(R.string.shipping_addresses)
            doneOrdersText.text = viewModel.getLangResources().getString(R.string.done_orders)
            ongoingOrdersText.text = viewModel.getLangResources().getString(R.string.ongoing_orders)
            faqText.text = viewModel.getLangResources().getString(R.string.faq)
            termsText.text = viewModel.getLangResources().getString(R.string.terms_and_conditions)
            settingsText.text = viewModel.getLangResources().getString(R.string.settings)

        }
    }
}