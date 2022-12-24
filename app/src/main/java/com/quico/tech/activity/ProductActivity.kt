package com.quico.tech.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.quico.tech.R
import com.quico.tech.adapter.ProductImageAdapter
import com.quico.tech.data.Constant
import com.quico.tech.databinding.ActivityProductBinding
import com.quico.tech.viewmodel.SharedViewModel


class ProductActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProductBinding
    private val viewModel: SharedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpText()
        setImages()
    }

    private fun setImages(){
        var images = arrayOf("","","")
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
                toolbar.backArrow.scaleX = -1f

           // oldPrice.setPaintFlags(oldPrice.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG) // draw line on old price
            oldPrice.setBackground(getResources().getDrawable(R.drawable.red_line))

        }
    }
}