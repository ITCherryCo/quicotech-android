package com.quico.tech.adapter

import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.quico.tech.R
import com.quico.tech.databinding.BrandItemSelectionBinding
import com.quico.tech.databinding.CategoryItemListBinding
import com.quico.tech.databinding.SubCategoryItemSelectionBinding
import com.quico.tech.model.Brand
import com.quico.tech.model.Category
import com.quico.tech.model.Product
import com.quico.tech.model.SubCategory

class SubCategoryRecyclerViewAdapter(onSubCategorySelect: OnSubCategorySelect?, onSubCategoryUnSelect: OnSubCategoryUnSelect?) : RecyclerView.Adapter<SubCategoryRecyclerViewAdapter.ItemViewHolder>() {
    private var lastSelectedPosition = -1
    private var defaultSelectedPosition = -1
    private var onSubCategorySelect = onSubCategorySelect
    private var onSubCategoryUnSelect = onSubCategoryUnSelect

    interface OnSubCategorySelect {
        fun onSubCategorySelect(subCategory: SubCategory?)
    }

    interface OnSubCategoryUnSelect {
        fun onSubCategoryUnSelect()
    }

    inner class ItemViewHolder(private var binding: SubCategoryItemSelectionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(subCategory: SubCategory) {
            binding.apply {

                subCatName.text = subCategory.name

                itemView.setOnClickListener {
                    lastSelectedPosition = absoluteAdapterPosition
                    notifyDataSetChanged()
                }


                if (lastSelectedPosition == absoluteAdapterPosition) {
                    setSelectedForm()
                    onSubCategorySelect!!.onSubCategorySelect(subCategory)
                }
                if (lastSelectedPosition != absoluteAdapterPosition) {
                    setUnselectedForm()
                    onSubCategoryUnSelect!!.onSubCategoryUnSelect()
                }
            }
        }

        fun setSelectedForm() {
            binding.apply {
                cardView.strokeColor = itemView.resources.getColor(R.color.color_primary_purple)
                subCatName.setBackgroundColor( itemView.resources.getColor(R.color.color_primary_purple))
                subCatName.setTextColor( itemView.resources.getColor(R.color.white))
                //cardView.strokeWidth = 6
            }
        }

        fun setUnselectedForm() {
            binding.apply {
                cardView.strokeColor = itemView.resources.getColor(R.color.input_field_hint)
                subCatName.setBackgroundColor( itemView.resources.getColor(R.color.white))
                subCatName.setTextColor( itemView.resources.getColor(R.color.black))
                //cardView.strokeWidth = 0
            }
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            SubCategoryItemSelectionBinding.inflate(
                LayoutInflater.from(
                    parent.context
                )
            )
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val subCategory = differ.currentList[position]
        holder.bind(subCategory)
    }

    private var onItemClickListener: ((SubCategory) -> Unit)? = null

    fun setOnItemClickListener(listener: (SubCategory) -> Unit) {
        onItemClickListener = listener
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private val differCallback = object : DiffUtil.ItemCallback<SubCategory>() {
        override fun areItemsTheSame(oldItem: SubCategory, newItem: SubCategory): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: SubCategory, newItem: SubCategory): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)
}