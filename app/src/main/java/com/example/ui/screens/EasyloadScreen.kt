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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CellTower
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import com.example.ui.theme.BrandVibrantBlue
import com.example.ui.theme.CardSurface
import com.example.ui.theme.CashGreen
import com.example.ui.theme.EasyloadBlue
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.SurfaceBackground
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun EasyloadScreen(
  summary: AccountSummary,
  transactions: List<TransactionEntity>,
  onAddTransaction: (TransactionEntity) -> Unit,
  onBack: () -> Unit
) {
  var showQuickDialog by remember { mutableStateOf(false) }
  var selectedProvider by remember { mutableStateOf("Jazz") }
  var amountInput by remember { mutableStateOf("") }
  var customerPhone by remember { mutableStateOf("") }
  var commissionRate by remember { mutableStateOf(0.025) }

  val easyloadTransactions = transactions.filter { it.type == "EASYLOAD" }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(SurfaceBackground)
  ) {
    JanAppHeader(
      title = "Easyload",
      subtitle = "Telecom Balance & Sales",
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
            MetricRow(label = "Opening Balance", amount = summary.easyloadOpening)
            MetricRow(label = "Load Sales (Total)", amount = summary.easyloadSales)
            MetricRow(label = "Balance Deduction", amount = summary.easyloadDeduction)
            MetricRow(label = "Commission", amount = summary.easyloadCommission)
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Big Green Card: Remaining Balance
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
              text = "Remaining Balance",
              fontSize = 14.sp,
              color = Color.White.copy(alpha = 0.9f),
              fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = summary.easyloadBalance.formatPkr(),
              fontSize = 26.sp,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Provider Selector Pills
        Text(
          text = "Telecom Networks",
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold,
          color = TextPrimary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          listOf("Jazz", "Zong", "Ufone", "Telenor").forEach { prov ->
            val isSelected = selectedProvider == prov
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = if (isSelected) EasyloadBlue else Color.White,
              shadowElevation = 1.dp,
              modifier = Modifier
                .weight(1f)
                .clickable { selectedProvider = prov }
            ) {
              Box(
                modifier = Modifier.padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = prov,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (isSelected) Color.White else TextPrimary
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Quick Load Form (Uses PkrAmountTextField!)
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = CardSurface)
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Text(
              text = "Send $selectedProvider Easyload",
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold,
              color = TextPrimary
            )
            Spacer(modifier = Modifier.height(10.dp))

            PkrAmountTextField(
              value = amountInput,
              onValueChange = { amountInput = it },
              label = "Load Amount",
              placeholder = "e.g. 100, 500, 1000",
              quickSuggestions = listOf(100.0, 200.0, 500.0, 1000.0)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
              onClick = {
                val amt = amountInput.parsePkr()
                if (amt > 0) {
                  val comm = amt * commissionRate
                  val cost = amt - comm
                  val tx = TransactionEntity(
                    type = "EASYLOAD",
                    subCategory = selectedProvider,
                    amount = amt,
                    costAmount = cost,
                    commission = comm,
                    accountAffected = "EASYLOAD",
                    description = "$selectedProvider Easyload Sale",
                    customerPhone = customerPhone
                  )
                  onAddTransaction(tx)
                  amountInput = ""
                }
              },
              modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .testTag("send_easyload_button"),
              shape = RoundedCornerShape(10.dp),
              colors = ButtonDefaults.buttonColors(containerColor = EasyloadBlue)
            ) {
              Text("Send Load & Record Sale", fontWeight = FontWeight.Bold)
            }
          }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
          text = "Recent Easyload Transactions",
          fontSize = 15.sp,
          fontWeight = FontWeight.Bold,
          color = TextPrimary
        )

        Spacer(modifier = Modifier.height(8.dp))
      }

      items(easyloadTransactions) { tx ->
        EasyloadRowItem(tx)
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

@Composable
private fun EasyloadRowItem(tx: TransactionEntity) {
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
          .background(EasyloadBlue.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.CellTower,
          contentDescription = tx.subCategory,
          tint = EasyloadBlue,
          modifier = Modifier.size(20.dp)
        )
      }

      Spacer(modifier = Modifier.width(12.dp))

      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = "${tx.subCategory} ${tx.amount.toInt()}",
          fontWeight = FontWeight.Bold,
          fontSize = 14.sp,
          color = TextPrimary
        )
        Text(
          text = "${formatDatePkt(tx.timestamp)} ${formatTimePkt(tx.timestamp)}",
          fontSize = 11.sp,
          color = TextMuted
        )
      }

      Text(
        text = "-${tx.amount.formatPkr()}",
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        color = ExpenseRed
      )
    }
  }
}
