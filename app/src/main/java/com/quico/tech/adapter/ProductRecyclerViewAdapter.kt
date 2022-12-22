package com.quico.tech.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.quico.tech.R
import com.quico.tech.databinding.ProductItemListBinding
import com.quico.tech.model.Product


class ProductRecyclerViewAdapter(val small:Boolean, val withSelection:Boolean, onProductSelect:OnProductSelect?) : RecyclerView.Adapter<ProductRecyclerViewAdapter.ItemViewHolder>() {
    private var lastSelectedPosition = -1
    private var defaultSelectedPosition = -1
    private var onProductSelect = onProductSelect

    interface OnProductSelect{
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
                    productImage.setImageDrawable(product.image)
                    productPrice.text = product.price.toString()
                } else {
                    largeContainer.visibility = View.VISIBLE
                    smallContainer.visibility = View.GONE
                    largeProductName.text = product.name
                    largeProductImage.setImageDrawable(product.image)
                    largeProductPrice.text = product.price.toString()
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
                }
            }
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