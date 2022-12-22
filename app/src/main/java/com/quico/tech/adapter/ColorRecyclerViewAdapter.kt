package com.quico.tech.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.quico.tech.R
import com.quico.tech.databinding.ColorItemSelectionBinding
import com.quico.tech.model.Color

class ColorRecyclerViewAdapter : RecyclerView.Adapter<ColorRecyclerViewAdapter.ItemViewHolder>() {
    private var lastSelectedPosition = 0
    private var defaultSelectedPosition = -1

   inner class ItemViewHolder(private var binding: ColorItemSelectionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(current_color: Color) {
            binding.apply {
                colorCode.setBackgroundColor(android.graphics.Color.parseColor(current_color.color_code))
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
               cardView.strokeWidth = 6
           }
       }

       fun setUnselectedForm() {
           binding.apply {
               cardView.strokeWidth = 0
           }
       }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            ColorItemSelectionBinding.inflate(
                LayoutInflater.from(
                    parent.context
                )
            )
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val color = differ.currentList[position]
        holder.bind(color)
    }

    private var onItemClickListener: ((Color) -> Unit)? = null

    fun setOnItemClickListener(listener: (Color) -> Unit) {
        onItemClickListener = listener
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private val differCallback = object : DiffUtil.ItemCallback<Color>() {
        override fun areItemsTheSame(oldItem: Color, newItem: Color): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Color, newItem: Color): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)
}