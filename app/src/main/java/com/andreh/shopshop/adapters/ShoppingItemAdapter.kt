package com.andreh.shopshop.adapters

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.andreh.shopshop.R
import com.andreh.shopshop.database.entities.ShoppingItem
import com.andreh.shopshop.databinding.ShoppingitemItemBinding
import com.andreh.shopshop.databinding.ShoppingitemaddItemBinding
import android.text.Editable
import android.text.TextWatcher
import java.util.*

class ShoppingItemAdapter(
    val data: List<ShoppingItem>,
    var addItemListener: (() -> Unit)?,
    val updateItem: (position: Int, item: ShoppingItem?) -> Unit,
) : RecyclerView.Adapter<ShoppingItemAdapter.ViewHolder>() {

    private val TYPE_REGULAR = 0
    private val TYPE_ADD = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_ADD -> ViewHolder(
                ShoppingitemaddItemBinding.inflate(
                    layoutInflater,
                    parent,
                    false
                )
            )
            else -> ViewHolder(ShoppingitemItemBinding.inflate(layoutInflater, parent, false))
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (holder.itemViewType) {
            TYPE_ADD -> {
                holder.addBinding!!.root.setOnClickListener { addItemListener?.invoke() }
            }
            else -> {
                with(data[position]) {
                    with(holder.regularBinding!!) {
                        editText.setText(name)
                        checkbox.isChecked = checked
                        amountText.text = amount.toString()

                        checkbox.setOnCheckedChangeListener { _, check ->
                            if (check) {
                                editText.paintFlags = editText.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                                editText.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.textColorDisabled))
                            }
                            else {
                                editText.paintFlags = editText.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                                editText.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.textColor))
                            }
                            checked = check
                            holder.updateItemDb()
                        }

                        editText.addTextChangedListener(
                            object : TextWatcher {
                                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
                                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

                                private var timer: Timer = Timer()
                                private val DELAY: Long = 1000
                                override fun afterTextChanged(s: Editable) {
                                    timer.cancel()
                                    timer = Timer()
                                    timer.schedule(
                                        object : TimerTask() {
                                            override fun run() {
                                                name = editText.text.toString()
                                                holder.updateItemDb()
                                            }
                                        },
                                        DELAY
                                    )
                                }
                            }
                        )

                        btnRemove.setOnClickListener {
                            if (amount > 0) {
                                amount--
                                amountText.text = amount.toString()
                                holder.updateItemDb()
                            }
                        }

                        btnAdd.setOnClickListener {
                            amount++
                            amountText.text = amount.toString()
                            holder.updateItemDb()
                        }

                        btnDelete.setOnClickListener {
                            holder.deleteItemDb()
                        }
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int = data.size + 1

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            data.size -> TYPE_ADD
            else -> TYPE_REGULAR
        }
    }

    inner class ViewHolder : RecyclerView.ViewHolder {
        var addBinding: ShoppingitemaddItemBinding? = null
        var regularBinding: ShoppingitemItemBinding? = null

        constructor(binding: ShoppingitemaddItemBinding) : super(binding.root) {
            addBinding = binding
        }

        constructor(binding: ShoppingitemItemBinding) : super(binding.root) {
            regularBinding = binding
        }

        fun updateItemDb() {
            updateItem(adapterPosition, data[adapterPosition])
        }

        fun deleteItemDb() {
            updateItem(adapterPosition, null)
        }
    }

}