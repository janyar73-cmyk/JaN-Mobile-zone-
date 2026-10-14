package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BrandVibrantBlue
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import kotlin.math.roundToLong

/**
 * Robust PKR Amount & Price Input Field
 * Fixes all manual typing, cursor resetting, premature reformatting, and dropdown locking bugs.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PkrAmountTextField(
  value: String,
  onValueChange: (String) -> Unit,
  modifier: Modifier = Modifier,
  label: String = "Amount",
  placeholder: String = "0.00",
  testTag: String = "amount_input",
  isError: Boolean = false,
  errorMessage: String? = null,
  quickSuggestions: List<Double>? = null,
  enabled: Boolean = true,
  imeAction: ImeAction = ImeAction.Next,
  onImeAction: () -> Unit = {}
) {
  Column(modifier = modifier.fillMaxWidth()) {
    OutlinedTextField(
      value = value,
      onValueChange = { input ->
        // Robust Character-by-character validation:
        // 1. Allow empty input so user can backspace completely
        // 2. Allow single leading zero or digits
        // 3. Allow at most ONE decimal point
        // 4. Do NOT convert "12." into "12" or reformat during typing
        val cleanInput = input.replace(',', '.')
        if (cleanInput.isEmpty()) {
          onValueChange("")
        } else if (isValidMonetaryInput(cleanInput)) {
          onValueChange(cleanInput)
        }
      },
      modifier = Modifier
        .fillMaxWidth()
        .testTag(testTag),
      enabled = enabled,
      label = { Text(label) },
      placeholder = { Text(placeholder, color = TextMuted) },
      prefix = {
        Text(
          text = "Rs. ",
          fontWeight = FontWeight.Bold,
          color = BrandVibrantBlue,
          fontSize = 16.sp
        )
      },
      singleLine = true,
      isError = isError,
      keyboardOptions = KeyboardOptions(
        keyboardType = KeyboardType.Decimal,
        imeAction = imeAction
      ),
      keyboardActions = KeyboardActions(
        onDone = { onImeAction() },
        onNext = { onImeAction() }
      ),
      shape = RoundedCornerShape(12.dp),
      colors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = BrandVibrantBlue,
        unfocusedBorderColor = Color(0xFFCBD5E1),
        errorBorderColor = ExpenseRed,
        focusedContainerColor = Color.White,
        unfocusedContainerColor = Color(0xFFF8FAFC)
      )
    )

    if (isError && !errorMessage.isNullOrBlank()) {
      Text(
        text = errorMessage,
        color = ExpenseRed,
        style = MaterialTheme.typography.bodySmall,
        modifier = Modifier.padding(start = 8.dp, top = 4.dp)
      )
    }

    // Optional quick amount chips (non-intrusive suggestions: user can still edit freely)
    if (!quickSuggestions.isNullOrEmpty()) {
      FlowRow(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        quickSuggestions.forEach { suggestion ->
          val suggestionText = if (suggestion % 1.0 == 0.0) {
            suggestion.toInt().toString()
          } else {
            String.format(Locale.US, "%.2f", suggestion)
          }

          Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color(0xFFE2E8F0),
            modifier = Modifier.clickable {
              onValueChange(suggestionText)
            }
          ) {
            Text(
              text = "+Rs. $suggestionText",
              fontSize = 11.sp,
              fontWeight = FontWeight.Medium,
              color = TextPrimary,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
          }
        }
      }
    }
  }
}

/**
 * Validates whether string is a legal in-progress monetary input.
 * Supports: "", "0", "100", "1250", "2500.50", "10000", "12."
 * Limits to at most one decimal point and at most 2 digits after decimal.
 */
fun isValidMonetaryInput(text: String): Boolean {
  if (text.isEmpty()) return true
  // Must only contain digits and at most one dot
  var dotCount = 0
  for (i in text.indices) {
    val char = text[i]
    if (char == '.') {
      dotCount++
      if (dotCount > 1) return false
    } else if (!char.isDigit()) {
      return false
    }
  }

  // If there is a dot, check decimal places do not exceed 2
  val dotIndex = text.indexOf('.')
  if (dotIndex >= 0) {
    val decimals = text.length - 1 - dotIndex
    if (decimals > 2) return false
  }

  // Max realistic single transaction amount limit in PKR to avoid overflow
  if (text.length > 12) return false

  return true
}

/**
 * Parses user typed string into Double safely without crashing.
 */
fun String.parsePkr(): Double {
  if (this.isBlank()) return 0.0
  return try {
    this.trim().replace(',', '.').toDoubleOrNull() ?: 0.0
  } catch (e: Exception) {
    0.0
  }
}

/**
 * Parses user input into paisas (cents) as Long to avoid IEEE 754 precision inaccuracies.
 */
fun String.parsePkrToPaisas(): Long {
  val value = this.parsePkr()
  return (value * 100).roundToLong()
}

/**
 * Formats a numeric PKR amount into human-readable Pakistani Rupee format.
 * Examples:
 * 12450.0 -> "Rs. 12,450"
 * 2500.50 -> "Rs. 2,500.50"
 */
fun Double.formatPkr(includeDecimalsIfZero: Boolean = false): String {
  val symbols = DecimalFormatSymbols(Locale.US).apply {
    groupingSeparator = ','
    decimalSeparator = '.'
  }
  val pattern = if (!includeDecimalsIfZero && this % 1.0 == 0.0) {
    "#,##0"
  } else {
    "#,##0.00"
  }
  val formatter = DecimalFormat(pattern, symbols)
  return "Rs. ${formatter.format(this)}"
}

fun Long.formatPaisaToPkr(): String {
  val rupees = this / 100.0
  return rupees.formatPkr()
}
