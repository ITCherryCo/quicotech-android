package com.quico.tech.adapter

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.quico.tech.R
import com.quico.tech.data.Constant.CANCELED
import com.quico.tech.data.Constant.DELIVERED
import com.quico.tech.data.Constant.TRACK_ORDER
import com.quico.tech.databinding.OrderItemListBinding
import com.quico.tech.model.Address
import com.quico.tech.model.Order
import com.quico.tech.model.OrderStatus

class OrderRecyclerViewAdapter : RecyclerView.Adapter<OrderRecyclerViewAdapter.ItemViewHolder>() {
    inner class ItemViewHolder(private var binding: OrderItemListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("ResourceAsColor")
        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        fun bind(order: Order) {
            binding.apply {

                //orderNumber.text = itemView.resources.getString(R.string.order_number,"12")
                orderNumber.text = itemView.resources.getString(R.string.order_number,"123")
                total.text = "$100"

                when (absoluteAdapterPosition) {
                    1 -> {
                        orderStatus.backgroundTintList = itemView.resources.getColorStateList(
                            OrderStatus.getOrderStatusColor(
                                itemView.resources,
                                TRACK_ORDER
                            )
                        )
                    }
                    2 -> {
                        orderStatus.text = itemView.resources.getString(R.string.canceled)

                        orderStatus.backgroundTintList = itemView.resources.getColorStateList(
                            OrderStatus.getOrderStatusColor(
                                itemView.resources,
                                CANCELED
                            )
                        )
                    }
                    3 -> {
                        orderStatus.text = itemView.resources.getString(R.string.delivered)
                        orderStatus.backgroundTintList = itemView.resources.getColorStateList(
                            OrderStatus.getOrderStatusColor(
                                itemView.resources,
                                DELIVERED
                            )
                        )
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            OrderItemListBinding.inflate(
                LayoutInflater.from(
                    parent.context
                )
            )
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val order = differ.currentList[position]
        holder.bind(order)
    }

    private var onItemClickListener: ((Order) -> Unit)? = null

    fun setOnItemClickListener(listener: (Order) -> Unit) {
        onItemClickListener = listener
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }


    private val differCallback = object : DiffUtil.ItemCallback<Order>() {
        override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)
}