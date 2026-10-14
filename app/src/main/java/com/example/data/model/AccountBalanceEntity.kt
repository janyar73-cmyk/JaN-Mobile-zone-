package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "account_balances")
data class AccountBalanceEntity(
  @PrimaryKey
  val accountKey: String, // "CASH", "EASYLOAD", "EASYPAISA", "JAZZ", "ZONG", "UFONE", "TELENOR"
  val openingBalance: Double = 0.0,
  val lastUpdated: Long = System.currentTimeMillis()
)
