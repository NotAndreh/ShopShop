package com.andreh.shopshop.database.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.andreh.shopshop.database.entities.ShoppingList

@Dao
interface ShoppingListDao {
    @Query("SELECT * FROM shoppinglist")
    fun getAll(): LiveData<List<ShoppingList>>

    @Query("SELECT * FROM shoppinglist WHERE id=:id")
    fun getById(id: Int): List<ShoppingList>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(shoppingList: ShoppingList): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(shoppingList: List<ShoppingList>): List<Long>

    @Update
    fun update(shoppingList: ShoppingList)

    @Delete
    fun delete(shoppingList: ShoppingList)
}