package com.quico.tech.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.quico.tech.R
import com.quico.tech.data.Constant
import com.quico.tech.databinding.CartItemListBinding
import com.quico.tech.model.Item
import com.quico.tech.model.Product
import com.quico.tech.viewmodel.SharedViewModel

class CartRecyclerViewAdapter (val viewModel: SharedViewModel): RecyclerView.Adapter<CartRecyclerViewAdapter.ItemViewHolder>() {

  inner  class ItemViewHolder(private var binding: CartItemListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {
            binding.apply {

                if (viewModel.getLanguage().equals(Constant.AR)){
                    plus.scaleX = -1f
                    minus.scaleX = -1f
                }

                if (absoluteAdapterPosition == 2) {
                    price.visibility = View.GONE
                    outOfStock.visibility = View.VISIBLE
                    totalPrice.setTextColor(itemView.resources.getColor(R.color.gray_dark))
                    totalPrice.alpha= 0.5f
                }

                if (absoluteAdapterPosition == 4)
                    grayLine.visibility = View.INVISIBLE
            }
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            CartItemListBinding.inflate(
                LayoutInflater.from(
                    parent.context
                )
            )
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val product = differ.currentList[position]
        holder.bind(product)
    }

    private var onItemClickListener: ((Product) -> Unit)? = null

    fun setOnItemClickListener(listener: (Product) -> Unit) {
        onItemClickListener = listener
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }


    private val differCallback = object : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)
}