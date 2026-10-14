package com.example.ui.screens

import android.content.Intent
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.InvoiceEntity
import com.example.data.model.InvoiceItemEntity
import com.example.ui.components.JanAppHeader
import com.example.ui.components.PkrAmountTextField
import com.example.ui.components.formatDatePkt
import com.example.ui.components.formatPkr
import com.example.ui.components.parsePkr
import com.example.ui.theme.BrandDarkBlue
import com.example.ui.theme.BrandVibrantBlue
import com.example.ui.theme.CardSurface
import com.example.ui.theme.CashGreen
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.SurfaceBackground
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.launch

data class InvoiceLineItemState(
  var description: String = "",
  var quantityText: String = "1",
  var unitPriceText: String = ""
) {
  val quantity: Int
    get() = quantityText.toIntOrNull()?.coerceAtLeast(1) ?: 1

  val unitPrice: Double
    get() = unitPriceText.parsePkr()

  val total: Double
    get() = quantity * unitPrice
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoicesScreen(
  invoices: List<InvoiceEntity>,
  onCreateInvoice: (InvoiceEntity, List<InvoiceItemEntity>) -> Unit,
  onBack: () -> Unit
) {
  var showCreateSheet by remember { mutableStateOf(false) }
  val context = LocalContext.current
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  val scope = rememberCoroutineScope()

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(SurfaceBackground)
  ) {
    JanAppHeader(
      title = "Invoices & Billing",
      subtitle = "Repair Bills & Product Invoices",
      showBackButton = true,
      onBackClick = onBack
    )

    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
      Button(
        onClick = { showCreateSheet = true },
        modifier = Modifier
          .fillMaxWidth()
          .height(48.dp)
          .testTag("create_new_invoice_button"),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = BrandVibrantBlue)
      ) {
        Icon(Icons.Default.Add, contentDescription = null)
        Spacer(modifier = Modifier.width(8.dp))
        Text("Create New Invoice", fontWeight = FontWeight.Bold)
      }
    }

    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp)
    ) {
      if (invoices.isEmpty()) {
        item {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .padding(top = 40.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "No invoices created yet. Tap button above to create one.",
              color = TextMuted,
              fontSize = 14.sp
            )
          }
        }
      } else {
        items(invoices) { inv ->
          InvoiceCard(
            invoice = inv,
            onShare = {
              val shareText = buildString {
                append("=== JAN MOBILE ZONE ===\n")
                append("Kanju, Swat | Mobile Repairing & Recharge\n")
                append("Invoice #: ${inv.invoiceNumber}\n")
                append("Date: ${formatDatePkt(inv.dateTimestamp)}\n")
                append("Customer: ${inv.customerName} (${inv.customerMobile})\n")
                append("Grand Total: ${inv.grandTotal.formatPkr()}\n")
                append("Paid: ${inv.paidAmount.formatPkr()}\n")
                append("Remaining Balance: ${inv.remainingBalance.formatPkr()}\n")
                append("Status: ${inv.paymentStatus}\n")
                append("Shukriya! (Thank you for your visit)")
              }
              val intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, shareText)
                putExtra(Intent.EXTRA_TITLE, "Invoice ${inv.invoiceNumber}")
                type = "text/plain"
              }
              context.startActivity(Intent.createChooser(intent, "Share Invoice"))
            }
          )
        }
      }

      item {
        Spacer(modifier = Modifier.height(80.dp))
      }
    }
  }

  // Create Invoice Sheet
  if (showCreateSheet) {
    ModalBottomSheet(
      onDismissRequest = { showCreateSheet = false },
      sheetState = sheetState
    ) {
      CreateInvoiceForm(
        onDismiss = {
          scope.launch { sheetState.hide() }.invokeOnCompletion {
            showCreateSheet = false
          }
        },
        onSave = { invoice, items ->
          onCreateInvoice(invoice, items)
          scope.launch { sheetState.hide() }.invokeOnCompletion {
            showCreateSheet = false
          }
        }
      )
    }
  }
}

