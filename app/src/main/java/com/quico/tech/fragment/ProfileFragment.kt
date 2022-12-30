package com.quico.tech.fragment

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.quico.tech.R
import com.quico.tech.activity.*
import com.quico.tech.data.Constant
import com.quico.tech.databinding.FragmentProfileBinding
import com.quico.tech.viewmodel.SharedViewModel
import java.util.*


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
        loadImage()
        binding.apply {

            if (viewModel.user == null) {
                loginInfo.visibility = View.GONE
                profileErrorContainer.root.visibility = View.VISIBLE
            }
            viewModel.user?.let { user ->
                loginInfo.visibility = View.VISIBLE
                profileErrorContainer.root.visibility = View.GONE
                if (user.is_vip)
                    vipImage.visibility = View.VISIBLE
                else
                    vipImage.visibility = View.GONE
            }

            profileErrorContainer.errorBtn.setOnClickListener {
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
                        .putExtra(Constant.ORDERS_TYPE, Constant.ONGOING_ORDERS)
                )
            }

            serviceOrderImage.setOnClickListener {
                startActivity(
                    Intent(activity, OrderListActivity::class.java)
                        .putExtra(Constant.ORDERS_TYPE, Constant.DONE_ORDERS)
                )
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

    private fun loadImage() {
        try {


            binding.apply {
                if (viewModel.user?.image.isNullOrEmpty()) {
                    profileImage.setImageResource(R.drawable.profile_user)
                } else {
                    Glide.with(this@ProfileFragment)
                        .load(viewModel.user?.image)
                        //.placeholder(R.drawable.placeholder)
                        //.error(R.drawable.imagenotfound)
                        .into(profileImage)
                }
            }
        } catch (e: Exception) {
        }
    }

    private fun decodeImage(){
        /*val imageBytes = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                  Base64.getDecoder().decode(viewModel.user?.image)
              } else {
                  android.util.Base64.decode(
                      viewModel.user?.image,
                      android.util.Base64.DEFAULT
                  )
              }
              var decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
              profileImage.setImageBitmap(decodedImage)
*/
    }

    private fun setUpText() {
        binding.apply {
            title.text = viewModel.getLangResources().getString(R.string.profile)
            cardText.text = viewModel.getLangResources().getString(R.string.card)
            shippingAddressesText.text =
                viewModel.getLangResources().getString(R.string.shipping_addresses)
            deliveryOrdersText.text =
                viewModel.getLangResources().getString(R.string.delivery_orders)
            serviceOrdersText.text = viewModel.getLangResources().getString(R.string.service_orders)
            faqText.text = viewModel.getLangResources().getString(R.string.faq)
            termsText.text = viewModel.getLangResources().getString(R.string.terms_and_conditions)
            settingsText.text = viewModel.getLangResources().getString(R.string.settings)
            profileErrorContainer.apply {

                errorImage.setImageResource(R.drawable.guest)

                tryAgain.visibility = View.GONE
                errorMsg1.text =
                    viewModel.getLangResources().getString(R.string.you_are_guest)
                errorMsg2.text =
                    viewModel.getLangResources().getString(R.string.need_to_login)
               errorBtn.text =
                    viewModel.getLangResources().getString(R.string.login)
            }
            // set margins to the button
        }
    }
}