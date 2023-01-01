package com.quico.tech.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.quico.tech.databinding.ProductDetailsItemListBinding
import com.quico.tech.databinding.SearchItemListBinding
import com.quico.tech.model.Product
import com.quico.tech.model.ProductDetails

class ProductDetailsRecyclerViewAdapter : RecyclerView.Adapter<ProductDetailsRecyclerViewAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(private var binding: ProductDetailsItemListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(productDetails: ProductDetails) {
            binding.apply {
                name.text = productDetails.name
                value.text = productDetails.value
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            ProductDetailsItemListBinding.inflate(
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

    private val differCallback = object : DiffUtil.ItemCallback<ProductDetails>() {
        override fun areItemsTheSame(oldItem: ProductDetails, newItem: ProductDetails): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: ProductDetails, newItem: ProductDetails): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)
}