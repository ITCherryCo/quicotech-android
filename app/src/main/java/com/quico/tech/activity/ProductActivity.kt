package com.quico.tech.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.quico.tech.R
import com.quico.tech.adapter.ProductImageAdapter
import com.quico.tech.data.Constant
import com.quico.tech.databinding.ActivityProductBinding
import com.quico.tech.utils.Common
import com.quico.tech.viewmodel.SharedViewModel


class ProductActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProductBinding
    private val viewModel: SharedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initToolbar()

        setUpText()
        setImages()
    }

    fun initToolbar(){
        binding.apply {
            Common.setSystemBarColor(this@ProductActivity, R.color.home_background_grey)
            Common.setSystemBarLight(this@ProductActivity)
            toolbarProductDetails.title.text = viewModel.getLangResources().getString(R.string.single_offer_title)
        }
    }

    private fun setImages(){
        var images = arrayOf(R.drawable.product_image_test,R.drawable.computer_all,R.drawable.headphones)
        binding.apply {
            var productImageAdapter = ProductImageAdapter(images)
            viewPager.setAdapter(productImageAdapter)
            productImageAdapter.registerAdapterDataObserver(indicator.getAdapterDataObserver())
            indicator.setViewPager(viewPager)
        }
    }
    private fun setUpText() {
        binding.apply {
            newText.text = viewModel.getLangResources().getString(R.string.new_text)
            bestSellerText.text = viewModel.getLangResources().getString(R.string.best_seller)
            compareBtn.text = viewModel.getLangResources().getString(R.string.compare)
            addToCartBtn.text = viewModel.getLangResources().getString(R.string.add_to_cart)

            if (viewModel.getLanguage().equals(Constant.AR))
                toolbarProductDetails.backArrow.scaleX = -1f

           // oldPrice.setPaintFlags(oldPrice.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG) // draw line on old price
            oldPrice.setBackground(getResources().getDrawable(R.drawable.red_line))

            compareBtn.setOnClickListener {
                //startActivity(Intent(this@ProductActivity,CompareSearchActivity::class.java))
                startActivity(
                    Intent(this@ProductActivity, CompareProductActivity::class.java)
                        .putExtra(Constant.ITEM_ID, 1)
                )
            }

            toolbarProductDetails.cartImage.setOnClickListener {
                startActivity(Intent(this@ProductActivity,CartActivity::class.java))
            }
        }
    }
}