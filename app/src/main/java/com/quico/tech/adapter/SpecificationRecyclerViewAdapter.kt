package com.quico.tech.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.quico.tech.databinding.SpecificationItemListBinding
import com.quico.tech.model.Specification

class SpecificationRecyclerViewAdapter : RecyclerView.Adapter<SpecificationRecyclerViewAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(private var binding: SpecificationItemListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(specification: Specification) {
            binding.apply {

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

    private var onItemClickListener: ((Specification) -> Unit)? = null

    fun setOnItemClickListener(listener: (Specification) -> Unit) {
        onItemClickListener = listener
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private val differCallback = object : DiffUtil.ItemCallback<Specification>() {
        override fun areItemsTheSame(oldItem: Specification, newItem: Specification): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Specification, newItem: Specification): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)
}