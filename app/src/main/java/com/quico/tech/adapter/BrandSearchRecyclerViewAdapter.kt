package com.quico.tech.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.quico.tech.R
import com.quico.tech.databinding.BrandItemSelectionBinding
import com.quico.tech.databinding.BrandSearchItemSelectionBinding
import com.quico.tech.model.Brand
import java.security.AccessController.getContext

class BrandSearchRecyclerViewAdapter :
    RecyclerView.Adapter<BrandSearchRecyclerViewAdapter.ItemViewHolder>() {
    private var lastSelectedPosition = -1
    private var defaultSelectedPosition = -1

    inner class ItemViewHolder(private var binding: BrandSearchItemSelectionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @RequiresApi(Build.VERSION_CODES.P)
        fun bind(brand: Brand) {
            binding.apply {
                if (brand.image != null) {
                    name.visibility = View.GONE
                    image.visibility = View.VISIBLE
                    image.setImageResource(brand.image!!)
                } else {
                    name.visibility = View.VISIBLE
                    image.visibility = View.GONE
                    name.text = brand.name
                }

                itemView.setOnClickListener {
                    lastSelectedPosition = absoluteAdapterPosition
                    notifyDataSetChanged()
                }

                if (lastSelectedPosition == absoluteAdapterPosition) {
                    setSelectedForm()
                }
                if (lastSelectedPosition != absoluteAdapterPosition) {
                    setUnselectedForm()
                }
            }
        }

        @RequiresApi(Build.VERSION_CODES.P)
        fun setSelectedForm() {
            binding.apply {
                cardView.elevation = 20f
                cardView.setOutlineAmbientShadowColor(itemView.resources.getColor(R.color.color_primary_purple))
                cardView.setOutlineSpotShadowColor(itemView.resources.getColor(R.color.color_primary_purple))
                //cardView.strokeWidth = 6
            }
        }

        @RequiresApi(Build.VERSION_CODES.P)
        fun setUnselectedForm() {
            binding.apply {
                cardView.elevation = 0f
                cardView.setOutlineAmbientShadowColor(itemView.resources.getColor(R.color.white))
                cardView.setOutlineSpotShadowColor(itemView.resources.getColor(R.color.white))
                //cardView.strokeWidth = 0
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            BrandSearchItemSelectionBinding.inflate(
                LayoutInflater.from(
                    parent.context
                )
            )
        )
    }

    @RequiresApi(Build.VERSION_CODES.P)
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
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Brand, newItem: Brand): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)
}