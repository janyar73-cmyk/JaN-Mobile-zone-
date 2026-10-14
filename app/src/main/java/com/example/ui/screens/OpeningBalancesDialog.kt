package com.example.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.PkrAmountTextField
import com.example.ui.components.parsePkr
import com.example.ui.theme.BrandVibrantBlue

@Composable
fun OpeningBalancesDialog(
  initialCash: Double,
  initialEasyload: Double,
  initialEasypaisa: Double,
  onDismiss: () -> Unit,
  onSave: (cash: Double, easyload: Double, easypaisa: Double) -> Unit
) {
  var cashInput by remember { mutableStateOf(if (initialCash > 0) initialCash.toInt().toString() else "0") }
  var easyloadInput by remember { mutableStateOf(if (initialEasyload > 0) initialEasyload.toInt().toString() else "0") }
  var easypaisaInput by remember { mutableStateOf(if (initialEasypaisa > 0) initialEasypaisa.toInt().toString() else "0") }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Text(
        text = "Set Opening Balances",
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp
      )
    },
    text = {
      Column(modifier = Modifier.fillMaxWidth()) {
        Text(
          text = "Enter the starting balances for your shop accounts. These do not count as profit.",
          fontSize = 12.sp,
          color = androidx.compose.ui.graphics.Color.Gray
        )

        Spacer(modifier = Modifier.height(14.dp))

        PkrAmountTextField(
          value = cashInput,
          onValueChange = { cashInput = it },
          label = "Cash Opening Balance"
        )

        Spacer(modifier = Modifier.height(10.dp))

        PkrAmountTextField(
          value = easyloadInput,
          onValueChange = { easyloadInput = it },
          label = "Easyload Opening Balance"
        )

        Spacer(modifier = Modifier.height(10.dp))

        PkrAmountTextField(
          value = easypaisaInput,
          onValueChange = { easypaisaInput = it },
          label = "Easypaisa Opening Balance"
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          onSave(
            cashInput.parsePkr(),
            easyloadInput.parsePkr(),
            easypaisaInput.parsePkr()
          )
        },
        colors = ButtonDefaults.buttonColors(containerColor = BrandVibrantBlue)
      ) {
        Text("Save Balances", fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      OutlinedButton(onClick = onDismiss) {
        Text("Cancel")
      }
    }
  )
}