@Composable
private fun InvoiceCard(
  invoice: InvoiceEntity,
  onShare: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 6.dp),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = CardSurface)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = invoice.invoiceNumber,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = BrandDarkBlue
          )
          Text(
            text = "${invoice.customerName} • ${invoice.customerMobile}",
            fontSize = 12.sp,
            color = TextSecondary
          )
        }

        // Status Badge
        val statusBg = when (invoice.paymentStatus) {
          "PAID" -> CashGreen.copy(alpha = 0.15f)
          "PARTIALLY_PAID" -> Color(0xFFFEF3C7)
          else -> ExpenseRed.copy(alpha = 0.15f)
        }
        val statusColor = when (invoice.paymentStatus) {
          "PAID" -> CashGreen
          "PARTIALLY_PAID" -> Color(0xFFD97706)
          else -> ExpenseRed
        }

        Surface(
          shape = RoundedCornerShape(8.dp),
          color = statusBg
        ) {
          Text(
            text = invoice.paymentStatus,
            color = statusColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Column {
          Text("Grand Total", fontSize = 11.sp, color = TextMuted)
          Text(invoice.grandTotal.formatPkr(), fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
        Column {
          Text("Paid", fontSize = 11.sp, color = TextMuted)
          Text(invoice.paidAmount.formatPkr(), fontWeight = FontWeight.Bold, fontSize = 14.sp, color = CashGreen)
        }
        Column {
          Text("Balance", fontSize = 11.sp, color = TextMuted)
          Text(invoice.remainingBalance.formatPkr(), fontWeight = FontWeight.Bold, fontSize = 14.sp, color = ExpenseRed)
        }
        IconButton(onClick = onShare) {
          Icon(Icons.Default.Share, contentDescription = "Share", tint = BrandVibrantBlue)
        }
      }
    }
  }
}

