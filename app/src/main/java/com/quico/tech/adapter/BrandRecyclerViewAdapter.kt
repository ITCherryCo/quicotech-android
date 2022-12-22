package com.quico.tech.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.quico.tech.R
import com.quico.tech.databinding.BrandItemSelectionBinding
import com.quico.tech.databinding.CategoryItemListBinding
import com.quico.tech.model.Brand
import com.quico.tech.model.Category

class BrandRecyclerViewAdapter : RecyclerView.Adapter<BrandRecyclerViewAdapter.ItemViewHolder>() {
    private var lastSelectedPosition = -1
    private var defaultSelectedPosition = -1

   inner class ItemViewHolder(private var binding: BrandItemSelectionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(brand: Brand) {
            binding.apply {
                name.text = brand.name
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

        fun setSelectedForm() {
            binding.apply {
                cardView.strokeColor = itemView.resources.getColor(R.color.color_primary_purple)
                name.setBackgroundColor( itemView.resources.getColor(R.color.color_primary_purple))
                name.setTextColor( itemView.resources.getColor(R.color.white))
                //cardView.strokeWidth = 6
            }
        }

        fun setUnselectedForm() {
            binding.apply {
                cardView.strokeColor = itemView.resources.getColor(R.color.input_field_hint)
                name.setBackgroundColor( itemView.resources.getColor(R.color.white))
                name.setTextColor( itemView.resources.getColor(R.color.black))
                //cardView.strokeWidth = 0
            }
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            BrandItemSelectionBinding.inflate(
                LayoutInflater.from(
                    parent.context
                )
            )
        )
    }

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