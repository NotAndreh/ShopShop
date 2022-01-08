package com.andreh.shopshop.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.andreh.shopshop.R
import com.andreh.shopshop.database.entities.ShoppingList
import com.andreh.shopshop.databinding.ShoppinglistItemBinding

class ShoppingListAdapter(
    var data: List<ShoppingList>,
    var listener: (shoppingList: ShoppingList, view: View) -> Unit,
    var longListener: (shoppingList: ShoppingList) -> Unit,
) : RecyclerView.Adapter<ShoppingListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ShoppinglistItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(data[position]) {
                binding.title.text = name ?: itemView.context.getText(R.string.unnamed_list)
                binding.root.transitionName = itemView.context.getString(R.string.shopping_list_transition_name) + id.toString()

                if (list.size > 3) {
                    binding.other.visibility = View.VISIBLE
                } else binding.other.visibility = View.GONE

                if (list.isEmpty()) {
                    binding.empty.visibility = View.VISIBLE
                } else binding.empty.visibility = View.GONE

                binding.recyclerView.apply {
                    adapter = ShoppingListPreviewAdapter(if (list.size > 3) list.subList(0, 3) else list)
                    layoutManager = LinearLayoutManager(itemView.context, LinearLayoutManager.VERTICAL, false)
                    isNestedScrollingEnabled = false
                    suppressLayout(true)
                }
            }
        }
    }

    override fun getItemCount(): Int = data.size

    inner class ViewHolder(val binding: ShoppinglistItemBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener { listener(data[adapterPosition], binding.root) }
            binding.root.setOnLongClickListener { longListener(data[adapterPosition]); true }
        }
    }
}