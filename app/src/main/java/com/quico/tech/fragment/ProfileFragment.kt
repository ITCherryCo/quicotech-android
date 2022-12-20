package com.quico.tech.fragment

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import com.quico.tech.R
import com.quico.tech.activity.*
import com.quico.tech.data.Constant
import com.quico.tech.databinding.FragmentHomeBinding
import com.quico.tech.databinding.FragmentProfileBinding
import com.quico.tech.viewmodel.SharedViewModel


class ProfileFragment : Fragment() {
    private lateinit var binding : FragmentProfileBinding
    private val viewModel: SharedViewModel by viewModels()


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        setUpText()
        binding.apply {
            editImage.setOnClickListener {
                startActivity(Intent(activity, EditProfileActivity::class.java))
            }

            cardImage.setOnClickListener {
                startActivity(Intent(activity, CardListActivity::class.java))
            }

            shippingImage.setOnClickListener {
                startActivity(Intent(activity, ShippingAddressActivity::class.java))
                // startActivity(Intent(this@ProfileActivity, ShippingAddressListActivity::class.java))
            }

            ongoingOrderImage.setOnClickListener {
                startActivity(
                    Intent(activity, OrderListActivity::class.java)
                    .putExtra(Constant.ORDERS_TYPE, Constant.ONGOING_ORDERS))
            }

            doneOrderImage.setOnClickListener {
                startActivity(
                    Intent(activity, OrderListActivity::class.java)
                    .putExtra(Constant.ORDERS_TYPE, Constant.DONE_ORDERS))
            }

            termsImage.setOnClickListener {
                startActivity(Intent(activity, GeneralWebInfoActivity::class.java))
            }

            faqImage.setOnClickListener {
                startActivity(Intent(activity, FaqActivity::class.java))
            }
            settingsImage.setOnClickListener {
                startActivity(Intent(activity, SettingsActivity::class.java))
            }
        }

        return binding.getRoot()
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