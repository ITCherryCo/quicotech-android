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
import com.quico.tech.data.Constant
import com.quico.tech.databinding.OrderStatusItemBinding


class OrderStatusRecyclerViewAdapter(val onOrderStatusClick: OnOrderStatusClick) :
    RecyclerView.Adapter<OrderStatusRecyclerViewAdapter.ItemViewHolder>() {

    private var lastSelectedPosition = 0

    interface OnOrderStatusClick {
        fun onOrderStatusClick(status: String)
    }


    inner class ItemViewHolder(private var binding: OrderStatusItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(status: Array<String>) {
            binding.apply {
                ordersStatus.text = status[1]
                //orderNumber.text = itemView.resources.getString(R.string.order_number,"12")

                ordersStatus.setOnClickListener {
                    lastSelectedPosition = absoluteAdapterPosition
                    notifyDataSetChanged()
                }

                if (lastSelectedPosition == absoluteAdapterPosition) {
                    setSelectedForm()
                    onOrderStatusClick.onOrderStatusClick("ALL")
                   // onOrderStatusClick.onOrderStatusClick(status[0])
                }
                if (lastSelectedPosition != absoluteAdapterPosition) {
                    setUnselectedForm()
                }
            }
        }

        fun setSelectedForm() {
            binding.apply {
                ordersStatus.background =itemView.resources.getDrawable(R.drawable.container_purple_corner_12)
                ordersStatus.setTextColor(itemView.resources.getColor(R.color.white))
            }
        }

        fun setUnselectedForm() {
            binding.apply {
                ordersStatus.background =itemView.resources.getDrawable(R.drawable.container_white_gray_stroke_12)
                ordersStatus.setTextColor(itemView.resources.getColor(R.color.gray_dark))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            OrderStatusItemBinding.inflate(
                LayoutInflater.from(
                    parent.context
                )
            )
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val status = differ.currentList[position]
        holder.bind(status)
    }

    private var onItemClickListener: ((Array<String>) -> Unit)? = null

    fun setOnItemClickListener(listener: (Array<String>) -> Unit) {
        onItemClickListener = listener
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }


    private val differCallback = object : DiffUtil.ItemCallback<Array<String>>() {
        override fun areItemsTheSame(oldItem: Array<String>, newItem: Array<String>): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Array<String>, newItem: Array<String>): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)
}