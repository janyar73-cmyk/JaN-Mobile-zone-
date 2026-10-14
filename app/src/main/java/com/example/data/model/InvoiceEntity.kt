package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "invoices")
data class InvoiceEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val invoiceNumber: String,
  val dateTimestamp: Long = System.currentTimeMillis(),
  val customerName: String,
  val customerMobile: String,
  val subtotal: Double = 0.0,
  val discount: Double = 0.0,
  val grandTotal: Double = 0.0,
  val paidAmount: Double = 0.0,
  val paymentStatus: String = "PAID", // PAID, PARTIALLY_PAID, UNPAID
  val notes: String = ""
) {
  val remainingBalance: Double
    get() = (grandTotal - paidAmount).coerceAtLeast(0.0)
}

@Entity(tableName = "invoice_items")
data class InvoiceItemEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val invoiceId: Long,
  val description: String,
  val quantity: Int,
  val unitPrice: Double,
  val totalPrice: Double
)
