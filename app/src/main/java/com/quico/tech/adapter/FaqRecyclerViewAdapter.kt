package com.quico.tech.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.TranslateAnimation
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.quico.tech.databinding.FaqItemListBinding
import com.quico.tech.model.FAQ


class FaqRecyclerViewAdapter : RecyclerView.Adapter<FaqRecyclerViewAdapter.ItemViewHolder>() {

   inner class ItemViewHolder(private var binding: FaqItemListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(faq: FAQ) {
            binding.apply {
                title.text = faq.title
                description.text = faq.description

                titleContainer.setOnClickListener {
                    faq.isDescriptionVisible = !faq.isDescriptionVisible
                    notifyDataSetChanged()
                }

                if (faq.isDescriptionVisible) {
                    description.visibility = View.VISIBLE
                    arrow.rotation=90f
                }
                else {
                    description.visibility = View.GONE
                    arrow.rotation=0f
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            FaqItemListBinding.inflate(
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

    private var onItemClickListener: ((FAQ) -> Unit)? = null

    fun setOnItemClickListener(listener: (FAQ) -> Unit) {
        onItemClickListener = listener
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }


    private val differCallback = object : DiffUtil.ItemCallback<FAQ>() {
        override fun areItemsTheSame(oldItem: FAQ, newItem: FAQ): Boolean {
            return oldItem.title == newItem.title
        }

        override fun areContentsTheSame(oldItem: FAQ, newItem: FAQ): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)
}