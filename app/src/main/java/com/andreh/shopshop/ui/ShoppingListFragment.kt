package com.andreh.shopshop.ui

import android.content.Context.INPUT_METHOD_SERVICE
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Slide
import com.andreh.shopshop.R
import com.andreh.shopshop.adapters.ShoppingItemAdapter
import com.andreh.shopshop.database.ListDatabase
import com.andreh.shopshop.database.entities.ShoppingList
import com.andreh.shopshop.database.entities.ShoppingItem
import com.andreh.shopshop.databinding.FragmentShoppingListBinding
import com.andreh.shopshop.databinding.RenameDialogBinding
import com.andreh.shopshop.extensions.ioLaunch
import com.andreh.shopshop.extensions.themeColor
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.transition.MaterialContainerTransform

class ShoppingListFragment : Fragment() {
    private var _binding: FragmentShoppingListBinding? = null
    private val binding get() = _binding!!
    private var hasStarted = false

    private val args: ShoppingListFragmentArgs by navArgs()
    private val argShoppingList: ShoppingList by lazy(LazyThreadSafetyMode.NONE) { args.shoppingList }
    private var dataTitle: String? = null
    private lateinit var dataList: MutableList<ShoppingItem>

    private lateinit var recyclerView: RecyclerView

    private val db by lazy { ListDatabase.getDatabase(requireContext()) }
    private val dao by lazy { db.shoppingListDao() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (_binding == null)
            _binding = FragmentShoppingListBinding.inflate(layoutInflater, container, false)

        sharedElementEnterTransition = MaterialContainerTransform().apply {
            duration = 300L
            scrimColor = Color.TRANSPARENT
            setAllContainerColors(requireContext().themeColor(R.attr.colorSurface))
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (hasStarted) return
        hasStarted = true

        recyclerView = binding.recyclerView

        dataTitle = argShoppingList.name
        dataList = argShoppingList.list.toMutableList()

        binding.collapsingToolbar.title = dataTitle ?: getString(R.string.unnamed_list)
        binding.toolbar.apply {
            setNavigationOnClickListener { findNavController().navigateUp() }
            inflateMenu(R.menu.shoppinglist_menu)
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.action_delete -> {
                        ioLaunch { dao.delete(argShoppingList) }
                        returnToHome()
                    }
                }
                false
            }
        }

        binding.collapsingToolbar.setOnClickListener {
            val v = RenameDialogBinding.inflate(layoutInflater)
            val d = MaterialAlertDialogBuilder(requireContext())
                .setView(v.root)
                .show()

            showSoftKeyboard(v.editText)

            v.btn.setOnClickListener {
                dataTitle = v.editText.editText?.text.toString()
                binding.collapsingToolbar.title = dataTitle
                d.dismiss()
                ioLaunch {
                    dao.update(argShoppingList.copy(name = dataTitle, list = dataList))
                }
            }
        }

        recyclerView.apply {
            adapter = ShoppingItemAdapter(dataList, {
                dataList.add(ShoppingItem(dataList.size, "", 1, false))
                adapter?.notifyItemInserted(dataList.size-1)
                ioLaunch {
                    dao.update(argShoppingList.copy(name = dataTitle, list = dataList))
                }
            }, { pos, item ->
                if (item != null) {
                    dataList[pos] = item
                } else {
                    dataList.removeAt(pos)
                    adapter?.notifyItemRemoved(pos)
                }
                ioLaunch {
                    dao.update(argShoppingList.copy(name = dataTitle, list = dataList))
                }
            })
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }
    }

    private fun showSoftKeyboard(view: View) {
        if (view.requestFocus()) {
            val imm = requireActivity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    private fun returnToHome() {
        sharedElementReturnTransition = null
        returnTransition = Slide().apply {
            duration = 300L
            slideEdge = Gravity.BOTTOM
        }
        findNavController().navigateUp()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}