package com.quico.tech.adapter

import android.content.Intent
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.quico.tech.R
import com.quico.tech.activity.ProductActivity
import com.quico.tech.data.Constant.PRODUCT_ID
import com.quico.tech.data.Constant.PRODUCT_NAME
import com.quico.tech.databinding.ProductItemListBinding
import com.quico.tech.model.Product
import com.quico.tech.model.ProductBodyParameters
import com.quico.tech.model.ProductParams
import com.quico.tech.utils.Common
import com.quico.tech.viewmodel.SharedViewModel


class ProductRecyclerViewAdapter(
    val small: Boolean,
    val withSelection: Boolean,
    val withinWishlist: Boolean,
    val viewModel: SharedViewModel,
    onProductSelect: OnProductSelect?
) : RecyclerView.Adapter<ProductRecyclerViewAdapter.ItemViewHolder>() {
    private var lastSelectedPosition = -1
    private var defaultSelectedPosition = -1
    private var onProductSelect = onProductSelect

    interface OnProductSelect {
        fun onProductSelect(product: Product?)
    }

    inner class ItemViewHolder(private var binding: ProductItemListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product) {

            binding.apply {
                if (small) {
                    largeContainer.visibility = View.GONE
                    smallContainer.visibility = View.VISIBLE
                    productName.text = product.name
                    productPrice.text = product.new_price.toString()
                    product.image?.let {
                        if (it.isNotEmpty())
                            loadImage(it, productImage)
                    }

                } else {
                    largeContainer.visibility = View.VISIBLE
                    smallContainer.visibility = View.GONE
                    largeProductName.text = product.name
                    // largeProductPrice.text = product.new_price.toString()
                    product.image?.let {
                        if (it.isNotEmpty())
                            loadImage(it, largeProductImage)
                    }


                    if (product.is_vip || product.is_on_sale) {
                        largeOldPrice.text = "$ ${product.regular_price.toString()}"
                        largeNewPrice.text = "$ ${product.new_price.toString()}"
                        largeOldPrice.setBackground(itemView.resources.getDrawable(R.drawable.red_line))
                    }

                    if (product.is_vip) {
                        largeVipText.visibility = View.VISIBLE
                        largeSaleText.visibility = View.GONE
                    } else if (product.is_on_sale) {
                        largeVipText.visibility = View.GONE
                        largeSaleText.visibility = View.VISIBLE
                    }

                    else{
                        largeVipText.visibility = View.INVISIBLE
                        largeSaleText.visibility = View.GONE
                        largeNewPrice.visibility = View.GONE
                        largeOldPrice.text = "$ ${product.regular_price.toString()}"
                    }
                }

                if (withSelection) {
                    cardView.setOnClickListener {
                        lastSelectedPosition = absoluteAdapterPosition
                        notifyDataSetChanged()
                    }

                    if (lastSelectedPosition == absoluteAdapterPosition) {
                        setSelectedForm()
                        onProductSelect!!.onProductSelect(product)
                    }
                    if (lastSelectedPosition != absoluteAdapterPosition) {
                        setUnselectedForm()
                    }
                } else {
                    cardView.setOnClickListener {
                        itemView.context.startActivity(
                            Intent(itemView.context, ProductActivity::class.java).putExtra(
                                PRODUCT_ID,
                                product.id
                            )
                                .putExtra(PRODUCT_NAME, product.name)
                        )
                    }

                    cardViewSmall.setOnClickListener {
                        itemView.context.startActivity(
                            Intent(itemView.context, ProductActivity::class.java).putExtra(
                                PRODUCT_ID,
                                product.id
                            )
                                .putExtra(PRODUCT_NAME, product.name)
                        )
                    }
                }
                if (withSelection) {
                    heartImage.visibility = View.VISIBLE
                    val params = ProductBodyParameters(
                        ProductParams(
                            product.id
                        )
                    )

                    viewModel.removeFromWishlist(params,
                        object : SharedViewModel.ResponseStandard {
                            override fun onSuccess(
                                success: Boolean,
                                resultTitle: String,
                                message: String
                            ) {
                                // later add progress bar to view

                            }

                            override fun onFailure(
                                success: Boolean,
                                resultTitle: String,
                                message: String
                            ) {
                                Toast.makeText(
                                    itemView.context,
                                    message,
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        })
                }
            }
        }

        fun loadImage(image_url: String, imageView: ImageView) {
            Glide.with(itemView.context)
                .load(image_url)
                //.placeholder(R.drawable.placeholder)
                .error(R.drawable.empty_item)
                .fitCenter()
                .into(imageView)
        }

        fun setSelectedForm() {
            binding.apply {
                cardView.strokeColor = itemView.resources.getColor(R.color.color_primary_purple)
                cardView.strokeWidth = 5
            }
        }

        fun setUnselectedForm() {
            binding.apply {
                cardView.strokeColor = itemView.resources.getColor(R.color.input_field_hint)
                cardView.strokeWidth = 0
                //cardView.strokeWidth = 0
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            ProductItemListBinding.inflate(
                LayoutInflater.from(
                    parent.context
                )
            )
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = differ.currentList[position]
        holder.bind(item)
    }

    private var onItemClickListener: ((Product) -> Unit)? = null

    fun setOnItemClickListener(listener: (Product) -> Unit) {
        onItemClickListener = listener
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private val differCallback = object : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)
}