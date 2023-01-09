package com.quico.tech.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.quico.tech.databinding.SpecificationItemListBinding
import com.quico.tech.model.Specifications

class SpecificationRecyclerViewAdapter : RecyclerView.Adapter<SpecificationRecyclerViewAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(private var binding: SpecificationItemListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(specifications: Specifications) {
            binding.apply {
                name.text =  specifications.name
                value.text =  specifications.value
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            SpecificationItemListBinding.inflate(
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

    private var onItemClickListener: ((Specifications) -> Unit)? = null

    fun setOnItemClickListener(listener: (Specifications) -> Unit) {
        onItemClickListener = listener
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