package com.quico.tech.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.quico.tech.R
import com.quico.tech.activity.CheckoutActivity
import com.quico.tech.data.Constant.CANCELED
import com.quico.tech.data.Constant.CHECKOUT_TYPE
import com.quico.tech.data.Constant.DELIVERED
import com.quico.tech.data.Constant.ORDERS
import com.quico.tech.data.Constant.SERVICE
import com.quico.tech.data.Constant.TRACKING_ON
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
                    0 -> {
                        orderStatus.text = itemView.resources.getString(R.string.track_order)
                        orderStatus.backgroundTintList = itemView.resources.getColorStateList(
                            OrderStatus.getOrderStatusColor(
                                itemView.resources,
                                TRACK_ORDER
                            )
                        )
                        container.setOnClickListener {itemView.context.startActivity(
                            Intent(itemView.context, CheckoutActivity::class.java)
                                .putExtra(TRACKING_ON, true)
                                .putExtra(CHECKOUT_TYPE, ORDERS)
                        )
                        }
                    }

                    1 -> {
                        orderStatus.text = itemView.resources.getString(R.string.track_order)
                        orderStatus.backgroundTintList = itemView.resources.getColorStateList(
                            OrderStatus.getOrderStatusColor(
                                itemView.resources,
                                TRACK_ORDER
                            )
                        )
                        container.setOnClickListener {itemView.context.startActivity(
                            Intent(itemView.context, CheckoutActivity::class.java)
                                .putExtra(TRACKING_ON, true)
                                .putExtra(CHECKOUT_TYPE, ORDERS)
                        )
                        }
                    }
                    2 -> {
                        orderStatus.text = itemView.resources.getString(R.string.canceled)

                        orderStatus.backgroundTintList = itemView.resources.getColorStateList(
                            OrderStatus.getOrderStatusColor(
                                itemView.resources,
                                CANCELED
                            )
                        )

                        container.setOnClickListener {
                            itemView.context.startActivity(
                                Intent(itemView.context, CheckoutActivity::class.java)
                                    .putExtra(TRACKING_ON, true)
                                    .putExtra(CHECKOUT_TYPE, SERVICE)
                            )
                        }
                    }
                    3 -> {
                        orderStatus.text = itemView.resources.getString(R.string.delivered)
                        orderStatus.backgroundTintList = itemView.resources.getColorStateList(
                            OrderStatus.getOrderStatusColor(
                                itemView.resources,
                                DELIVERED
                            )
                        )

                        container.setOnClickListener{
                            itemView.context.startActivity(
                                Intent(itemView.context, CheckoutActivity::class.java)
                                    .putExtra(TRACKING_ON, false)
                                    .putExtra(CHECKOUT_TYPE, ORDERS)
                            )
                        }
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