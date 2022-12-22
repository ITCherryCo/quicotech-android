package com.quico.tech.adapter

import android.annotation.SuppressLint
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.quico.tech.R
import com.quico.tech.databinding.CategoryItemListBinding
import com.quico.tech.model.Category


class CategoryRecyclerViewAdapter(var withFilterSelection : Boolean) : RecyclerView.Adapter<CategoryRecyclerViewAdapter.ItemViewHolder>() {
    private var lastSelectedPosition = 1
    private var defaultSelectedPosition = -1

  inner class ItemViewHolder(private var binding: CategoryItemListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        fun bind(category: Category) {
            binding.apply {
                catTitle.text = category.name
                catImage.setImageDrawable(category.image)

                itemView.setOnClickListener {
                    if (withFilterSelection) {
                        lastSelectedPosition = absoluteAdapterPosition
                        notifyDataSetChanged()
                    }
                }

                if (withFilterSelection) {
                    if (lastSelectedPosition == absoluteAdapterPosition) {
                        container.setBackgroundColor(itemView.resources.getColor(R.color.white))
                        container.elevation = 6f
                    }
                    if (lastSelectedPosition != absoluteAdapterPosition) {
                        container.setBackgroundColor(itemView.resources.getColor(R.color.gray_light))
                        container.elevation = 0f
                    }
                }
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            CategoryItemListBinding.inflate(
                LayoutInflater.from(
                    parent.context
                )
            )
        )
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = differ.currentList[position]
        holder.bind(item)
    }

    private var onItemClickListener: ((Category) -> Unit)? = null

    fun setOnItemClickListener(listener: (Category) -> Unit) {
        onItemClickListener = listener
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private val differCallback = object : DiffUtil.ItemCallback<Category>() {
        override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)
}