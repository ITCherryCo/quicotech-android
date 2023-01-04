package com.quico.tech.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.VideoView
import androidx.annotation.RequiresApi
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.quico.tech.R

class ProductImageAdapter(
    var images: ArrayList<String>
) :
    RecyclerView.Adapter<ProductImageAdapter.SliderViewHolder>() {
    var imageView: ImageView? = null


    inner class SliderViewHolder internal constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        init {
            imageView = itemView.findViewById(R.id.image_view)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SliderViewHolder {
        return SliderViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.product_image, parent, false)
        )
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onBindViewHolder(
        holder: SliderViewHolder,
        position: Int
    ) {

        images?.let {  images->

            Glide
                .with(holder.itemView.context)
                .load(images.get(position))
                .fitCenter()
                .into(imageView!!)
        }

    }

    override fun getItemCount(): Int {
        return images!!.size
    }


}
