package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CallReceived
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TransactionEntity
import com.example.data.repository.AccountSummary
import com.example.ui.components.JanAppHeader
import com.example.ui.components.PkrAmountTextField
import com.example.ui.components.TransactionRow
import com.example.ui.components.formatPkr
import com.example.ui.components.parsePkr
import com.example.ui.theme.BrandDarkBlue
import com.example.ui.theme.CardSurface
import com.example.ui.theme.CashGreen
import com.example.ui.theme.CashGreenBg
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.ExpenseRedBg
import com.example.ui.theme.SurfaceBackground
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun CashInOutScreen(
  summary: AccountSummary,
  transactions: List<TransactionEntity>,
  initialTab: String = "CASH_IN",
  onAddTransaction: (TransactionEntity) -> Unit,
  onBack: () -> Unit
) {
  var selectedTab by remember { mutableStateOf(initialTab) }
  var amountInput by remember { mutableStateOf("") }
  var descriptionInput by remember { mutableStateOf("") }
  var customerNameInput by remember { mutableStateOf("") }

  val isCashIn = selectedTab == "CASH_IN"
  val activeColor = if (isCashIn) CashGreen else ExpenseRed

  val cashTransactions = transactions.filter { it.type == "CASH_IN" || it.type == "CASH_OUT" }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(SurfaceBackground)
  ) {
    // Header
    Surface(
      color = BrandDarkBlue,
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        IconButton(onClick = onBack, modifier = Modifier.testTag("cash_back_button")) {
          Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = "Back",
            tint = Color.White
          )
        }
        Text(
          text = "Cash In / Cash Out",
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold,
          color = Color.White,
          modifier = Modifier.padding(start = 4.dp)
        )
      }
    }

    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp)
    ) {
      item {
        Spacer(modifier = Modifier.height(14.dp))

        // Tabs: Cash In (Green) vs Cash Out (Red)
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = if (isCashIn) CashGreen else Color.White,
            shadowElevation = 2.dp,
            modifier = Modifier
              .weight(1f)
              .clickable { selectedTab = "CASH_IN" }
          ) {
            Box(
              modifier = Modifier.padding(vertical = 12.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = "Cash In",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = if (isCashIn) Color.White else CashGreen
              )
            }
          }

          Surface(
            shape = RoundedCornerShape(10.dp),
            color = if (!isCashIn) ExpenseRed else Color.White,
            shadowElevation = 2.dp,
            modifier = Modifier
              .weight(1f)
              .clickable { selectedTab = "CASH_OUT" }
          ) {
            Box(
              modifier = Modifier.padding(vertical = 12.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = "Cash Out",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = if (!isCashIn) Color.White else ExpenseRed
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Today's Cash Summary Card (Matching reference design 7th screen)
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = CardSurface)
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Text(
              text = "Today's Cash Summary",
              fontSize = 15.sp,
              fontWeight = FontWeight.Bold,
              color = TextPrimary
            )

            Spacer(modifier = Modifier.height(12.dp))

            SummaryLine(
              label = "Total In",
              amount = summary.todayCashIn,
              icon = Icons.Default.Payments,
              iconColor = CashGreen,
              textColor = CashGreen
            )

            SummaryLine(
              label = "Total Out",
              amount = summary.todayCashOut,
              icon = Icons.Default.CallReceived,
              iconColor = ExpenseRed,
              textColor = ExpenseRed
            )

            SummaryLine(
              label = "Net Cash",
              amount = summary.todayNetCash,
              icon = Icons.Default.TrendingUp,
              iconColor = CashGreen,
              textColor = if (summary.todayNetCash >= 0) CashGreen else ExpenseRed
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Quick Entry Form for Cash In / Out
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = CardSurface)
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Text(
              text = "Quick ${if (isCashIn) "Cash In" else "Cash Out"} Entry",
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold,
              color = TextPrimary
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Robust Amount Entry Field
            PkrAmountTextField(
              value = amountInput,
              onValueChange = { amountInput = it },
              label = "Amount",
              placeholder = "0.00",
              quickSuggestions = listOf(100.0, 500.0, 1000.0, 2000.0, 5000.0)
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
              value = descriptionInput,
              onValueChange = { descriptionInput = it },
              label = { Text("Description") },
              placeholder = { Text(if (isCashIn) "e.g. Repair payment, Screen replacement" else "e.g. Shop tea, Tools purchase", color = TextMuted) },
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(12.dp),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = activeColor,
                unfocusedBorderColor = Color(0xFFCBD5E1),
                unfocusedContainerColor = Color(0xFFF8FAFC)
              )
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
              value = customerNameInput,
              onValueChange = { customerNameInput = it },
              label = { Text("Customer / Person (Optional)") },
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(12.dp),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = activeColor,
                unfocusedBorderColor = Color(0xFFCBD5E1),
                unfocusedContainerColor = Color(0xFFF8FAFC)
              )
            )

            Spacer(modifier = Modifier.height(14.dp))

            Button(
              onClick = {
                val amt = amountInput.parsePkr()
                if (amt > 0) {
                  val tx = TransactionEntity(
                    type = selectedTab,
                    subCategory = if (isCashIn) "Customer Payment" else "Shop Expense",
                    amount = amt,
                    accountAffected = "CASH",
                    description = if (descriptionInput.isNotBlank()) descriptionInput else if (isCashIn) "Cash Received" else "Cash Paid",
                    customerName = customerNameInput
                  )
                  onAddTransaction(tx)
                  amountInput = ""
                  descriptionInput = ""
                  customerNameInput = ""
                }
              },
              modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .testTag("submit_cash_button"),
              shape = RoundedCornerShape(10.dp),
              colors = ButtonDefaults.buttonColors(containerColor = activeColor)
            ) {
              Text(
                text = "Record ${if (isCashIn) "Cash In" else "Cash Out"}",
                fontWeight = FontWeight.Bold
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
          text = "Recent Transactions",
          fontSize = 15.sp,
          fontWeight = FontWeight.Bold,
          color = TextPrimary
        )

        Spacer(modifier = Modifier.height(8.dp))
      }

      items(cashTransactions) { tx ->
        TransactionRow(transaction = tx)
      }

      item {
        Spacer(modifier = Modifier.height(80.dp))
      }
    }
  }
}

@Composable
private fun SummaryLine(
  label: String,
  amount: Double,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  iconColor: Color,
  textColor: Color
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 6.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Box(
        modifier = Modifier
          .size(32.dp)
          .background(iconColor.copy(alpha = 0.15f), RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
      ) {
        Icon(icon, contentDescription = label, tint = iconColor, modifier = Modifier.size(18.dp))
      }
      Spacer(modifier = Modifier.width(10.dp))
      Text(label, fontSize = 14.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
    }

    Text(
      text = amount.formatPkr(),
      fontSize = 15.sp,
      fontWeight = FontWeight.Bold,
      color = textColor
    )
  }
}
