package com.quico.tech.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.quico.tech.R
import com.quico.tech.activity.LoginActivity
import com.quico.tech.activity.RequestActivity
import com.quico.tech.activity.ServiceListActivity
import com.quico.tech.data.Constant
import com.quico.tech.databinding.ServiceItemListBinding
import com.quico.tech.model.Service
import com.quico.tech.model.ServiceType
import com.quico.tech.utils.Common
import com.quico.tech.viewmodel.SharedViewModel

class ServiceRecyclerViewAdapter(val viewModel: SharedViewModel) :
    RecyclerView.Adapter<ServiceRecyclerViewAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(private var binding: ServiceItemListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(serviceType: ServiceType) {
            binding.apply {

                name.text = serviceType.type
                name.text = serviceType.type
                itemView.setOnClickListener {
                    if (serviceType.have_sub_service) {
                        if (viewModel.user!=null)
                        itemView.context.startActivity(
                            Intent(itemView.context, ServiceListActivity::class.java)
                                .putExtra(Constant.SERVICE_ID, serviceType.id)
                        )
                        else{
                            Common.setUpChoicesAlert(
                                itemView.context,
                                viewModel.getLangResources().getString(R.string.login),
                                viewModel.getLangResources()
                                    .getString(R.string.please_login),
                                viewModel.getLangResources().getString(R.string.cancel),
                                viewModel.getLangResources().getString(R.string.login),
                                object : Common.ResponseChoices {
                                    override fun onConfirm() {
                                        itemView.context.startActivity(Intent(itemView.context,LoginActivity::class.java))
                                    }

                                    override fun onCancel() {
                                    }
                                }
                            )
                        }
                    }
                    else
                        itemView.context.startActivity(
                            Intent(
                                itemView.context,
                                RequestActivity::class.java
                            )
                        )
                }
                if (serviceType.image.isNullOrEmpty()) {
                    Glide.with(itemView)
                        .load(serviceType.image)
                        //.placeholder(R.drawable.placeholder)
                        .error(R.drawable.smartphone)
                        .into(image)
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
        val serviceType = differ.currentList[position]
        holder.bind(serviceType)
    }

    private var onItemClickListener: ((ServiceType) -> Unit)? = null

    fun setOnItemClickListener(listener: (ServiceType) -> Unit) {
        onItemClickListener = listener
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private val differCallback = object : DiffUtil.ItemCallback<ServiceType>() {
        override fun areItemsTheSame(oldItem: ServiceType, newItem: ServiceType): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ServiceType, newItem: ServiceType): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)
}