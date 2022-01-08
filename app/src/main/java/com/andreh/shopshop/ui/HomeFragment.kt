package com.andreh.shopshop.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.SharedElementCallback
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.andreh.shopshop.R
import com.andreh.shopshop.adapters.ShoppingListAdapter
import com.andreh.shopshop.database.ListDatabase
import com.andreh.shopshop.database.entities.ShoppingList
import com.andreh.shopshop.databinding.FragmentHomeBinding
import com.andreh.shopshop.extensions.ioLaunch
import com.andreh.shopshop.extensions.runWhenReady
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.transition.MaterialElevationScale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var hasBeenStarted = false

    private lateinit var recyclerView: RecyclerView
    private lateinit var fab: ExtendedFloatingActionButton

    private val db by lazy { ListDatabase.getDatabase(requireContext()) }
    private val dao by lazy { db.shoppingListDao() }

    private val data = mutableListOf<ShoppingList>()
    private var openedPos: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (_binding == null)
            _binding = FragmentHomeBinding.inflate(layoutInflater, container, false)

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        exitTransition = MaterialElevationScale(false).apply {
            duration = 300L
        }
        reenterTransition = MaterialElevationScale(true).apply {
            duration = 300L
        }
    }

    private fun setExitTransition() {
        setExitSharedElementCallback(object : SharedElementCallback() {
            override fun onMapSharedElements(
                names: MutableList<String>,
                sharedElements: MutableMap<String, View>
            ) {
                super.onMapSharedElements(names, sharedElements)
                if (names[0] == getString(R.string.shopping_list_fab_transition_name)) {
                    recyclerView.layoutManager?.getChildAt(data.size-1)?.let {
                        sharedElements.clear()
                        sharedElements.put(names[0], it)
                    }
                } else if (sharedElements.toList()[0].second != recyclerView.layoutManager?.getChildAt(data.size-1)) {
                    recyclerView.layoutManager?.getChildAt(openedPos)?.let {
                        sharedElements.clear()
                        sharedElements.put(names[0], it)
                    }
                }
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dao.getAll().observe(viewLifecycleOwner) { list ->
            viewLifecycleOwner.lifecycleScope.launch {
                val oldList = data.minus(list.toSet())
                val newList = list.minus(data.toSet()).toMutableList()

                data.toSet().forEachIndexed { i, list ->
                    for (old in oldList) {
                        if (list == old) {
                            for (new in newList) {
                                if (list.id == new.id) {
                                    data[i] = new
                                    newList.remove(new)
                                    recyclerView.adapter?.notifyItemChanged(i)
                                    return@forEachIndexed
                                }
                            }
                            data.removeAt(i)
                            recyclerView.adapter?.notifyItemRemoved(i)
                        }
                    }
                }
                for (new in newList) {
                    data.add(new)
                    recyclerView.adapter?.notifyItemInserted(data.size-1)
                }

                if (newList.isEmpty() && oldList.isEmpty()) {
                    setExitTransition()
                    startPostponedEnterTransition()
                } else {
                    recyclerView.runWhenReady {
                        setExitTransition()
                        startPostponedEnterTransition()
                    }
                }
            }
        }

        postponeEnterTransition()

        if (hasBeenStarted) return
        hasBeenStarted = true

        recyclerView = binding.recyclerView
        fab = binding.fabAdd

        recyclerView.apply {
            adapter = ShoppingListAdapter(data, { list, view ->
                    navigateToShoppingList(list, view)
                    openedPos = data.indexOf(list)
                }, {
                MaterialAlertDialogBuilder(context)
                    .setTitle(context.getString(R.string.dialog_delete_title))
                    .setMessage(context.getString(R.string.dialog_delete_text))
                    .setPositiveButton(context.getString(R.string.dialog_delete_confirm)) { d, _ ->
                        d.dismiss()
                        ioLaunch {
                            dao.delete(it)
                        }
                    }
                    .setNegativeButton(context.getString(R.string.dialog_delete_cancel)) { d, _ ->
                        d.dismiss()
                    }.show()
            })
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }

        fab.setOnClickListener { v ->
            ShoppingList(0, context?.getString(R.string.unnamed_list), listOf()).also {
                viewLifecycleOwner.lifecycleScope.launch {
                    val list = withContext(Dispatchers.IO) {
                        val id = dao.insert(it).toInt()
                        dao.getById(id).firstOrNull()
                    }
                    list?.let { navigateToShoppingList(list, v) }
                }
            }
        }
    }

    private fun navigateToShoppingList(shoppingList: ShoppingList, view: View) {
        val extras = FragmentNavigatorExtras(view to getString(R.string.shopping_list_transition_name))
        val directions = HomeFragmentDirections.actionHomeFragmentToShoppingListFragment(shoppingList.deepCopy())
        findNavController().navigate(directions, extras)
    }
}