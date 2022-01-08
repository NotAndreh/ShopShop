package com.andreh.shopshop.database.entities

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize


@Entity
@Parcelize
data class ShoppingList (
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "name") val name: String?,
    @ColumnInfo(name = "list") val list: List<ShoppingItem>
) : Parcelable {
    fun deepCopy(
        id: Int = this.id,
        name: String? = this.name,
        list: List<ShoppingItem> = this.list.map { it.deepCopy() }
    ) = ShoppingList(id, name, list)
}