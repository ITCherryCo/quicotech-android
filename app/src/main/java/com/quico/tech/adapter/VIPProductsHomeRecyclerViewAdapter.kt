package com.quico.tech.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.ui.graphics.Paint
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


class VIPProductsHomeRecyclerViewAdapter() : RecyclerView.Adapter<VIPProductsHomeRecyclerViewAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(private var binding: ProductItemListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product) {

            binding.apply {

                largeContainer.visibility = View.GONE
                smallContainer.visibility = View.VISIBLE

                productName.text = product.name
                Glide.with(itemView.context)
                    .load(product.image)
                    .fitCenter()
                    .error(R.drawable.empty_item)
                    .into(productImage)

                smallProductOldPrice.text = "$ ${product.regular_price.toString()}"
                smallProductNewPrice.text ="$ ${product.new_price.toString()}"
                smallProductOldPrice.setBackground(itemView.resources.getDrawable(R.drawable.red_line))
                smallVipText.visibility = View.VISIBLE
                smallSaleText.visibility = View.GONE

                cardViewSmall.setOnClickListener {
                    itemView.context.startActivity(
                        Intent(itemView.context, ProductActivity::class.java).putExtra(PRODUCT_ID, product.id)
                            .putExtra(PRODUCT_NAME,product.name)

                    )
                }

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