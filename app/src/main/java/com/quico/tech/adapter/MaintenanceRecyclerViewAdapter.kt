package com.quico.tech.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.quico.tech.R
import com.quico.tech.activity.ServiceListActivity
import com.quico.tech.data.Constant.SERVICE_ID
import com.quico.tech.databinding.MaintenaceItemListBinding
import com.quico.tech.model.Service

class MaintenanceRecyclerViewAdapter : RecyclerView.Adapter<MaintenanceRecyclerViewAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(private var binding: MaintenaceItemListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(service: Service) {
            binding.apply {

                title.text = service.name
                description.text = service.description
               // image.setImageResource(service.image)

                when(absoluteAdapterPosition){
                    0-> {
                        verticalLine.setBackgroundColor(itemView.resources.getColor(R.color.color_primary_purple))
                        image.setImageResource(R.drawable.repair2)
                    }
                    1-> {
                        verticalLine.setBackgroundColor(itemView.resources.getColor(R.color.green))
                        image.setImageResource(R.drawable.recovery)
                    }
                    2-> {
                        verticalLine.setBackgroundColor(itemView.resources.getColor(R.color.orange))
                        image.setImageResource(R.drawable.technical)
                    }

                    3-> {
                        verticalLine.setBackgroundColor(itemView.resources.getColor(R.color.red_dark))
                        image.setImageResource(R.drawable.repair2)
                    }
                }

                itemView.setOnClickListener {
                    itemView.context.startActivity(Intent(itemView.context, ServiceListActivity::class.java)
                        .putExtra(SERVICE_ID,service.id))

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