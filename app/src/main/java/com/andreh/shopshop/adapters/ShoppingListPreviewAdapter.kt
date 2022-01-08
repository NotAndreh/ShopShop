package com.andreh.shopshop.adapters

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.andreh.shopshop.R
import com.andreh.shopshop.database.entities.ShoppingItem
import com.andreh.shopshop.databinding.ShoppinglistpreviewItemBinding

class ShoppingListPreviewAdapter(
    var data: List<ShoppingItem>
) : RecyclerView.Adapter<ShoppingListPreviewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ShoppinglistpreviewItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(data[position]) {
                if (name?.isNotBlank() == true) binding.text.text = name
                else binding.text.text = itemView.context.getText(R.string.unnamed_item)

                binding.checkbox.isChecked = checked
                if (checked) binding.text.paintFlags = binding.text.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            }
        }
    }

    override fun getItemCount(): Int = data.size

    inner class ViewHolder(val binding: ShoppinglistpreviewItemBinding) : RecyclerView.ViewHolder(binding.root)
}
