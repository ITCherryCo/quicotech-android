package com.quico.tech.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.quico.tech.R
import com.quico.tech.databinding.VisaCardItemSelectionBinding
import com.quico.tech.model.Card

class CardSelectionRecyclerViewAdapter : RecyclerView.Adapter<CardSelectionRecyclerViewAdapter.ItemViewHolder>() {
    private var lastSelectedPosition = -1
    private var defaultSelectedPosition = -1
   inner class ItemViewHolder(private var binding: VisaCardItemSelectionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(card: Card) {
            binding.apply {
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
               checkImage.visibility = View.VISIBLE
               visaContainer.background = itemView.resources.getDrawable(R.drawable.container_black_stroke_1)
           }
       }

       fun setUnselectedForm() {
           binding.apply {
               checkImage.visibility = View.GONE
               visaContainer.background = itemView.resources.getDrawable(R.drawable.container_gray_stroke_1)
           }
       }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            VisaCardItemSelectionBinding.inflate(
                LayoutInflater.from(
                    parent.context
                )
            )
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = differ.currentList[position]
        holder.bind(item)
    }

    private var onItemClickListener: ((Card) -> Unit)? = null

    fun setOnItemClickListener(listener: (Card) -> Unit) {
        onItemClickListener = listener
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private val differCallback = object : DiffUtil.ItemCallback<Card>() {
        override fun areItemsTheSame(oldItem: Card, newItem: Card): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Card, newItem: Card): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)
}