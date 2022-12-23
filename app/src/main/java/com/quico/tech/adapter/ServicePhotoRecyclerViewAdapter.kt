package com.quico.tech.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.quico.tech.databinding.ServicePhotoItemListBinding

class ServicePhotoRecyclerViewAdapter : RecyclerView.Adapter<ServicePhotoRecyclerViewAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(private var binding: ServicePhotoItemListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(photo: String) {
            binding.apply {

                Glide
                    .with(itemView.context)
                    .load(photo)
                    //.placeholder(R.drawable.gray_placeholder)
                    //.error(R.drawable.empty_image)
                    .fitCenter()
                    .into(image)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            ServicePhotoItemListBinding.inflate(
                LayoutInflater.from(
                    parent.context
                )
            )
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val photo = differ.currentList[position]
        holder.bind(photo)
    }

    private var onItemClickListener: ((String) -> Unit)? = null

    fun setOnItemClickListener(listener: (String) -> Unit) {
        onItemClickListener = listener
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }


    private val differCallback = object : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)
}