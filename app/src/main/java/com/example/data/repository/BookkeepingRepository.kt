package com.example.data.repository

import com.example.data.db.AppDao
import com.example.data.model.AccountBalanceEntity
import com.example.data.model.InvoiceEntity
import com.example.data.model.InvoiceItemEntity
import com.example.data.model.TransactionEntity
import com.example.data.model.UserEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

data class AccountSummary(
  val cashBalance: Double,
  val easyloadBalance: Double,
  val easypaisaBalance: Double,
  val todayProfit: Double,
  val monthlyProfit: Double,
  val todayCashIn: Double,
  val todayCashOut: Double,
  val todayNetCash: Double,
  val easyloadOpening: Double,
  val easyloadSales: Double,
  val easyloadDeduction: Double,
  val easyloadCommission: Double,
  val easypaisaOpening: Double,
  val easypaisaReceived: Double,
  val easypaisaPaidOut: Double,
  val easypaisaServiceCharges: Double,
  val easypaisaCommission: Double
)

class BookkeepingRepository(private val dao: AppDao) {

  val allTransactions: Flow<List<TransactionEntity>> = dao.getAllTransactions()
  val allBalances: Flow<List<AccountBalanceEntity>> = dao.getAllBalances()
  val allInvoices: Flow<List<InvoiceEntity>> = dao.getAllInvoices()

  // Combined flow that accurately calculates all balances and profits based on real records
  val accountSummaryFlow: Flow<AccountSummary> = combine(
    allTransactions,
    allBalances
  ) { transactions, balances ->
    calculateSummary(transactions, balances)
  }

  suspend fun insertTransaction(transaction: TransactionEntity): Long {
    return dao.insertTransaction(transaction)
  }

  suspend fun updateTransaction(transaction: TransactionEntity) {
    dao.updateTransaction(transaction)
  }

  suspend fun deleteTransaction(transaction: TransactionEntity) {
    dao.deleteTransaction(transaction)
  }

  suspend fun reverseTransaction(transactionId: Long) {
    val tx = dao.getTransactionById(transactionId) ?: return
    dao.updateTransaction(tx.copy(isReversed = !tx.isReversed))
  }

  suspend fun setOpeningBalance(accountKey: String, amount: Double) {
    dao.setBalance(
      AccountBalanceEntity(
        accountKey = accountKey,
        openingBalance = amount,
        lastUpdated = System.currentTimeMillis()
      )
    )
  }

  suspend fun getOpeningBalance(accountKey: String): Double {
    return dao.getBalanceByKey(accountKey)?.openingBalance ?: 0.0
  }

  // Invoice functions
  suspend fun createInvoice(invoice: InvoiceEntity, items: List<InvoiceItemEntity>): Long {
    val id = dao.insertInvoice(invoice)
    val itemsWithId = items.map { it.copy(invoiceId = id) }
    dao.insertInvoiceItems(itemsWithId)

    // Also record payment as Cash In transaction if paid > 0
    if (invoice.paidAmount > 0) {
      dao.insertTransaction(
        TransactionEntity(
          type = "CASH_IN",
          subCategory = "Invoice Payment #${invoice.invoiceNumber}",
          amount = invoice.paidAmount,
          accountAffected = "CASH",
          description = "Payment for ${invoice.customerName} (${items.joinToString { it.description }})",
          customerName = invoice.customerName,
          customerPhone = invoice.customerMobile,
          referenceNumber = invoice.invoiceNumber
        )
      )
    }
    return id
  }

  fun getInvoiceItems(invoiceId: Long): Flow<List<InvoiceItemEntity>> = dao.getInvoiceItems(invoiceId)

  // Auth functions
  suspend fun getUser(username: String): UserEntity? = dao.getUserByUsername(username)
  suspend fun saveUser(user: UserEntity) = dao.insertUser(user)
  suspend fun getFirstUser(): UserEntity? = dao.getFirstUser()

