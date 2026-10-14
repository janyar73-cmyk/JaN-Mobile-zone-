package com.example.data.model

enum class AccountType(val displayName: String) {
  CASH("Cash"),
  EASYLOAD("Easyload"),
  EASYPAISA("Easypaisa")
}

enum class TransactionType(val displayName: String) {
  CASH_IN("Cash In"),
  CASH_OUT("Cash Out"),
  EASYLOAD("Easyload"),
  EASYPAISA("Easypaisa"),
  INTERNAL_TRANSFER("Account Transfer")
}

enum class EasyloadProvider(val providerName: String, val defaultCommissionRate: Double) {
  JAZZ("Jazz", 0.025), // 2.5% standard commission
  ZONG("Zong", 0.025),
  UFONE("Ufone", 0.030),
  TELENOR("Telenor", 0.025)
}

enum class EasypaisaSubtype(val displayName: String) {
  CASH_IN("Received from Customer (Cash In)"),
  CASH_OUT("Paid to Customer (Cash Out)"),
  BILL_PAYMENT("Bill Payment"),
  MONEY_TRANSFER("Money Transfer")
}

enum class PaymentStatus {
  PAID,
  PARTIALLY_PAID,
  UNPAID
}
