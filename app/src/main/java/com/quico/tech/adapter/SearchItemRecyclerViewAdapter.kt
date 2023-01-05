package com.quico.tech.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.quico.tech.R
import com.quico.tech.activity.CompareProductActivity

import com.quico.tech.databinding.SearchItemListBinding
import com.quico.tech.model.Product
import com.quico.tech.viewmodel.SharedViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class SearchItemRecyclerViewAdapter(val fragment_position:Int,private val lifecycleOwner: LifecycleOwner): RecyclerView.Adapter<SearchItemRecyclerViewAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(private var binding: SearchItemListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product) {
            binding.apply {

                name.text = product.name

                if (product.is_vip || product.is_on_sale) {

                    price.text = "$ ${product.new_price.toString()}"
                } else {
                    price.text = "$ ${product.regular_price.toString()}"
                }

                if (!product.images.isNullOrEmpty())
                {
                    Glide
                        .with(itemView.context)
                        .load(product.images[0])
                        .fitCenter()
                        .error(R.drawable.empty_item)
                        .into(image)
                }
                else
                    image.setImageResource(R.drawable.empty_item)

                itemView.setOnClickListener {
                   when(fragment_position){
                       1-> lifecycleOwner.lifecycleScope.launch {  CompareProductActivity._item_id_1.emit( product.id)}
                       2-> lifecycleOwner.lifecycleScope.launch {  CompareProductActivity._item_id_2.emit( product.id)}
                       //1-> viewModel.itemId1 = product.id
                       //2-> viewModel.itemId2 = product.id
                   }
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            SearchItemListBinding.inflate(
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

    private var onItemClickListener: ((Product) -> Unit)? = null

    fun setOnItemClickListener(listener: (Product) -> Unit) {
        onItemClickListener = listener
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private val differCallback = object : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)
}