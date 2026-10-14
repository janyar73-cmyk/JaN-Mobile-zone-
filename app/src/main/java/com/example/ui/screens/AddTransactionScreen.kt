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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CallReceived
import androidx.compose.material.icons.filled.CellTower
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import com.example.ui.components.PkrAmountTextField
import com.example.ui.components.formatDatePkt
import com.example.ui.components.parsePkr
import com.example.ui.theme.BrandDarkBlue
import com.example.ui.theme.BrandVibrantBlue
import com.example.ui.theme.CardSurface
import com.example.ui.theme.CashGreen
import com.example.ui.theme.EasyloadBlue
import com.example.ui.theme.EasypaisaPurple
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.SurfaceBackground
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
  initialType: String = "CASH_IN",
  onSave: (TransactionEntity) -> Unit,
  onCancel: () -> Unit
) {
  var selectedType by remember { mutableStateOf(initialType) }

  // Manual typing states - raw strings so typing is NEVER interrupted
  var amountInput by remember { mutableStateOf("") }
  var commissionInput by remember { mutableStateOf("") }
  var serviceChargeInput by remember { mutableStateOf("") }
  var descriptionInput by remember { mutableStateOf("") }
  var customerNameInput by remember { mutableStateOf("") }
  var customerPhoneInput by remember { mutableStateOf("") }
  var referenceNumberInput by remember { mutableStateOf("") }

  // Easyload Provider selection
  var selectedProvider by remember { mutableStateOf("Jazz") }

  // Subcategory dropdown
  var expandedDropdown by remember { mutableStateOf(false) }
  val subcategories = when (selectedType) {
    "CASH_IN" -> listOf("Customer Payment", "Repair Service", "Mobile Sale", "Top Up", "Other Income")
    "CASH_OUT" -> listOf("Shop Purchase", "Expense", "Utility Bill", "Tea / Refreshment", "Personal Drawing", "Rent")
    "EASYLOAD" -> listOf("Jazz", "Zong", "Ufone", "Telenor", "Load Added")
    "EASYPAISA" -> listOf("Received from Customer (Cash In)", "Paid to Customer (Cash Out)", "Bill Payment", "Account Transfer")
    else -> listOf("General")
  }
  var selectedSubcategory by remember(selectedType) {
    mutableStateOf(subcategories.first())
  }

  // Validation errors
  var amountError by remember { mutableStateOf<String?>(null) }
  var isSaving by remember { mutableStateOf(false) }

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
        IconButton(onClick = onCancel, modifier = Modifier.testTag("back_button")) {
          Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = "Back",
            tint = Color.White
          )
        }
        Text(
          text = "Add Transaction",
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold,
          color = Color.White,
          modifier = Modifier.padding(start = 4.dp)
        )
      }
    }

    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(16.dp)
    ) {
      // 1. Transaction Type Selector (4 Pills matching reference image)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        TypePill(
          label = "Cash In",
          icon = Icons.Default.Payments,
          isSelected = selectedType == "CASH_IN",
          activeColor = CashGreen,
          modifier = Modifier.weight(1f),
          onClick = {
            selectedType = "CASH_IN"
            amountError = null
          }
        )

        TypePill(
          label = "Cash Out",
          icon = Icons.Default.CallReceived,
          isSelected = selectedType == "CASH_OUT",
          activeColor = ExpenseRed,
          modifier = Modifier.weight(1f),
          onClick = {
            selectedType = "CASH_OUT"
            amountError = null
          }
        )

        TypePill(
          label = "Easyload",
          icon = Icons.Default.CellTower,
          isSelected = selectedType == "EASYLOAD",
          activeColor = EasyloadBlue,
          modifier = Modifier.weight(1f),
          onClick = {
            selectedType = "EASYLOAD"
            amountError = null
          }
        )

        TypePill(
          label = "Easypaisa",
          icon = Icons.Default.Wallet,
          isSelected = selectedType == "EASYPAISA",
          activeColor = EasypaisaPurple,
          modifier = Modifier.weight(1f),
          onClick = {
            selectedType = "EASYPAISA"
            amountError = null
          }
        )
      }

      Spacer(modifier = Modifier.height(18.dp))

      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
        ) {
          // Date Display
          OutlinedTextField(
            value = formatDatePkt(System.currentTimeMillis()),
            onValueChange = {},
            readOnly = true,
            label = { Text("Date") },
            leadingIcon = {
              Icon(Icons.Default.CalendarToday, contentDescription = "Date", tint = TextMuted)
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
              unfocusedContainerColor = Color(0xFFF8FAFC)
            )
          )

          Spacer(modifier = Modifier.height(14.dp))

          // Subcategory selector
          ExposedDropdownMenuBox(
            expanded = expandedDropdown,
            onExpandedChange = { expandedDropdown = !expandedDropdown },
            modifier = Modifier.fillMaxWidth()
          ) {
            OutlinedTextField(
              value = selectedSubcategory,
              onValueChange = {},
              readOnly = true,
              label = { Text("Category / Type") },
              trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDropdown) },
              modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
              shape = RoundedCornerShape(12.dp),
              colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color(0xFFF8FAFC)
              )
            )
            ExposedDropdownMenu(
              expanded = expandedDropdown,
              onDismissRequest = { expandedDropdown = false }
            ) {
              subcategories.forEach { option ->
                DropdownMenuItem(
                  text = { Text(option) },
                  onClick = {
                    selectedSubcategory = option
                    expandedDropdown = false
                  }
                )
              }
            }
          }

          // If Easyload, provide provider selection
          if (selectedType == "EASYLOAD") {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
              text = "Select Network Provider",
              fontSize = 12.sp,
              fontWeight = FontWeight.Medium,
              color = TextSecondary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              listOf("Jazz", "Zong", "Ufone", "Telenor").forEach { prov ->
                val isProvSelected = selectedProvider == prov
                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = if (isProvSelected) EasyloadBlue else Color(0xFFF1F5F9),
                  modifier = Modifier
                    .weight(1f)
                    .clickable {
                      selectedProvider = prov
                      selectedSubcategory = prov
                    }
                ) {
                  Box(
                    modifier = Modifier.padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                  ) {
                    Text(
                      text = prov,
                      fontSize = 12.sp,
                      fontWeight = if (isProvSelected) FontWeight.Bold else FontWeight.Medium,
                      color = if (isProvSelected) Color.White else TextPrimary
                    )
                  }
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // 2. CRITICAL COMPONENT: Robust PkrAmountTextField
          // Supports full manual typing of 100, 1250, 2500.50, 10000 etc.
          PkrAmountTextField(
            value = amountInput,
            onValueChange = {
              amountInput = it
              amountError = null
            },
            label = "Amount",
            placeholder = "0.00",
            testTag = "amount_input_field",
            isError = amountError != null,
            errorMessage = amountError,
            quickSuggestions = listOf(100.0, 500.0, 1000.0, 2000.0, 5000.0)
          )

          // 3. Optional Commission / Profit Field for Easyload / Easypaisa
          if (selectedType == "EASYLOAD" || selectedType == "EASYPAISA") {
            Spacer(modifier = Modifier.height(14.dp))
            PkrAmountTextField(
              value = commissionInput,
              onValueChange = { commissionInput = it },
              label = "Commission / Profit (Optional)",
              placeholder = "0.00",
              testTag = "commission_input_field"
            )
          }

          // 4. Optional Service Charges for Easypaisa
          if (selectedType == "EASYPAISA") {
            Spacer(modifier = Modifier.height(14.dp))
            PkrAmountTextField(
              value = serviceChargeInput,
              onValueChange = { serviceChargeInput = it },
              label = "Service Charges (Optional)",
              placeholder = "0.00",
              testTag = "service_charge_input_field"
            )
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Description
          OutlinedTextField(
            value = descriptionInput,
            onValueChange = { descriptionInput = it },
            label = { Text("Description") },
            placeholder = { Text("e.g. Customer payment, Load sale etc.", color = TextMuted) },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("description_input"),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = BrandVibrantBlue,
              unfocusedBorderColor = Color(0xFFCBD5E1),
              focusedContainerColor = Color.White,
              unfocusedContainerColor = Color(0xFFF8FAFC)
            )
          )

          Spacer(modifier = Modifier.height(14.dp))

          // Customer Name & Phone (Optional)
          OutlinedTextField(
            value = customerNameInput,
            onValueChange = { customerNameInput = it },
            label = { Text("Customer Name / Reference (Optional)") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = BrandVibrantBlue,
              unfocusedBorderColor = Color(0xFFCBD5E1),
              focusedContainerColor = Color.White,
              unfocusedContainerColor = Color(0xFFF8FAFC)
            )
          )

          Spacer(modifier = Modifier.height(10.dp))

          OutlinedTextField(
            value = customerPhoneInput,
            onValueChange = { customerPhoneInput = it },
            label = { Text("Phone Number / Ref # (Optional)") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = BrandVibrantBlue,
              unfocusedBorderColor = Color(0xFFCBD5E1),
              focusedContainerColor = Color.White,
              unfocusedContainerColor = Color(0xFFF8FAFC)
            )
          )

          Spacer(modifier = Modifier.height(24.dp))

          // Save Button
          Button(
            onClick = {
              if (isSaving) return@Button
              val parsedAmount = amountInput.parsePkr()
              if (parsedAmount <= 0.0) {
                amountError = "Please enter a valid amount greater than Rs. 0"
                return@Button
              }

              isSaving = true
              val comm = commissionInput.parsePkr()
              val serv = serviceChargeInput.parsePkr()

              val accountAffected = when (selectedType) {
                "CASH_IN", "CASH_OUT" -> "CASH"
                "EASYLOAD" -> "EASYLOAD"
                "EASYPAISA" -> "EASYPAISA"
                else -> "CASH"
              }

              val tx = TransactionEntity(
                type = selectedType,
                subCategory = selectedSubcategory,
                amount = parsedAmount,
                costAmount = if (selectedType == "EASYLOAD") (parsedAmount - comm).coerceAtLeast(0.0) else 0.0,
                commission = comm,
                serviceCharges = serv,
                accountAffected = accountAffected,
                description = if (descriptionInput.isBlank()) selectedSubcategory else descriptionInput,
                customerName = customerNameInput,
                customerPhone = customerPhoneInput,
                referenceNumber = referenceNumberInput
              )
              onSave(tx)
            },
            modifier = Modifier
              .fillMaxWidth()
              .height(50.dp)
              .testTag("save_transaction_button"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BrandVibrantBlue)
          ) {
            Text(
              text = "Save",
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
          }

          Spacer(modifier = Modifier.height(8.dp))

          Button(
            onClick = onCancel,
            modifier = Modifier
              .fillMaxWidth()
              .height(44.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
          ) {
            Text(
              text = "Cancel",
              fontSize = 14.sp,
              color = TextSecondary
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(60.dp))
    }
  }
}

@Composable
private fun TypePill(
  label: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  isSelected: Boolean,
  activeColor: Color,
  modifier: Modifier = Modifier,
  onClick: () -> Unit
) {
  Surface(
    modifier = modifier.clickable(onClick = onClick),
    shape = RoundedCornerShape(10.dp),
    color = if (isSelected) activeColor else Color.White,
    shadowElevation = if (isSelected) 3.dp else 1.dp
  ) {
    Column(
      modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Icon(
        imageVector = icon,
        contentDescription = label,
        tint = if (isSelected) Color.White else activeColor,
        modifier = Modifier.size(20.dp)
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = label,
        fontSize = 11.sp,
        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
        color = if (isSelected) Color.White else TextPrimary
      )
    }
  }
}
