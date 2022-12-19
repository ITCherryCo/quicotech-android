package com.quico.tech.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.quico.tech.R
import com.quico.tech.activity.RequestActivity
import com.quico.tech.databinding.ServiceItemListBinding
import com.quico.tech.model.Service

class ServiceRecyclerViewAdapter() : RecyclerView.Adapter<ServiceRecyclerViewAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(private var binding: ServiceItemListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(service: Service) {
            binding.apply {
                when(absoluteAdapterPosition){
                    0->image.setImageResource(R.drawable.smartphone)
                    1->image.setImageResource(R.drawable.gaming)
                    2->image.setImageResource(R.drawable.smartphone)
                    3->image.setImageResource(R.drawable.gaming)
                }

                itemView.setOnClickListener {
                    itemView.context.startActivity(Intent(itemView.context, RequestActivity::class.java))
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            ServiceItemListBinding.inflate(
                LayoutInflater.from(
                    parent.context
                )
            )
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val service = differ.currentList[position]
        holder.bind(service)
    }

    private var onItemClickListener: ((Service) -> Unit)? = null

    fun setOnItemClickListener(listener: (Service) -> Unit) {
        onItemClickListener = listener
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }


    private val differCallback = object : DiffUtil.ItemCallback<Service>() {
        override fun areItemsTheSame(oldItem: Service, newItem: Service): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Service, newItem: Service): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)
}