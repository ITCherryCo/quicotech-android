package com.quico.tech.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.quico.tech.R
import com.quico.tech.data.Constant
import com.quico.tech.databinding.CartItemListBinding
import com.quico.tech.model.*
import com.quico.tech.utils.Common
import com.quico.tech.viewmodel.SharedViewModel

class CartRecyclerViewAdapter (val viewModel: SharedViewModel): RecyclerView.Adapter<CartRecyclerViewAdapter.ItemViewHolder>() {

  inner  class ItemViewHolder(private var binding: CartItemListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {
            binding.apply {

                name.text = product.name
                price.text = "$ ${product.new_price}"

                if (viewModel.getLanguage().equals(Constant.AR)) {
                    plus.scaleX = -1f
                    minus.scaleX = -1f
                }

                if (absoluteAdapterPosition == 2) {
                    price.visibility = View.GONE
                    outOfStock.visibility = View.VISIBLE
                    totalPrice.setTextColor(itemView.resources.getColor(R.color.gray_dark))
                    totalPrice.alpha = 0.5f
                }

                if (absoluteAdapterPosition == 4)
                    grayLine.visibility = View.INVISIBLE

                product?.image.let {
                    Glide.with(itemView.context)
                        .load(it)
                        //.placeholder(R.drawable.placeholder)
                        .error(R.drawable.empty_item)
                        .fitCenter()
                        .into(coverImage)
                }


                deleteImage.setOnClickListener {
                    Common.setUpChoicesAlert(itemView.context,
                        viewModel.getLangResources().getString(R.string.delete_item),
                        viewModel.getLangResources().getString(R.string.sure_delete_item),
                        viewModel.getLangResources().getString(R.string.no),
                        viewModel.getLangResources().getString(R.string.yes),
                        object : Common.ResponseChoices {
                            override fun onConfirm() {
                                val params = ProductBodyParameters(
                                    ProductParams(
                                        product.id
                                    )
                                )

                                progressBar.visibility = View.VISIBLE
                                viewModel.removeFromCart(params,
                                    object : SharedViewModel.ResponseStandard {
                                        override fun onSuccess(
                                            success: Boolean,
                                            resultTitle: String,
                                            message: String
                                        ) {
                                            // later add progress bar to view
                                            progressBar.visibility = View.GONE
                                            Toast.makeText(
                                                itemView.context,
                                                message,
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }

                                        override fun onFailure(
                                            success: Boolean,
                                            resultTitle: String,
                                            message: String
                                        ) {
                                            progressBar.visibility = View.GONE
                                            Common.setUpAlert(
                                                itemView.context, false,
                                                viewModel.getLangResources()
                                                    .getString(R.string.error),
                                                message,
                                                viewModel.getLangResources().getString(R.string.ok),
                                                null
                                            )
                                        }
                                    })
                            }

                            override fun onCancel() {

                            }

                        })
                }
            }
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            CartItemListBinding.inflate(
                LayoutInflater.from(
                    parent.context
                )
            )
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val product = differ.currentList[position]
        holder.bind(product)
    }

    private var onItemClickListener: ((Product) -> Unit)? = null

    fun setOnItemClickListener(listener: (Product) -> Unit) {
        onItemClickListener = listener
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }


    private val differCallback = object : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)
}