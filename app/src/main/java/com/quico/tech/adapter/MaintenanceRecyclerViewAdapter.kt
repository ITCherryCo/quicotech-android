package com.quico.tech.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.quico.tech.R
import com.quico.tech.activity.ServiceListActivity
import com.quico.tech.data.Maintenance
import com.quico.tech.databinding.MaintenaceItemListBinding

class MaintenanceRecyclerViewAdapter : RecyclerView.Adapter<MaintenanceRecyclerViewAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(private var binding: MaintenaceItemListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(maintenance: Maintenance) {
            binding.apply {

                title.text = maintenance.title
                description.text = maintenance.description
                image.setImageResource(maintenance.image)

                when(maintenance.id){
                    1->verticalLine.setBackgroundColor(itemView.resources.getColor(R.color.purple_quico))
                    2->verticalLine.setBackgroundColor (itemView.resources.getColor(R.color.green))
                    3->verticalLine.setBackgroundColor(itemView.resources.getColor(R.color.orange))
                    4->verticalLine.setBackgroundColor(itemView.resources.getColor(R.color.red_dark))
                }

                itemView.setOnClickListener {
                    itemView.context.startActivity(Intent(itemView.context, ServiceListActivity::class.java))
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            MaintenaceItemListBinding.inflate(
                LayoutInflater.from(
                    parent.context
                )
            )
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val maintenance = differ.currentList[position]
        holder.bind(maintenance)
    }

    private var onItemClickListener: ((Maintenance) -> Unit)? = null

    fun setOnItemClickListener(listener: (Maintenance) -> Unit) {
        onItemClickListener = listener
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }


    private val differCallback = object : DiffUtil.ItemCallback<Maintenance>() {
        override fun areItemsTheSame(oldItem: Maintenance, newItem: Maintenance): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Maintenance, newItem: Maintenance): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)
}