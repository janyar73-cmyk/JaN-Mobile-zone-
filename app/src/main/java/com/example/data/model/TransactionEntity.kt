package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val type: String, // CASH_IN, CASH_OUT, EASYLOAD, EASYPAISA, INTERNAL_TRANSFER
  val subCategory: String, // e.g. "Customer Payment", "Load Sale", "Bill Payment", "Shop Purchase"
  val amount: Double, // PKR transaction amount
  val costAmount: Double = 0.0, // Cost / deduction from telecom or balance
  val commission: Double = 0.0, // Profit / commission earned on this transaction
  val serviceCharges: Double = 0.0, // Extra fee/charges collected
  val accountAffected: String, // CASH, EASYLOAD, EASYPAISA
  val secondaryAccount: String? = null, // Used for internal transfers between accounts
  val description: String,
  val customerName: String = "",
  val customerPhone: String = "",
  val referenceNumber: String = "",
  val timestamp: Long = System.currentTimeMillis(),
  val isReversed: Boolean = false
) {
  // Pure profit calculation for this transaction
  val netProfit: Double
    get() {
      if (isReversed) return 0.0
      return when (type) {
        "EASYLOAD" -> commission
        "EASYPAISA" -> commission + serviceCharges
        "CASH_IN" -> {
          // If customer payment for service or goods, commission or profit can be stored, or if shop sales:
          if (subCategory.contains("Deposit", ignoreCase = true) || subCategory.contains("Top Up", ignoreCase = true)) {
            0.0 // Deposits are not income
          } else {
            if (commission > 0.0) commission else amount // By default sales amount or commission
          }
        }
        "CASH_OUT" -> {
          if (subCategory.contains("Expense", ignoreCase = true) || subCategory.contains("Purchase", ignoreCase = true) || subCategory.contains("Shop", ignoreCase = true)) {
            -amount // Expense reduces profit
          } else {
            0.0 // Personal withdrawal / drawing or transfer
          }
        }
        else -> 0.0
      }
    }
}
