package com.quico.tech.adapter

import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.quico.tech.R
import com.quico.tech.activity.BrandDetailActivity
import com.quico.tech.activity.RequestActivity
import com.quico.tech.data.Constant
import com.quico.tech.databinding.BrandAllItemListBinding
import com.quico.tech.databinding.CategoryAllItemListBinding
import com.quico.tech.databinding.ServiceItemListBinding
import com.quico.tech.model.Brand
import com.quico.tech.model.Category
import com.quico.tech.model.Service

class BrandAllRecyclerViewAdapter() : RecyclerView.Adapter<BrandAllRecyclerViewAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(private var binding: BrandAllItemListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        fun bind(brand: Brand) {
            binding.apply {
                brandImage.setImageDrawable(itemView.context.getDrawable(brand.image!!))

                itemView.setOnClickListener {
                    itemView.context.startActivity(
                        Intent(itemView.context, BrandDetailActivity::class.java).putExtra(
                            Constant.BRAND_ID, 1
                        )
                    )
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            BrandAllItemListBinding.inflate(
                LayoutInflater.from(
                    parent.context
                )
            )
        )
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val brand = differ.currentList[position]
        holder.bind(brand)
    }

    private var onItemClickListener: ((Brand) -> Unit)? = null

    fun setOnItemClickListener(listener: (Brand) -> Unit) {
        onItemClickListener = listener
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }


    private val differCallback = object : DiffUtil.ItemCallback<Brand>() {
        override fun areItemsTheSame(oldItem: Brand, newItem: Brand): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: Brand, newItem: Brand): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)
}