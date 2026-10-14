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
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
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
import com.example.ui.components.JanAppHeader
import com.example.ui.components.TransactionRow
import com.example.ui.components.formatDatePkt
import com.example.ui.components.formatPkr
import com.example.ui.components.formatTimePkt
import com.example.ui.theme.BrandDarkBlue
import com.example.ui.theme.BrandVibrantBlue
import com.example.ui.theme.CardSurface
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.SurfaceBackground
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun TransactionHistoryScreen(
  transactions: List<TransactionEntity>,
  onReverseTransaction: (Long) -> Unit,
  onDeleteTransaction: (TransactionEntity) -> Unit,
  onBack: () -> Unit
) {
  var selectedFilter by remember { mutableStateOf("All") }
  var searchQuery by remember { mutableStateOf("") }
  var selectedTransactionForDetail by remember { mutableStateOf<TransactionEntity?>(null) }

  val filteredTransactions by remember(transactions, selectedFilter, searchQuery) {
    derivedStateOf {
      transactions.filter { tx ->
        val matchesCategory = when (selectedFilter) {
          "Cash" -> tx.type == "CASH_IN" || tx.type == "CASH_OUT"
          "Easyload" -> tx.type == "EASYLOAD"
          "Easypaisa" -> tx.type == "EASYPAISA"
          else -> true
        }
        val matchesSearch = if (searchQuery.isBlank()) true else {
          tx.description.contains(searchQuery, ignoreCase = true) ||
            tx.subCategory.contains(searchQuery, ignoreCase = true) ||
            tx.customerName.contains(searchQuery, ignoreCase = true) ||
            tx.customerPhone.contains(searchQuery, ignoreCase = true) ||
            tx.amount.toString().contains(searchQuery)
        }
        matchesCategory && matchesSearch
      }
    }
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(SurfaceBackground)
  ) {
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
        IconButton(onClick = onBack, modifier = Modifier.testTag("history_back_button")) {
          Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
        }
        Text(
          text = "Transaction History",
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold,
          color = Color.White,
          modifier = Modifier.padding(start = 4.dp)
        )
      }
    }

    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
      // Filter Pills: All, Cash, Easyload, Easypaisa
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        listOf("All", "Cash", "Easyload", "Easypaisa").forEach { filter ->
          val isSelected = selectedFilter == filter
          Surface(
            shape = RoundedCornerShape(20.dp),
            color = if (isSelected) BrandVibrantBlue else Color.White,
            shadowElevation = 1.dp,
            modifier = Modifier.clickable { selectedFilter = filter }
          ) {
            Text(
              text = filter,
              fontSize = 12.sp,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
              color = if (isSelected) Color.White else TextPrimary,
              modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Search Box
      OutlinedTextField(
        value = searchQuery,
        onValueChange = { searchQuery = it },
        placeholder = { Text("Search by description, customer...", color = TextMuted) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = TextMuted) },
        trailingIcon = {
          if (searchQuery.isNotEmpty()) {
            IconButton(onClick = { searchQuery = "" }) {
              Icon(Icons.Default.Close, contentDescription = "Clear", tint = TextMuted)
            }
          }
        },
        modifier = Modifier
          .fillMaxWidth()
          .testTag("search_input"),
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = BrandVibrantBlue,
          unfocusedBorderColor = Color(0xFFCBD5E1),
          focusedContainerColor = Color.White,
          unfocusedContainerColor = Color.White
        )
      )
    }

    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp)
    ) {
      if (filteredTransactions.isEmpty()) {
        item {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .padding(top = 40.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "No transactions found",
              color = TextMuted,
              fontSize = 14.sp
            )
          }
        }
      } else {
        items(filteredTransactions) { tx ->
          TransactionRow(
            transaction = tx,
            onClick = { selectedTransactionForDetail = tx }
          )
        }
      }

      item {
        Spacer(modifier = Modifier.height(80.dp))
      }
    }
  }

  // Transaction Detail & Action Modal
  selectedTransactionForDetail?.let { tx ->
    AlertDialog(
      onDismissRequest = { selectedTransactionForDetail = null },
      title = {
        Text(
          text = "Transaction #${tx.id}",
          fontWeight = FontWeight.Bold,
          fontSize = 18.sp
        )
      },
      text = {
        Column(modifier = Modifier.fillMaxWidth()) {
          Text(text = "Type: ${tx.type} (${tx.subCategory})", fontWeight = FontWeight.Medium)
          Text(text = "Amount: ${tx.amount.formatPkr()}", fontWeight = FontWeight.Bold, color = BrandVibrantBlue)
          if (tx.commission > 0) {
            Text(text = "Commission: ${tx.commission.formatPkr()}")
          }
          if (tx.serviceCharges > 0) {
            Text(text = "Service Charges: ${tx.serviceCharges.formatPkr()}")
          }
          Text(text = "Account: ${tx.accountAffected}")
          Text(text = "Description: ${tx.description}")
          if (tx.customerName.isNotBlank()) {
            Text(text = "Customer: ${tx.customerName}")
          }
          if (tx.customerPhone.isNotBlank()) {
            Text(text = "Phone/Ref: ${tx.customerPhone}")
          }
          Text(text = "Time: ${formatDatePkt(tx.timestamp)} ${formatTimePkt(tx.timestamp)}")
          if (tx.isReversed) {
            Text(text = "STATUS: REVERSED", color = ExpenseRed, fontWeight = FontWeight.Bold)
          }
        }
      },
      confirmButton = {
        Button(
          onClick = {
            onReverseTransaction(tx.id)
            selectedTransactionForDetail = null
          },
          colors = ButtonDefaults.buttonColors(containerColor = if (tx.isReversed) BrandVibrantBlue else Color(0xFFF59E0B))
        ) {
          Icon(Icons.Default.Replay, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text(if (tx.isReversed) "Un-Reverse" else "Reverse")
        }
      },
      dismissButton = {
        OutlinedButton(
          onClick = {
            onDeleteTransaction(tx)
            selectedTransactionForDetail = null
          },
          colors = ButtonDefaults.outlinedButtonColors(contentColor = ExpenseRed)
        ) {
          Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Delete")
        }
      }
    )
  }
}
