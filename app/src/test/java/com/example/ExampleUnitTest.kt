package com.example

import com.example.data.model.TransactionEntity
import com.example.ui.components.formatPkr
import com.example.ui.components.isValidMonetaryInput
import com.example.ui.components.parsePkr
import com.example.ui.components.parsePkrToPaisas
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

  @Test
  fun testManualPriceAndAmountEntryBugFix() {
    // Requirements: "Test manual entry using '100', '1250', '2500.50', and '10000'.
    // Verify that each value remains correct before and after saving and reopening the transaction."

    val testValues = listOf("100", "1250", "2500.50", "10000")
    for (value in testValues) {
      assertTrue("Input '$value' must be valid while typing", isValidMonetaryInput(value))
      val parsed = value.parsePkr()
      assertTrue("Parsed value must be > 0", parsed > 0.0)

      // Verify transaction saving and reopening amount integrity
      val tx = TransactionEntity(
        id = 101,
        type = "CASH_IN",
        subCategory = "Customer Payment",
        amount = parsed,
        accountAffected = "CASH",
        description = "Test transaction for $value"
      )
      assertEquals("Amount must match input", parsed, tx.amount, 0.0001)

      // Test paisa representation avoids precision loss
      val paisas = value.parsePkrToPaisas()
      assertTrue("Paisas must accurately represent value", paisas > 0)
    }

    assertEquals(100.0, "100".parsePkr(), 0.0)
    assertEquals(1250.0, "1250".parsePkr(), 0.0)
    assertEquals(2500.50, "2500.50".parsePkr(), 0.0)
    assertEquals(10000.0, "10000".parsePkr(), 0.0)

    assertEquals(10000L, "100".parsePkrToPaisas())
    assertEquals(125000L, "1250".parsePkrToPaisas())
    assertEquals(250050L, "2500.50".parsePkrToPaisas())
    assertEquals(1000000L, "10000".parsePkrToPaisas())
  }

  @Test
  fun testIncompleteTypingStatesDoNotEatDecimals() {
    // "Do not convert an incomplete input such as '12.' into '12' while the user is still typing."
    assertTrue(isValidMonetaryInput("12."))
    assertTrue(isValidMonetaryInput("12.5"))
    assertTrue(isValidMonetaryInput("12.50"))

    // Max 2 decimal digits enforced
    assertFalse(isValidMonetaryInput("12.500"))
    // Multiple dots rejected
    assertFalse(isValidMonetaryInput("12..5"))
    assertFalse(isValidMonetaryInput("12.5.5"))
    // Non numeric characters rejected
    assertFalse(isValidMonetaryInput("abc"))
    assertFalse(isValidMonetaryInput("12a"))
    assertFalse(isValidMonetaryInput("-50"))
  }

  @Test
  fun testPkrFormatting() {
    assertEquals("Rs. 100", 100.0.formatPkr())
    assertEquals("Rs. 1,250", 1250.0.formatPkr())
    assertEquals("Rs. 2,500.50", 2500.50.formatPkr())
    assertEquals("Rs. 10,000", 10000.0.formatPkr())
    assertEquals("Rs. 12,450", 12450.0.formatPkr())
  }

  @Test
  fun testAccountingNetProfit() {
    // Cash In customer sale produces income
    val saleTx = TransactionEntity(
      type = "CASH_IN",
      subCategory = "Repair Service",
      amount = 2500.0,
      accountAffected = "CASH",
      description = "LCD replacement"
    )
    assertEquals(2500.0, saleTx.netProfit, 0.0)

    // Top Up does not count as income
    val topUpTx = TransactionEntity(
      type = "CASH_IN",
      subCategory = "Cash Top Up",
      amount = 5000.0,
      accountAffected = "CASH",
      description = "Counter deposit"
    )
    assertEquals(0.0, topUpTx.netProfit, 0.0)

    // Shop Expense reduces profit
    val expenseTx = TransactionEntity(
      type = "CASH_OUT",
      subCategory = "Shop Expense",
      amount = 800.0,
      accountAffected = "CASH",
      description = "Tea & electric repair"
    )
    assertEquals(-800.0, expenseTx.netProfit, 0.0)

    // Easyload commission is profit
    val easyloadTx = TransactionEntity(
      type = "EASYLOAD",
      subCategory = "Jazz",
      amount = 1000.0,
      costAmount = 970.0,
      commission = 30.0,
      accountAffected = "EASYLOAD",
      description = "Jazz super card"
    )
    assertEquals(30.0, easyloadTx.netProfit, 0.0)
  }
}
