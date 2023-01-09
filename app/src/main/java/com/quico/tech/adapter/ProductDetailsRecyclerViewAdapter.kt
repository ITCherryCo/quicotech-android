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
import com.quico.tech.model.Specifications

class ProductDetailsRecyclerViewAdapter : RecyclerView.Adapter<ProductDetailsRecyclerViewAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(private var binding: ProductDetailsItemListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(specification: Specifications) {
            binding.apply {
                name.text = specification.name
                value.text = specification.value
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
        val specification = differ.currentList[position]
        holder.bind(specification)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private val differCallback = object : DiffUtil.ItemCallback<Specifications>() {
        override fun areItemsTheSame(oldItem: Specifications, newItem: Specifications): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: Specifications, newItem: Specifications): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)
}