  // Seeding initial records matching JAN Mobile Zone reference design if database is empty
  suspend fun seedInitialDataIfEmpty() {
    val existingUser = dao.getFirstUser()
    if (existingUser == null) {
      dao.insertUser(
        UserEntity(
          username = "admin",
          passwordHash = "1234",
          shopName = "JAN Mobile Zone",
          location = "Kanju, Swat",
          phone = "+92 345 9400000",
          isRemembered = true
        )
      )
    }

    val cashBalance = dao.getBalanceByKey("CASH")
    if (cashBalance == null) {
      dao.setBalances(
        listOf(
          AccountBalanceEntity("CASH", openingBalance = 6750.0),
          AccountBalanceEntity("EASYLOAD", openingBalance = 1000.0),
          AccountBalanceEntity("EASYPAISA", openingBalance = 2000.0),
          AccountBalanceEntity("JAZZ", openingBalance = 4000.0),
          AccountBalanceEntity("ZONG", openingBalance = 3000.0),
          AccountBalanceEntity("UFONE", openingBalance = 2000.0),
          AccountBalanceEntity("TELENOR", openingBalance = 1000.0)
        )
      )
    }

    val calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Karachi"))
    val now = calendar.timeInMillis
    calendar.set(Calendar.HOUR_OF_DAY, 9)
    calendar.set(Calendar.MINUTE, 30)
    val morningTime = calendar.timeInMillis

    val existingTx = dao.getTransactionById(1)
    if (existingTx == null) {
      val sampleList = listOf(
        // Cash transactions from reference
        TransactionEntity(
          type = "CASH_IN",
          subCategory = "Customer Payment",
          amount = 1000.0,
          accountAffected = "CASH",
          description = "Mobile LCD repair payment",
          customerName = "Khanzada Swat",
          timestamp = morningTime + 1000 * 60 * 30
        ),
        TransactionEntity(
          type = "CASH_OUT",
          subCategory = "Shop Purchase",
          amount = 1500.0,
          accountAffected = "CASH",
          description = "Protector glass stock & tools",
          timestamp = morningTime + 1000 * 60 * 65
        ),
        TransactionEntity(
          type = "CASH_IN",
          subCategory = "Top Up",
          amount = 2000.0,
          accountAffected = "CASH",
          description = "Shop cash counter top up",
          timestamp = morningTime + 1000 * 60 * 90
        ),
        TransactionEntity(
          type = "CASH_OUT",
          subCategory = "Cash Out",
          amount = 800.0,
          accountAffected = "CASH",
          description = "Shop utility & tea bill",
          timestamp = morningTime + 1000 * 60 * 120
        ),
        TransactionEntity(
          type = "CASH_IN",
          subCategory = "Customer Payment",
          amount = 4500.0,
          accountAffected = "CASH",
          description = "Samsung A32 display replacement",
          customerName = "Ali Rehman",
          timestamp = morningTime + 1000 * 60 * 150
        ),
        TransactionEntity(
          type = "CASH_OUT",
          subCategory = "Cash Out",
          amount = 900.0,
          accountAffected = "CASH",
          description = "Soldering iron & flux solder purchase",
          timestamp = morningTime + 1000 * 60 * 180
        ),

        // Easyload transactions from reference
        TransactionEntity(
          type = "EASYLOAD",
          subCategory = "Jazz",
          amount = 1000.0,
          costAmount = 970.0,
          commission = 30.0,
          accountAffected = "EASYLOAD",
          description = "Jazz super card recharge",
          customerPhone = "03001234567",
          timestamp = morningTime + 1000 * 60 * 200
        ),
        TransactionEntity(
          type = "EASYLOAD",
          subCategory = "Zong",
          amount = 500.0,
          costAmount = 485.0,
          commission = 15.0,
          accountAffected = "EASYLOAD",
          description = "Zong weekly package",
          customerPhone = "03139876543",
          timestamp = morningTime + 1000 * 60 * 220
        ),
        TransactionEntity(
          type = "EASYLOAD",
          subCategory = "Telenor",
          amount = 300.0,
          costAmount = 292.0,
          commission = 8.0,
          accountAffected = "EASYLOAD",
          description = "Telenor easyload",
          customerPhone = "03451122334",
          timestamp = morningTime + 1000 * 60 * 240
        ),
        TransactionEntity(
          type = "EASYLOAD",
          subCategory = "Load Added",
          amount = 3400.0,
          costAmount = 0.0,
          commission = 0.0,
          accountAffected = "EASYLOAD",
          description = "Stock added from franchise distributor",
          timestamp = morningTime + 1000 * 60 * 250
        ),

        // Easypaisa transactions from reference
        TransactionEntity(
          type = "EASYPAISA",
          subCategory = "Received from Customer",
          amount = 2000.0,
          commission = 50.0,
          serviceCharges = 30.0,
          accountAffected = "EASYPAISA",
          description = "Cash In money transfer to Lahore",
          customerName = "Waqas Ahmad",
          timestamp = morningTime + 1000 * 60 * 280
        ),
        TransactionEntity(
          type = "EASYPAISA",
          subCategory = "Paid to Customer",
          amount = 1500.0,
          commission = 40.0,
          serviceCharges = 20.0,
          accountAffected = "EASYPAISA",
          description = "Cash Out biometric withdrawal",
          customerName = "Fazal Swati",
          timestamp = morningTime + 1000 * 60 * 300
        ),
        TransactionEntity(
          type = "EASYPAISA",
          subCategory = "Bill Payment",
          amount = 300.0,
          commission = 20.0,
          serviceCharges = 10.0,
          accountAffected = "EASYPAISA",
          description = "PESCO Electricity Bill",
          referenceNumber = "041234567890",
          timestamp = morningTime + 1000 * 60 * 320
        )
      )
      dao.insertTransactions(sampleList)
    }
  }

