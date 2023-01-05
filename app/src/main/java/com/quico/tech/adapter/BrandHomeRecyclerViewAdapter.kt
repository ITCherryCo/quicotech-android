package com.quico.tech.adapter

import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.quico.tech.R
import com.quico.tech.activity.BrandDetailActivity
import com.quico.tech.activity.CategoryDetailActivity
import com.quico.tech.data.Constant
import com.quico.tech.databinding.BrandAllItemListBinding
import com.quico.tech.databinding.BrandHomeItemListBinding
import com.quico.tech.model.Brand


class BrandHomeRecyclerViewAdapter() : RecyclerView.Adapter<BrandHomeRecyclerViewAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(private var binding: BrandHomeItemListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        fun bind(brand: Brand) {
            binding.apply {
                //brandImage.setImageDrawable(itemView.resources.getDrawable(brand.image!!))

                Glide.with(itemView.context)
                    .load(brand.image)
                    //.placeholder(R.drawable.placeholder)
                    .error(R.drawable.profile_user)
                    .into(brandImage)

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
            BrandHomeItemListBinding.inflate(
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