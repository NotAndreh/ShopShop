package com.andreh.shopshop.database.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ShoppingItem(
    val id: Int,
    var name: String?,
    var amount: Int,
    var checked: Boolean
) : Parcelable {
    fun deepCopy(
        id: Int = this.id,
        name: String? = this.name,
        amount: Int = this.amount,
        checked: Boolean = this.checked
    ) = ShoppingItem(id, name, amount, checked)
}