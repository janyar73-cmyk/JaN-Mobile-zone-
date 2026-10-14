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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TransactionEntity
import com.example.data.repository.AccountSummary
import com.example.ui.components.JanAppHeader
import com.example.ui.components.PkrAmountTextField
import com.example.ui.components.formatDatePkt
import com.example.ui.components.formatPkr
import com.example.ui.components.formatTimePkt
import com.example.ui.components.parsePkr
import com.example.ui.theme.BrandDarkBlue
import com.example.ui.theme.CardSurface
import com.example.ui.theme.CashGreen
import com.example.ui.theme.EasypaisaPurple
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.SurfaceBackground
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun EasypaisaScreen(
  summary: AccountSummary,
  transactions: List<TransactionEntity>,
  onAddTransaction: (TransactionEntity) -> Unit,
  onBack: () -> Unit
) {
  var selectedTab by remember { mutableStateOf("Received (Cash In)") }
  var amountInput by remember { mutableStateOf("") }
  var serviceChargeInput by remember { mutableStateOf("") }
  var customerNameInput by remember { mutableStateOf("") }
  var customerPhoneInput by remember { mutableStateOf("") }

  val easypaisaTransactions = transactions.filter { it.type == "EASYPAISA" }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(SurfaceBackground)
  ) {
    JanAppHeader(
      title = "Easypaisa",
      subtitle = "Branchless Banking & Transfers",
      showBackButton = true,
      onBackClick = onBack
    )

    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp)
    ) {
      item {
        Spacer(modifier = Modifier.height(14.dp))

        // Metrics Summary Card
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = CardSurface)
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            MetricRow("Opening Balance", summary.easypaisaOpening)
            MetricRow("Received (Cash In)", summary.easypaisaReceived)
            MetricRow("Paid Out", summary.easypaisaPaidOut)
            MetricRow("Service Charges", summary.easypaisaServiceCharges)
            MetricRow("Commission", summary.easypaisaCommission)
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Big Green Card: Current Balance
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = CashGreen)
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 16.dp, horizontal = 20.dp)
          ) {
            Text(
              text = "Current Balance",
              fontSize = 14.sp,
              color = Color.White.copy(alpha = 0.9f),
              fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = summary.easypaisaBalance.formatPkr(),
              fontSize = 26.sp,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Quick Easypaisa Transaction Tabs
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          listOf("Received (Cash In)", "Paid (Cash Out)", "Bill Payment").forEach { tab ->
            val isSelected = selectedTab == tab
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = if (isSelected) EasypaisaPurple else Color.White,
              shadowElevation = 1.dp,
              modifier = Modifier
                .weight(1f)
                .clickable { selectedTab = tab }
            ) {
              Box(
                modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = if (tab.contains("Received")) "Cash In" else if (tab.contains("Paid")) "Cash Out" else "Bill Pay",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (isSelected) Color.White else TextPrimary
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Quick Entry Card
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = CardSurface)
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Text(
              text = selectedTab,
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold,
              color = TextPrimary
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Robust Amount Entry Field
            PkrAmountTextField(
              value = amountInput,
              onValueChange = { amountInput = it },
              label = "Transaction Amount",
              placeholder = "0.00",
              quickSuggestions = listOf(500.0, 1000.0, 2500.0, 5000.0, 10000.0)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Service Charges Field
            PkrAmountTextField(
              value = serviceChargeInput,
              onValueChange = { serviceChargeInput = it },
              label = "Service Charges / Fee (Optional)",
              placeholder = "e.g. 20, 50",
              quickSuggestions = listOf(20.0, 50.0, 100.0)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
              onClick = {
                val amt = amountInput.parsePkr()
                if (amt > 0) {
                  val fee = serviceChargeInput.parsePkr()
                  val tx = TransactionEntity(
                    type = "EASYPAISA",
                    subCategory = selectedTab,
                    amount = amt,
                    serviceCharges = fee,
                    commission = fee * 0.6, // Typical shop fee split
                    accountAffected = "EASYPAISA",
                    description = "Easypaisa $selectedTab",
                    customerName = customerNameInput,
                    customerPhone = customerPhoneInput
                  )
                  onAddTransaction(tx)
                  amountInput = ""
                  serviceChargeInput = ""
                }
              },
              modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .testTag("submit_easypaisa_button"),
              shape = RoundedCornerShape(10.dp),
              colors = ButtonDefaults.buttonColors(containerColor = EasypaisaPurple)
            ) {
              Text("Record Easypaisa Transaction", fontWeight = FontWeight.Bold)
            }
          }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
          text = "Recent Easypaisa Transactions",
          fontSize = 15.sp,
          fontWeight = FontWeight.Bold,
          color = TextPrimary
        )

        Spacer(modifier = Modifier.height(8.dp))
      }

      items(easypaisaTransactions) { tx ->
        val isReceived = tx.subCategory.contains("Received", ignoreCase = true) || tx.subCategory.contains("Cash In", ignoreCase = true)
        val sign = if (isReceived) "+" else "-"
        val color = if (isReceived) CashGreen else ExpenseRed

        Card(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = CardSurface)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(EasypaisaPurple.copy(alpha = 0.15f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Wallet,
                contentDescription = tx.subCategory,
                tint = EasypaisaPurple,
                modifier = Modifier.size(20.dp)
              )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = tx.subCategory,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = TextPrimary
              )
              Text(
                text = "${formatDatePkt(tx.timestamp)} ${formatTimePkt(tx.timestamp)}",
                fontSize = 11.sp,
                color = TextMuted
              )
            }

            Text(
              text = "$sign${tx.amount.formatPkr()}",
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp,
              color = color
            )
          }
        }
      }

      item {
        Spacer(modifier = Modifier.height(80.dp))
      }
    }
  }
}

@Composable
private fun MetricRow(label: String, amount: Double) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 6.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(
      text = label,
      fontSize = 13.sp,
      color = TextSecondary
    )
    Text(
      text = amount.formatPkr(),
      fontSize = 14.sp,
      fontWeight = FontWeight.Bold,
      color = TextPrimary
    )
  }
}
