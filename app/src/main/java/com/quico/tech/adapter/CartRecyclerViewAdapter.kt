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

class CartRecyclerViewAdapter(val viewModel: SharedViewModel) :
    RecyclerView.Adapter<CartRecyclerViewAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(private var binding: CartItemListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {
            var quantity = product.quantity!! //product quantity
            binding.apply {

                name.text = product.name
                qty.text = product.quantity.toString()
                var final_price = 0.0
                //var total_price=0.0
                if (product.is_vip_charge_product) {
                    qtyContainer.visibility = View.GONE
                    price.text = "$ ${product.regular_price}"
                    totalPrice.text = "${
                        viewModel.getLangResources().getString(R.string.total)
                    }: $${product.regular_price}"
                    coverImage.setImageResource(R.drawable.subscription)
                } else {
                    if (!product.in_stock!!) {
                        outOfStock.visibility = View.VISIBLE
                        outOfStock.text =
                            viewModel.getLangResources().getString(R.string.out_of_stock_msg)
                        price.visibility = View.GONE
                        totalPrice.setTextColor(itemView.resources.getColor(R.color.gray_dark))
                        totalPrice.alpha = 0.5f
                    } else if (product.quantity > product.quantity_available!!) {
                        outOfStock.visibility = View.VISIBLE
                        outOfStock.text = viewModel.getLangResources().getString(
                            R.string.available_qty_msg,
                            product.quantity_available.toString()
                        )
                        price.visibility = View.GONE
                        totalPrice.setTextColor(itemView.resources.getColor(R.color.gray_dark))
                        totalPrice.alpha = 0.5f
                    }

                    viewModel.user?.let { user ->
                        if (user.is_vip || viewModel.vip_subsription) {
                            if (product.is_vip || product.is_on_sale)
                                final_price = product.new_price
                            else
                                final_price = product.regular_price
                        } else {
                            if (product.is_on_sale)
                                final_price = product.new_price
                            else
                                final_price = product.regular_price
                        }
                    }

                    price.text = "$ ${final_price}"
                    totalPrice.text = "${
                        viewModel.getLangResources().getString(R.string.total)
                    }: $${final_price * quantity}"

                    if (viewModel.getLanguage().equals(Constant.AR)) {
                        plus.scaleX = -1f
                        minus.scaleX = -1f
                    }


                    product?.image.let {
                        Glide.with(itemView.context)
                            .load(it)
                            //.placeholder(R.drawable.placeholder)
                            .error(R.drawable.empty_item)
                            .fitCenter()
                            .into(coverImage)
                    }

                    plus.setOnClickListener {
                        // check for available qty
                        if (quantity + 1 > product.quantity_available!!) {
                            Common.setUpAlert(
                                itemView.context, false,
                                viewModel.getLangResources()
                                    .getString(R.string.quantity),
                                viewModel.getLangResources()
                                    .getString(R.string.available_qty_msg),
                                viewModel.getLangResources().getString(R.string.ok),
                                null
                            )
                        } else {
                            quantity++
                            qty.text = "$quantity"
                            updateQty(product.id, quantity)
                        }
                    }

                    minus.setOnClickListener {
                        // check for available qty
                        if (quantity > 1) {
                            quantity--
                            updateQty(product.id, quantity)
                        } else
                            deleteItem(product.is_vip_charge_product, product.id)
                        // qty.text = "$quantity"
                    }
                }

                deleteImage.setOnClickListener {
                    deleteItem(product.is_vip_charge_product, product.id)
                }
            }
        }

        private fun updateQty(product_id: Int, quantity: Int) {

            val params = ProductBodyParameters(
                ProductParams(
                    product_id,
                    quantity
                )
            )

            Common.setUpProgressDialog(itemView.context)
            viewModel.addToCart(true, params,
                object : SharedViewModel.ResponseStandard {
                    override fun onSuccess(
                        success: Boolean,
                        resultTitle: String,
                        message: String
                    ) {
                        // later add progress bar to view
                        binding.qty.text = "${params.params.quantity}"
                        Common.cancelProgressDialog()

                    }

                    override fun onFailure(
                        success: Boolean,
                        resultTitle: String,
                        message: String
                    ) {
                        Common.cancelProgressDialog()
                        if (message.equals(itemView.resources.getString(R.string.session_expired))) {
                            viewModel.resetSession()
                            Common.setUpSessionProgressDialog(itemView.context)
                        } else
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


        private fun deleteItem(is_vip_charge_product: Boolean, product_id: Int) {
            binding.apply {
                Common.setUpChoicesAlert(itemView.context,
                    viewModel.getLangResources().getString(R.string.delete_item),
                    viewModel.getLangResources().getString(R.string.sure_delete_item),
                    viewModel.getLangResources().getString(R.string.no),
                    viewModel.getLangResources().getString(R.string.yes),
                    object : Common.ResponseChoices {
                        override fun onConfirm() {
                            val params = ProductBodyParameters(
                                ProductParams(
                                    product_id,
                                )
                            )

                            progressBar.visibility = View.VISIBLE
                            viewModel.removeFromCart(is_vip_charge_product, params,
                                object : SharedViewModel.ResponseStandard {
                                    override fun onSuccess(
                                        success: Boolean,
                                        resultTitle: String,
                                        message: String
                                    ) {
                                        // later add progress bar to view
                                        progressBar.visibility = View.GONE

                                        /*     Toast.makeText(
                                             itemView.context,
                                             message,
                                             Toast.LENGTH_LONG
                                         ).show()*/
                                    }

                                    override fun onFailure(
                                        success: Boolean,
                                        resultTitle: String,
                                        message: String
                                    ) {
                                        progressBar.visibility = View.GONE
                                        if (message.equals(itemView.resources.getString(R.string.session_expired))) {
                                            viewModel.resetSession()
                                            Common.setUpSessionProgressDialog(itemView.context)
                                        } else
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

    fun getSelectedProduct():ArrayList<Product> {
        var selectedProducts = ArrayList<Product>()
        viewModel.user?.let { user ->
            differ.currentList.forEach { product ->

                if (user.is_vip || viewModel.vip_subsription) {
                    if (product.is_vip || product.is_on_sale)
                        selectedProducts.add(Product(product.id,product.name,product.quantity!!,product.new_price))
                    else
                        selectedProducts.add(Product(product.id,product.name,product.quantity!!,product.regular_price))
                } else {
                    if (product.is_on_sale)
                        selectedProducts.add(Product(product.id,product.name,product.quantity!!,product.new_price))
                    else
                        selectedProducts.add(Product(product.id,product.name,product.quantity!!,product.regular_price))
                }
            }
        }
        return selectedProducts
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