  // Backup data to JSON string
  suspend fun exportBackupJson(): String {
    val obj = JSONObject()
    obj.put("app", "JAN Mobile Zone")
    obj.put("version", "1.0")
    obj.put("exportTime", System.currentTimeMillis())

    // All balances
    val balances = dao.getBalanceByKey("CASH")
    val balanceArray = JSONArray()
    listOf("CASH", "EASYLOAD", "EASYPAISA", "JAZZ", "ZONG", "UFONE", "TELENOR").forEach { key ->
      val b = dao.getBalanceByKey(key)
      if (b != null) {
        val bObj = JSONObject()
        bObj.put("key", b.accountKey)
        bObj.put("balance", b.openingBalance)
        balanceArray.put(bObj)
      }
    }
    obj.put("balances", balanceArray)
    return obj.toString(2)
  }

  // Export to CSV
  fun exportToCsv(transactions: List<TransactionEntity>): String {
    val sb = StringBuilder()
    sb.append("ID,Date,Type,Category,Amount(PKR),Commission(PKR),Account,Description,Customer,Ref\n")
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH)
    transactions.forEach { tx ->
      val dateStr = sdf.format(Date(tx.timestamp))
      sb.append("${tx.id},\"$dateStr\",\"${tx.type}\",\"${tx.subCategory}\",${tx.amount},${tx.commission},\"${tx.accountAffected}\",\"${tx.description.replace("\"", "\"\"")}\",\"${tx.customerName}\",\"${tx.referenceNumber}\"\n")
    }
    return sb.toString()
  }

  private fun calculateSummary(
    transactions: List<TransactionEntity>,
    balances: List<AccountBalanceEntity>
  ): AccountSummary {
    val openingCash = balances.find { it.accountKey == "CASH" }?.openingBalance ?: 6750.0
    val openingEasyload = balances.find { it.accountKey == "EASYLOAD" }?.openingBalance ?: 1000.0
    val openingEasypaisa = balances.find { it.accountKey == "EASYPAISA" }?.openingBalance ?: 2000.0

    val tz = TimeZone.getTimeZone("Asia/Karachi")
    val cal = Calendar.getInstance(tz)
    val currentDay = cal.get(Calendar.DAY_OF_YEAR)
    val currentYear = cal.get(Calendar.YEAR)
    val currentMonth = cal.get(Calendar.MONTH)

    var cashBalance = openingCash
    var easyloadBalance = openingEasyload
    var easypaisaBalance = openingEasypaisa

    var todayProfit = 0.0
    var monthlyProfit = 0.0

    var todayCashIn = 0.0
    var todayCashOut = 0.0

    var easyloadSales = 0.0
    var easyloadDeduction = 0.0
    var easyloadCommission = 0.0

    var easypaisaReceived = 0.0
    var easypaisaPaidOut = 0.0
    var easypaisaServiceCharges = 0.0
    var easypaisaCommission = 0.0

    transactions.forEach { tx ->
      if (!tx.isReversed) {
        cal.timeInMillis = tx.timestamp
        val isToday = (cal.get(Calendar.DAY_OF_YEAR) == currentDay && cal.get(Calendar.YEAR) == currentYear)
        val isThisMonth = (cal.get(Calendar.MONTH) == currentMonth && cal.get(Calendar.YEAR) == currentYear)

        val profit = tx.netProfit
        if (isToday) todayProfit += profit
        if (isThisMonth) monthlyProfit += profit

        when (tx.type) {
          "CASH_IN" -> {
            cashBalance += tx.amount
            if (isToday) todayCashIn += tx.amount
          }
          "CASH_OUT" -> {
            cashBalance -= tx.amount
            if (isToday) todayCashOut += tx.amount
          }
          "EASYLOAD" -> {
            if (tx.subCategory == "Load Added" || tx.subCategory == "Stock Added") {
              easyloadBalance += tx.amount
            } else {
              // Customer load sale
              val deduction = if (tx.costAmount > 0) tx.costAmount else tx.amount
              easyloadBalance -= deduction
              easyloadSales += tx.amount
              easyloadDeduction += deduction
              easyloadCommission += tx.commission
              // Customer pays cash to shop
              cashBalance += tx.amount
              if (isToday) todayCashIn += tx.amount
            }
          }
          "EASYPAISA" -> {
            if (tx.subCategory.contains("Received", ignoreCase = true) || tx.subCategory.contains("Cash In", ignoreCase = true)) {
              // Customer gives cash to shop, shop sends Easypaisa from app
              cashBalance += (tx.amount + tx.serviceCharges)
              easypaisaBalance -= tx.amount
              easypaisaReceived += tx.amount
              easypaisaServiceCharges += tx.serviceCharges
              easypaisaCommission += tx.commission
              if (isToday) todayCashIn += (tx.amount + tx.serviceCharges)
            } else if (tx.subCategory.contains("Paid", ignoreCase = true) || tx.subCategory.contains("Cash Out", ignoreCase = true)) {
              // Customer transfers to shop's Easypaisa, shop gives customer cash
              cashBalance -= tx.amount
              easypaisaBalance += (tx.amount + tx.serviceCharges)
              easypaisaPaidOut += tx.amount
              easypaisaServiceCharges += tx.serviceCharges
              easypaisaCommission += tx.commission
              if (isToday) todayCashOut += tx.amount
            } else if (tx.subCategory.contains("Bill", ignoreCase = true)) {
              // Bill payment: customer gives cash, shop pays via Easypaisa
              cashBalance += (tx.amount + tx.serviceCharges)
              easypaisaBalance -= tx.amount
              easypaisaServiceCharges += tx.serviceCharges
              easypaisaCommission += tx.commission
              if (isToday) todayCashIn += (tx.amount + tx.serviceCharges)
            } else if (tx.subCategory.contains("Deposit", ignoreCase = true) || tx.subCategory.contains("Stock", ignoreCase = true)) {
              // Shop added money to Easypaisa account from bank/franchise
              easypaisaBalance += tx.amount
            }
          }
          "INTERNAL_TRANSFER" -> {
            // Transfer between accounts
            when (tx.accountAffected) {
              "CASH" -> cashBalance -= tx.amount
              "EASYLOAD" -> easyloadBalance -= tx.amount
              "EASYPAISA" -> easypaisaBalance -= tx.amount
            }
            when (tx.secondaryAccount) {
              "CASH" -> cashBalance += tx.amount
              "EASYLOAD" -> easyloadBalance += tx.amount
              "EASYPAISA" -> easypaisaBalance += tx.amount
            }
          }
        }
      }
    }

    return AccountSummary(
      cashBalance = cashBalance,
      easyloadBalance = easyloadBalance,
      easypaisaBalance = easypaisaBalance,
      todayProfit = todayProfit,
      monthlyProfit = monthlyProfit,
      todayCashIn = todayCashIn,
      todayCashOut = todayCashOut,
      todayNetCash = todayCashIn - todayCashOut,
      easyloadOpening = openingEasyload,
      easyloadSales = easyloadSales,
      easyloadDeduction = easyloadDeduction,
      easyloadCommission = easyloadCommission,
      easypaisaOpening = openingEasypaisa,
      easypaisaReceived = easypaisaReceived,
      easypaisaPaidOut = easypaisaPaidOut,
      easypaisaServiceCharges = easypaisaServiceCharges,
      easypaisaCommission = easypaisaCommission
    )
  }
}
