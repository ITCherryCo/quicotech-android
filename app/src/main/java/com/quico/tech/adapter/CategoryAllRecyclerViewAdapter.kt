package com.quico.tech.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.quico.tech.R
import com.quico.tech.activity.RequestActivity
import com.quico.tech.databinding.CategoryAllItemListBinding
import com.quico.tech.databinding.ServiceItemListBinding
import com.quico.tech.model.Category
import com.quico.tech.model.Service

class CategoryAllRecyclerViewAdapter() : RecyclerView.Adapter<CategoryAllRecyclerViewAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(private var binding: CategoryAllItemListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(category: Category) {
            binding.apply {
                name.text = category.name
                image.setImageDrawable(category.image)


                itemView.setOnClickListener {
                    //itemView.context.startActivity(Intent(itemView.context, RequestActivity::class.java))
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            CategoryAllItemListBinding.inflate(
                LayoutInflater.from(
                    parent.context
                )
            )
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val category = differ.currentList[position]
        holder.bind(category)
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