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
        fun bind(productDetails: ArrayList<String>) {
            binding.apply {
                name.text = productDetails[0]
                value.text = productDetails[1]
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


    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private val differCallback = object : DiffUtil.ItemCallback<ArrayList<String>>() {
        override fun areItemsTheSame(oldItem: ArrayList<String>, newItem: ArrayList<String>): Boolean {
            return oldItem[0] == newItem[0]
        }

        override fun areContentsTheSame(oldItem: ArrayList<String>, newItem: ArrayList<String>): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)
}