@Composable
fun CreateInvoiceForm(
  onDismiss: () -> Unit,
  onSave: (InvoiceEntity, List<InvoiceItemEntity>) -> Unit
) {
  var customerName by remember { mutableStateOf("") }
  var customerMobile by remember { mutableStateOf("") }
  var discountInput by remember { mutableStateOf("") }
  var paidInput by remember { mutableStateOf("") }

  // Line items state
  val items = remember {
    mutableStateListOf(
      InvoiceLineItemState("LCD / Display Screen", "1", "2500"),
      InvoiceLineItemState("Fitting Service Charges", "1", "500")
    )
  }

  val subtotal = items.sumOf { it.total }
  val discount = discountInput.parsePkr()
  val grandTotal = (subtotal - discount).coerceAtLeast(0.0)
  val paidAmount = paidInput.parsePkr()
  val remaining = (grandTotal - paidAmount).coerceAtLeast(0.0)

  val invoiceNumber = remember {
    "INV-${System.currentTimeMillis() % 100000}"
  }

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .verticalScroll(rememberScrollState())
      .padding(20.dp)
  ) {
    Text(
      text = "New Invoice ($invoiceNumber)",
      fontSize = 18.sp,
      fontWeight = FontWeight.Bold,
      color = BrandDarkBlue
    )

    Spacer(modifier = Modifier.height(14.dp))

    OutlinedTextField(
      value = customerName,
      onValueChange = { customerName = it },
      label = { Text("Customer Name") },
      placeholder = { Text("e.g. Asad Khan") },
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(12.dp)
    )

    Spacer(modifier = Modifier.height(10.dp))

    OutlinedTextField(
      value = customerMobile,
      onValueChange = { customerMobile = it },
      label = { Text("Mobile Number") },
      placeholder = { Text("0345-XXXXXXX") },
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(12.dp)
    )

    Spacer(modifier = Modifier.height(16.dp))

    Text(
      text = "Line Items",
      fontSize = 14.sp,
      fontWeight = FontWeight.Bold,
      color = TextPrimary
    )

    Spacer(modifier = Modifier.height(8.dp))

    items.forEachIndexed { index, itemState ->
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 4.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC))
      ) {
        Column(modifier = Modifier.padding(10.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            OutlinedTextField(
              value = itemState.description,
              onValueChange = {
                items[index] = itemState.copy(description = it)
              },
              label = { Text("Item / Service Description") },
              modifier = Modifier.weight(1f),
              shape = RoundedCornerShape(8.dp)
            )

            if (items.size > 1) {
              IconButton(onClick = { items.removeAt(index) }) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = ExpenseRed)
              }
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            // Quantity field
            OutlinedTextField(
              value = itemState.quantityText,
              onValueChange = {
                items[index] = itemState.copy(quantityText = it.filter { c -> c.isDigit() })
              },
              label = { Text("Qty") },
              modifier = Modifier.weight(0.4f),
              shape = RoundedCornerShape(8.dp)
            )

            // CRITICAL: Manual Unit Price Input Field! Never resets or locks!
            PkrAmountTextField(
              value = itemState.unitPriceText,
              onValueChange = {
                items[index] = itemState.copy(unitPriceText = it)
              },
              label = "Unit Price",
              modifier = Modifier.weight(0.6f)
            )
          }

          Text(
            text = "Total: ${itemState.total.formatPkr()}",
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = BrandVibrantBlue,
            modifier = Modifier
              .align(Alignment.End)
              .padding(top = 4.dp)
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(8.dp))

    Button(
      onClick = {
        items.add(InvoiceLineItemState("", "1", ""))
      },
      colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE2E8F0)),
      shape = RoundedCornerShape(8.dp)
    ) {
      Text("+ Add Another Item", color = TextPrimary, fontSize = 12.sp)
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Discount Field (Uses PkrAmountTextField)
    PkrAmountTextField(
      value = discountInput,
      onValueChange = { discountInput = it },
      label = "Discount (Optional)",
      placeholder = "0.00"
    )

    Spacer(modifier = Modifier.height(10.dp))

    // Paid Amount Field (Uses PkrAmountTextField)
    PkrAmountTextField(
      value = paidInput,
      onValueChange = { paidInput = it },
      label = "Amount Paid Now",
      placeholder = "0.00"
    )

    Spacer(modifier = Modifier.height(16.dp))

    // Invoice Summary Preview
    Card(
      modifier = Modifier.fillMaxWidth(),
      colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9))
    ) {
      Column(modifier = Modifier.padding(12.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text("Grand Total:")
          Text(grandTotal.formatPkr(), fontWeight = FontWeight.Bold)
        }
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text("Remaining Balance:")
          Text(remaining.formatPkr(), fontWeight = FontWeight.Bold, color = ExpenseRed)
        }
      }
    }

    Spacer(modifier = Modifier.height(18.dp))

    Button(
      onClick = {
        if (customerName.isBlank()) customerName = "Walk-in Customer"
        val status = if (remaining <= 0) "PAID" else if (paidAmount > 0) "PARTIALLY_PAID" else "UNPAID"
        val invoice = InvoiceEntity(
          invoiceNumber = invoiceNumber,
          customerName = customerName,
          customerMobile = customerMobile,
          subtotal = subtotal,
          discount = discount,
          grandTotal = grandTotal,
          paidAmount = paidAmount,
          paymentStatus = status
        )
        val invoiceItems = items.map {
          InvoiceItemEntity(
            invoiceId = 0,
            description = it.description.ifBlank { "Mobile Repair Item" },
            quantity = it.quantity,
            unitPrice = it.unitPrice,
            totalPrice = it.total
          )
        }
        onSave(invoice, invoiceItems)
      },
      modifier = Modifier
        .fillMaxWidth()
        .height(48.dp)
        .testTag("save_invoice_button"),
      shape = RoundedCornerShape(12.dp),
      colors = ButtonDefaults.buttonColors(containerColor = BrandVibrantBlue)
    ) {
      Text("Save Invoice & Record Payment", fontWeight = FontWeight.Bold)
    }

    Spacer(modifier = Modifier.height(30.dp))
  }
}
