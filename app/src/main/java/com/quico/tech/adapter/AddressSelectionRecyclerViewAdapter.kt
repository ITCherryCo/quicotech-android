package com.quico.tech.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.quico.tech.R
import com.quico.tech.databinding.AddressItemSelectionBinding
import com.quico.tech.model.Address

class AddressSelectionRecyclerViewAdapter :
    RecyclerView.Adapter<AddressSelectionRecyclerViewAdapter.ItemViewHolder>() {
    private var lastSelectedPosition = -1
    private var defaultSelectedPosition = -1

    inner class ItemViewHolder(private var binding: AddressItemSelectionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(address: Address) {
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
                mainContainer.background = itemView.resources.getDrawable(R.drawable.container_black_stroke_1)
            }
        }

        fun setUnselectedForm() {
            binding.apply {
                checkImage.visibility = View.GONE
                mainContainer.background = itemView.resources.getDrawable(R.drawable.container_gray_stroke_1)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            AddressItemSelectionBinding.inflate(
                LayoutInflater.from(
                    parent.context
                )
            )
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val address = differ.currentList[position]
        holder.bind(address)
    }

    private var onItemClickListener: ((Address) -> Unit)? = null

    fun setOnItemClickListener(listener: (Address) -> Unit) {
        onItemClickListener = listener
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }


    private val differCallback = object : DiffUtil.ItemCallback<Address>() {
        override fun areItemsTheSame(oldItem: Address, newItem: Address): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Address, newItem: Address): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)
}