package com.quico.tech.fragment

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import com.quico.tech.R
import com.quico.tech.activity.*
import com.quico.tech.data.Constant
import com.quico.tech.databinding.FragmentProfileBinding
import com.quico.tech.viewmodel.SharedViewModel


class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SharedViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpText()
        binding.apply {

            if (viewModel.current_session_id==null){
                loginInfo.visibility = View.GONE
                errorContainer.visibility = View.VISIBLE
            }
            else{
                loginInfo.visibility = View.VISIBLE
                errorContainer.visibility = View.GONE
            }

            loginBtn.setOnClickListener {
                startActivity(Intent(activity, LoginActivity::class.java))
            }

            editImage.setOnClickListener {
                startActivity(Intent(activity, EditProfileActivity::class.java))
            }

            cardImage.setOnClickListener {
                startActivity(Intent(activity, CardListActivity::class.java))
            }

            shippingImage.setOnClickListener {
                startActivity(Intent(activity, ShippingAddressActivity::class.java))
            }

            deliveryOrderImage.setOnClickListener {
                startActivity(
                    Intent(activity, OrderListActivity::class.java)
                    .putExtra(Constant.ORDERS_TYPE, Constant.ONGOING_ORDERS))
            }

            serviceOrderImage.setOnClickListener {
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
    }


    private fun setUpText() {
        binding.apply {
            title.text = viewModel.getLangResources().getString(R.string.profile)
            cardText.text = viewModel.getLangResources().getString(R.string.card)
            shippingAddressesText.text = viewModel.getLangResources().getString(R.string.shipping_addresses)
            deliveryOrdersText.text = viewModel.getLangResources().getString(R.string.delivery_orders)
            serviceOrdersText.text = viewModel.getLangResources().getString(R.string.service_orders)
            faqText.text = viewModel.getLangResources().getString(R.string.faq)
            termsText.text = viewModel.getLangResources().getString(R.string.terms_and_conditions)
            settingsText.text = viewModel.getLangResources().getString(R.string.settings)
            errorMsg1.text = viewModel.getLangResources().getString(R.string.you_are_guest)
            errorMsg2.text = viewModel.getLangResources().getString(R.string.need_to_login)
            loginBtn.text = viewModel.getLangResources().getString(R.string.login)

        }
    }
}