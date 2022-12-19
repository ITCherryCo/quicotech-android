package com.quico.tech.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.quico.tech.activity.RequestActivity
import com.quico.tech.databinding.ServicePhotoItemListBinding

class RequestPhotoRecyclerViewAdapter(onDeletePhoto:OnDeletePhoto) : RecyclerView.Adapter<RequestPhotoRecyclerViewAdapter.ItemViewHolder>() {
    private var onDeletePhoto = onDeletePhoto

    interface OnDeletePhoto {
        fun onDeletePhoto(position: Int)
    }

    inner class ItemViewHolder(private var binding: ServicePhotoItemListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(photo: RequestActivity.PhotoService) {
            binding.apply {
                deleteImage.visibility = View.VISIBLE
                image.setImageURI(photo.img)
                deleteImage.setOnClickListener {
                    onDeletePhoto.onDeletePhoto(absoluteAdapterPosition)
                    notifyDataSetChanged()
                }
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

    private var onItemClickListener: ((RequestActivity.PhotoService) -> Unit)? = null

    fun setOnItemClickListener(listener: (RequestActivity.PhotoService) -> Unit) {
        onItemClickListener = listener
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private val differCallback = object : DiffUtil.ItemCallback<RequestActivity.PhotoService>() {
        override fun areItemsTheSame(oldItem: RequestActivity.PhotoService, newItem: RequestActivity.PhotoService): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: RequestActivity.PhotoService, newItem: RequestActivity.PhotoService): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)
}