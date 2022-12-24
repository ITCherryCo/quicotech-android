package com.quico.tech.adapter

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.quico.tech.activity.RequestActivity
import com.quico.tech.databinding.RepairPhotoItemListBinding
import com.quico.tech.databinding.ServicePhotoItemListBinding

class RequestPhotoRecyclerViewAdapter(onAddPhoto:OnAddPhoto) : RecyclerView.Adapter<RequestPhotoRecyclerViewAdapter.ItemViewHolder>() {
    private var onAddPhoto = onAddPhoto

    interface OnAddPhoto {
        fun onAddPhoto(position: Int,currentPhoto: RequestActivity.PhotoService)
    }

    inner class ItemViewHolder(private var binding: RepairPhotoItemListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(photo: RequestActivity.PhotoService) {
            binding.apply {
                if (photo.img == null) {
                    deleteImage.visibility = View.GONE
                    cardView.visibility = View.GONE
                    add.visibility = View.VISIBLE

                } else {
                    Log.d("CURRENT_PHOTO","not null")
                    deleteImage.visibility = View.VISIBLE
                    cardView.visibility = View.VISIBLE
                    add.visibility = View.GONE

                    image.setImageURI(photo.img!!)
                }

                deleteImage.setOnClickListener {
                    //onAddDeletePhoto.onAddDeletePhoto(absoluteAdapterPosition,photo)
                    photo.img = null
                    notifyDataSetChanged()
                }

                add.setOnClickListener {

                   // if (photo.img == null)
                        onAddPhoto.onAddPhoto(absoluteAdapterPosition,photo)
                   // notifyDataSetChanged()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            RepairPhotoItemListBinding.inflate(
                LayoutInflater.from(
                    parent.context
                )
            )
        )
    }

    fun submitlist(){
        notifyDataSetChanged()
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