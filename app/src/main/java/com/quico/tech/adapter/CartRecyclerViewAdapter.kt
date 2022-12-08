package com.quico.tech.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.quico.tech.R
import com.quico.tech.data.Constant
import com.quico.tech.databinding.CartItemListBinding
import com.quico.tech.model.Item
import com.quico.tech.viewmodel.SharedViewModel
import kotlin.math.min

class CartRecyclerViewAdapter (val viewModel: SharedViewModel): RecyclerView.Adapter<CartRecyclerViewAdapter.ItemViewHolder>() {

  inner  class ItemViewHolder(private var binding: CartItemListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Item) {
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
        val item = differ.currentList[position]
        holder.bind(item)
    }

    private var onItemClickListener: ((Item) -> Unit)? = null

    fun setOnItemClickListener(listener: (Item) -> Unit) {
        onItemClickListener = listener
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }


    private val differCallback = object : DiffUtil.ItemCallback<Item>() {
        override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)
}