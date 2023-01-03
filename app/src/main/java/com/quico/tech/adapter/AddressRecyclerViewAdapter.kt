package com.quico.tech.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.quico.tech.R
import com.quico.tech.activity.AddressActivity
import com.quico.tech.data.Constant
import com.quico.tech.databinding.AddressItemListBinding
import com.quico.tech.model.Address
import com.quico.tech.model.ID
import com.quico.tech.model.IDBodyParameters
import com.quico.tech.utils.Common
import com.quico.tech.viewmodel.SharedViewModel

class AddressRecyclerViewAdapter(val viewModel: SharedViewModel) :
    RecyclerView.Adapter<AddressRecyclerViewAdapter.ItemViewHolder>() {
    private var lastSelectedPosition = -1
    private var defaultSelectedPosition = -1

    inner class ItemViewHolder(private var binding: AddressItemListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(address: Address) {
            binding.apply {
                var addressValue = ""

                username.text = "${address.name}, ${address.mobile}"
                addressValue = "${address.country}, ${address.city}, ${address.street}"
                if (address.street2.isNotEmpty())
                    addressValue = "${addressValue} , ${address.street2}"

                addressValue = "${addressValue}, ${address.zip}"

                edit.text = viewModel.getLangResources().getString(R.string.edit)
                delete.text = viewModel.getLangResources().getString(R.string.delete)
                addressText.text = addressValue

                edit.setOnClickListener {
                    Constant.TEMPORAR_ADDRESS = address
                    itemView.context.startActivity(
                        Intent(itemView.context, AddressActivity::class.java)
                            .putExtra(Constant.ADDRESS, address))
                }

                delete.setOnClickListener {
                    Common.setUpChoicesAlert(itemView.context,
                        viewModel.getLangResources().getString(R.string.delete_address),
                        viewModel.getLangResources().getString(R.string.sure_delete_address),
                        viewModel.getLangResources().getString(R.string.no),
                        viewModel.getLangResources().getString(R.string.yes),
                        object : Common.ResponseChoices {
                            override fun onConfirm() {
                                val params = IDBodyParameters(
                                    ID(
                                        address.id
                                    )
                                )
                                progressBar.visibility = View.VISIBLE

                                viewModel.deleteAddress(params,
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
            AddressItemListBinding.inflate(
                LayoutInflater.from(
                    parent.context
                )
            )
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val address = differ.currentList[position]
        holder.bind(address)
    }

    private var onItemClickListener: ((Address) -> Unit)? = null

    fun setOnItemClickListener(listener: (Address) -> Unit) {
        onItemClickListener = listener
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }


    private val differCallback = object : DiffUtil.ItemCallback<Address>() {
        override fun areItemsTheSame(oldItem: Address, newItem: Address): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Address, newItem: Address): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)
}