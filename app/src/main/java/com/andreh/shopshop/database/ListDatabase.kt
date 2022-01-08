package com.andreh.shopshop.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.andreh.shopshop.database.daos.ShoppingListDao
import com.andreh.shopshop.database.entities.ShoppingList

@Database(entities = [ShoppingList::class], version = 1)
@TypeConverters(DataConverter::class)
abstract class ListDatabase : RoomDatabase() {
    abstract fun shoppingListDao(): ShoppingListDao

    companion object {
        @Volatile
        private var INSTANCE: ListDatabase? = null

        fun getDatabase(context: Context): ListDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ListDatabase::class.java,
                    "shoppinglist_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}