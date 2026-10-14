package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CallReceived
import androidx.compose.material.icons.filled.CellTower
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TransactionEntity
import com.example.data.repository.AccountSummary
import com.example.ui.components.AccountBalanceCard
import com.example.ui.components.JanAppHeader
import com.example.ui.components.ProfitCard
import com.example.ui.components.QuickActionButton
import com.example.ui.components.TransactionRow
import com.example.ui.theme.AmberProfit
import com.example.ui.theme.AmberProfitBg
import com.example.ui.theme.BrandDarkBlue
import com.example.ui.theme.BrandVibrantBlue
import com.example.ui.theme.CashGreen
import com.example.ui.theme.CashGreenBg
import com.example.ui.theme.EasyloadBlue
import com.example.ui.theme.EasyloadBlueBg
import com.example.ui.theme.EasypaisaPurple
import com.example.ui.theme.EasypaisaPurpleBg
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.ExpenseRedBg
import com.example.ui.theme.PinkProfit
import com.example.ui.theme.PinkProfitBg
import com.example.ui.theme.SurfaceBackground
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun DashboardScreen(
  summary: AccountSummary,
  recentTransactions: List<TransactionEntity>,
  onNavigate: (String) -> Unit,
  onTransactionClick: (TransactionEntity) -> Unit = {}
) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(SurfaceBackground)
  ) {
    JanAppHeader(
      title = "JAN Mobile Zone",
      subtitle = "Kanju, Swat",
      onMenuClick = { onNavigate("more") },
      onNotificationClick = { onNavigate("transactions") }
    )

    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp)
    ) {
      item {
        Spacer(modifier = Modifier.height(12.dp))

        // 1. Cash Balance Card (Green)
        AccountBalanceCard(
          title = "Cash Balance",
          amount = summary.cashBalance,
          icon = Icons.Default.Payments,
          backgroundGradient = Brush.horizontalGradient(
            colors = listOf(Color(0xFF059669), Color(0xFF10B981))
          ),
          onClick = { onNavigate("cash_in_out") }
        )

        // 2. Easyload Balance Card (Cyan / Blue)
        AccountBalanceCard(
          title = "Easyload Balance",
          amount = summary.easyloadBalance,
          icon = Icons.Default.CellTower,
          backgroundGradient = Brush.horizontalGradient(
            colors = listOf(Color(0xFF0284C7), Color(0xFF38BDF8))
          ),
          onClick = { onNavigate("easyload") }
        )

        // 3. Easypaisa Balance Card (Purple)
        AccountBalanceCard(
          title = "Easypaisa Balance",
          amount = summary.easypaisaBalance,
          icon = Icons.Default.Wallet,
          backgroundGradient = Brush.horizontalGradient(
            colors = listOf(Color(0xFF7C3AED), Color(0xFFA855F7))
          ),
          onClick = { onNavigate("easypaisa") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Profit Summary Row (Today's Profit & Monthly Profit)
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          ProfitCard(
            title = "Today's Profit",
            amount = summary.todayProfit,
            icon = Icons.Default.TrendingUp,
            containerColor = AmberProfitBg,
            textColor = AmberProfit,
            modifier = Modifier.weight(1f),
            onClick = { onNavigate("reports") }
          )

          ProfitCard(
            title = "Monthly Profit",
            amount = summary.monthlyProfit,
            icon = Icons.Default.EventNote,
            containerColor = PinkProfitBg,
            textColor = PinkProfit,
            modifier = Modifier.weight(1f),
            onClick = { onNavigate("reports") }
          )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Quick Actions Section Title
        Text(
          text = "Quick Actions",
          fontSize = 16.sp,
          fontWeight = FontWeight.Bold,
          color = TextPrimary
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Quick Actions 2x2 Grid
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          QuickActionButton(
            title = "Cash In",
            icon = Icons.Default.Payments,
            iconColor = CashGreen,
            backgroundColor = CashGreenBg,
            modifier = Modifier.weight(1f),
            onClick = { onNavigate("add_cash_in") }
          )

          QuickActionButton(
            title = "Cash Out",
            icon = Icons.Default.CallReceived,
            iconColor = ExpenseRed,
            backgroundColor = ExpenseRedBg,
            modifier = Modifier.weight(1f),
            onClick = { onNavigate("add_cash_out") }
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          QuickActionButton(
            title = "Easyload",
            icon = Icons.Default.CellTower,
            iconColor = EasyloadBlue,
            backgroundColor = EasyloadBlueBg,
            modifier = Modifier.weight(1f),
            onClick = { onNavigate("easyload") }
          )

          QuickActionButton(
            title = "Easypaisa",
            icon = Icons.Default.Wallet,
            iconColor = EasypaisaPurple,
            backgroundColor = EasypaisaPurpleBg,
            modifier = Modifier.weight(1f),
            onClick = { onNavigate("easypaisa") }
          )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Recent Transactions Section Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Recent Transactions",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
          )
          Text(
            text = "View All",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = BrandVibrantBlue,
            modifier = Modifier.clickable { onNavigate("transactions") }
          )
        }

        Spacer(modifier = Modifier.height(8.dp))
      }

      items(recentTransactions.take(6)) { tx ->
        TransactionRow(
          transaction = tx,
          onClick = { onTransactionClick(tx) }
        )
      }

      item {
        Spacer(modifier = Modifier.height(80.dp))
      }
    }
  }
}
