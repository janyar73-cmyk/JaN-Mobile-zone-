package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.model.InvoiceEntity
import com.example.data.model.InvoiceItemEntity
import com.example.data.model.TransactionEntity
import com.example.data.repository.AccountSummary
import com.example.data.repository.BookkeepingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class BookkeepingViewModel(application: Application) : AndroidViewModel(application) {

  private val database = AppDatabase.getDatabase(application)
  private val repository = BookkeepingRepository(database.appDao())

  // Authentication state
  private val _isLoggedIn = MutableStateFlow(true) // Auto-logged in so emulator and user see dashboard immediately
  val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

  // Navigation state
  private val _currentScreen = MutableStateFlow("home")
  val currentScreen: StateFlow<String> = _currentScreen.asStateFlow()

  // Initial type for AddTransactionScreen
  private val _addTransactionType = MutableStateFlow("CASH_IN")
  val addTransactionType: StateFlow<String> = _addTransactionType.asStateFlow()

  // Transactions & Summary
  val accountSummary: StateFlow<AccountSummary> = repository.accountSummaryFlow.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = AccountSummary(
      cashBalance = 12450.0,
      easyloadBalance = 8760.0,
      easypaisaBalance = 5320.0,
      todayProfit = 480.0,
      monthlyProfit = 12850.0,
      todayCashIn = 7500.0,
      todayCashOut = 3200.0,
      todayNetCash = 4300.0,
      easyloadOpening = 1000.0,
      easyloadSales = 5200.0,
      easyloadDeduction = 4800.0,
      easyloadCommission = 200.0,
      easypaisaOpening = 2000.0,
      easypaisaReceived = 6500.0,
      easypaisaPaidOut = 4800.0,
      easypaisaServiceCharges = 120.0,
      easypaisaCommission = 180.0
    )
  )

  val transactions: StateFlow<List<TransactionEntity>> = repository.allTransactions.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = emptyList()
  )

  val invoices: StateFlow<List<InvoiceEntity>> = repository.allInvoices.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = emptyList()
  )

  init {
    viewModelScope.launch {
      repository.seedInitialDataIfEmpty()
    }
  }

  fun navigateTo(screen: String, transactionType: String = "CASH_IN") {
    _addTransactionType.value = transactionType
    _currentScreen.value = screen
  }

  fun addTransaction(tx: TransactionEntity) {
    viewModelScope.launch {
      repository.insertTransaction(tx)
      _currentScreen.value = "home"
    }
  }

  fun deleteTransaction(tx: TransactionEntity) {
    viewModelScope.launch {
      repository.deleteTransaction(tx)
    }
  }

  fun reverseTransaction(id: Long) {
    viewModelScope.launch {
      repository.reverseTransaction(id)
    }
  }

  fun createInvoice(invoice: InvoiceEntity, items: List<InvoiceItemEntity>) {
    viewModelScope.launch {
      repository.createInvoice(invoice, items)
    }
  }

  fun setOpeningBalances(cash: Double, easyload: Double, easypaisa: Double) {
    viewModelScope.launch {
      repository.setOpeningBalance("CASH", cash)
      repository.setOpeningBalance("EASYLOAD", easyload)
      repository.setOpeningBalance("EASYPAISA", easypaisa)
    }
  }

  fun exportCsv(): String {
    return repository.exportToCsv(transactions.value)
  }

  suspend fun exportBackupJson(): String {
    return repository.exportBackupJson()
  }

  fun login() {
    _isLoggedIn.value = true
    _currentScreen.value = "home"
  }

  fun logout() {
    _isLoggedIn.value = false
    _currentScreen.value = "login"
  }
}
