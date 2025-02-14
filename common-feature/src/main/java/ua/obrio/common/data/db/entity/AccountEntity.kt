package ua.obrio.common.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "account")
data class AccountEntity(
    @PrimaryKey val id: Int = 0, // Always one row (single account)
    val currentBalanceBTC: Double
)