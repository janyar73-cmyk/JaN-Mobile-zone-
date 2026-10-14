package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.JanBottomNavBar
import com.example.ui.screens.AddTransactionScreen
import com.example.ui.screens.CashInOutScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.EasyloadScreen
import com.example.ui.screens.EasypaisaScreen
import com.example.ui.screens.InvoicesScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.MoreScreen
import com.example.ui.screens.OpeningBalancesDialog
import com.example.ui.screens.ReportsScreen
import com.example.ui.screens.TransactionHistoryScreen
import com.example.ui.theme.JANMobileZoneTheme
import com.example.viewmodel.BookkeepingViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      JANMobileZoneTheme {
        JANMobileZoneApp()
      }
    }
  }
}

@Composable
fun JANMobileZoneApp(
  viewModel: BookkeepingViewModel = viewModel()
) {
  val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()
  val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
  val addTransactionType by viewModel.addTransactionType.collectAsStateWithLifecycle()
  val summary by viewModel.accountSummary.collectAsStateWithLifecycle()
  val transactions by viewModel.transactions.collectAsStateWithLifecycle()
  val invoices by viewModel.invoices.collectAsStateWithLifecycle()

  var showOpeningBalancesDialog by remember { mutableStateOf(false) }
  val scope = rememberCoroutineScope()

  // Back button handling
  BackHandler(enabled = currentScreen != "home" && isLoggedIn) {
    viewModel.navigateTo("home")
  }

  if (!isLoggedIn) {
    LoginScreen(onLoginSuccess = { viewModel.login() })
  } else {
    Scaffold(
      modifier = Modifier.fillMaxSize(),
      bottomBar = {
        // Show bottom navigation bar on all main tab screens
        val mainTabs = listOf("home", "transactions", "reports", "more", "cash_in_out", "easyload", "easypaisa")
        if (currentScreen in mainTabs) {
          JanBottomNavBar(
            currentScreen = currentScreen,
            onScreenSelect = { screen ->
              viewModel.navigateTo(screen)
            }
          )
        }
      }
    ) { innerPadding ->
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(innerPadding)
      ) {
        when (currentScreen) {
          "home" -> {
            DashboardScreen(
              summary = summary,
              recentTransactions = transactions,
              onNavigate = { route ->
                when (route) {
                  "add_cash_in" -> viewModel.navigateTo("add_transaction", "CASH_IN")
                  "add_cash_out" -> viewModel.navigateTo("add_transaction", "CASH_OUT")
                  else -> viewModel.navigateTo(route)
                }
              },
              onTransactionClick = {
                viewModel.navigateTo("transactions")
              }
            )
          }

          "transactions" -> {
            TransactionHistoryScreen(
              transactions = transactions,
              onReverseTransaction = { id -> viewModel.reverseTransaction(id) },
              onDeleteTransaction = { tx -> viewModel.deleteTransaction(tx) },
              onBack = { viewModel.navigateTo("home") }
            )
          }

          "add_transaction" -> {
            AddTransactionScreen(
              initialType = addTransactionType,
              onSave = { tx -> viewModel.addTransaction(tx) },
              onCancel = { viewModel.navigateTo("home") }
            )
          }

          "cash_in_out" -> {
            CashInOutScreen(
              summary = summary,
              transactions = transactions,
              initialTab = "CASH_IN",
              onAddTransaction = { tx -> viewModel.addTransaction(tx) },
              onBack = { viewModel.navigateTo("home") }
            )
          }

          "easyload" -> {
            EasyloadScreen(
              summary = summary,
              transactions = transactions,
              onAddTransaction = { tx -> viewModel.addTransaction(tx) },
              onBack = { viewModel.navigateTo("home") }
            )
          }

          "easypaisa" -> {
            EasypaisaScreen(
              summary = summary,
              transactions = transactions,
              onAddTransaction = { tx -> viewModel.addTransaction(tx) },
              onBack = { viewModel.navigateTo("home") }
            )
          }

          "invoices" -> {
            InvoicesScreen(
              invoices = invoices,
              onCreateInvoice = { inv, items -> viewModel.createInvoice(inv, items) },
              onBack = { viewModel.navigateTo("more") }
            )
          }

          "reports" -> {
            ReportsScreen(
              summary = summary,
              transactions = transactions,
              onExportCsv = { viewModel.exportCsv() },
              onBack = { viewModel.navigateTo("home") }
            )
          }

          "more" -> {
            MoreScreen(
              summary = summary,
              onOpenOpeningBalances = { showOpeningBalancesDialog = true },
              onOpenInvoices = { viewModel.navigateTo("invoices") },
              onOpenReports = { viewModel.navigateTo("reports") },
              onExportCsv = { viewModel.exportCsv() },
              onExportBackupJson = { viewModel.exportBackupJson() },
              onLogout = { viewModel.logout() }
            )
          }

          else -> {
            DashboardScreen(
              summary = summary,
              recentTransactions = transactions,
              onNavigate = { viewModel.navigateTo(it) }
            )
          }
        }
      }
    }
  }

  // Opening Balances Dialog
  if (showOpeningBalancesDialog) {
    OpeningBalancesDialog(
      initialCash = summary.cashBalance,
      initialEasyload = summary.easyloadOpening,
      initialEasypaisa = summary.easypaisaOpening,
      onDismiss = { showOpeningBalancesDialog = false },
      onSave = { cash, easyload, easypaisa ->
        viewModel.setOpeningBalances(cash, easyload, easypaisa)
        showOpeningBalancesDialog = false
      }
    )
  }
}
