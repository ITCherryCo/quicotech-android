package com.quico.tech.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.quico.tech.R
import com.quico.tech.activity.CheckoutActivity
import com.quico.tech.data.Constant.ORDER_TYPE
import com.quico.tech.data.Constant.DELIVERY_ORDERS
import com.quico.tech.data.Constant.ORDER_ID
import com.quico.tech.data.Constant.SERVICE_ORDERS
import com.quico.tech.data.Constant.TRACKING_ON
import com.quico.tech.databinding.OrderItemListBinding
import com.quico.tech.model.Order
import com.quico.tech.viewmodel.SharedViewModel

class OrderRecyclerViewAdapter(val is_service: Boolean, val viewModel: SharedViewModel) :
    RecyclerView.Adapter<OrderRecyclerViewAdapter.ItemViewHolder>() {
    inner class ItemViewHolder(private var binding: OrderItemListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("ResourceAsColor")
        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        fun bind(order: Order) {
            binding.apply {

                //orderNumber.text = itemView.resources.getString(R.string.order_number,"12")
                orderNumber.text = itemView.resources.getString(R.string.order_number, order.order_nb)
                if (is_service)
                    total.visibility = View.GONE
                else {
                    total.visibility = View.VISIBLE
                    total.text = "$ ${order.total_price}"
                }


                container.setOnClickListener {
                    itemView.context.startActivity(
                        Intent(itemView.context, CheckoutActivity::class.java)
                            //.putExtra(TRACKING_ON, if (order.status.equals(DELIVERED)||order.status.equals(CANCELED))true else false)
                            .putExtra(TRACKING_ON,true)
                            .putExtra(ORDER_TYPE, if (is_service) SERVICE_ORDERS else DELIVERY_ORDERS)
                            .putExtra(ORDER_ID, order.id)
                    )
                }

               // orderStatus.text = itemView.resources.getString(R.string.track_order)
               // orderStatus.text = OrderStatus.getOrderStatus(viewModel.getLangResources(),order.status)
                /*orderStatus.backgroundTintList = itemView.resources.getColorStateList(
                    OrderStatus.getOrderStatusColor(
                        itemView.resources,
                        order.status
                    )
                )*/

               /* when (absoluteAdapterPosition) {
                    0 -> {
                        orderStatus.text = itemView.resources.getString(R.string.track_order)
                        orderStatus.backgroundTintList = itemView.resources.getColorStateList(
                            OrderStatus.getOrderStatusColor(
                                itemView.resources,
                                TRACK_ORDER
                            )
                        )
                        container.setOnClickListener {
                            itemView.context.startActivity(
                                Intent(itemView.context, CheckoutActivity::class.java)
                                    .putExtra(TRACKING_ON, true)
                                    .putExtra(CHECKOUT_TYPE, DELIVERY_ORDERS)
                            )
                        }
                    }*/

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