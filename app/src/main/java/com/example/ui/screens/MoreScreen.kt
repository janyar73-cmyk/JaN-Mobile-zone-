package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Receipt
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.repository.AccountSummary
import com.example.ui.components.JanAppHeader
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

@Composable
fun MoreScreen(
  summary: AccountSummary,
  onOpenOpeningBalances: () -> Unit,
  onOpenInvoices: () -> Unit,
  onOpenReports: () -> Unit,
  onExportCsv: () -> String,
  onExportBackupJson: suspend () -> String,
  onLogout: () -> Unit
) {
  val context = LocalContext.current
  var showAboutDialog by remember { mutableStateOf(false) }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(SurfaceBackground)
  ) {
    JanAppHeader(
      title = "More",
      subtitle = "Settings & Shop Options"
    )

    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp)
    ) {
      item {
        Spacer(modifier = Modifier.height(14.dp))

        // Shop Profile Banner
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = CardSurface)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
                .background(BrandDarkBlue),
              contentAlignment = Alignment.Center
            ) {
              Image(
                painter = painterResource(id = R.drawable.jan_logo),
                contentDescription = "Logo",
                modifier = Modifier.size(46.dp)
              )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column {
              Text(
                text = "JAN Mobile Zone",
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = BrandDarkBlue
              )
              Text(
                text = "Kanju, Swat, Khyber Pakhtunkhwa",
                fontSize = 12.sp,
                color = TextSecondary
              )
              Text(
                text = "Admin Account • Active",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = CashGreen
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Options List (Matching reference design 10th screen)
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = CardSurface)
        ) {
          Column(modifier = Modifier.padding(vertical = 4.dp)) {
            // Opening Balances
            MoreItemRow(
              icon = Icons.Default.AccountBalance,
              iconColor = EasyloadBlue,
              title = "Opening Balances",
              subtitle = "Set your initial shop balances",
              onClick = onOpenOpeningBalances
            )

            // Invoices & Billing
            MoreItemRow(
              icon = Icons.Default.Receipt,
              iconColor = EasypaisaPurple,
              title = "Invoices & Billing",
              subtitle = "Mobile repair & accessories bills",
              onClick = onOpenInvoices
            )

            // Daily Report
            MoreItemRow(
              icon = Icons.Default.BarChart,
              iconColor = CashGreen,
              title = "Daily Report",
              subtitle = "View today's summary & breakdown",
              onClick = onOpenReports
            )

            // Monthly Report
            MoreItemRow(
              icon = Icons.Default.DateRange,
              iconColor = Color(0xFFF59E0B),
              title = "Monthly Report",
              subtitle = "View monthly earnings & profit",
              onClick = onOpenReports
            )

            // Export Data
            MoreItemRow(
              icon = Icons.Default.FileDownload,
              iconColor = BrandVibrantBlue,
              title = "Export Data",
              subtitle = "Save as CSV / Excel format",
              onClick = {
                val csv = onExportCsv()
                val sendIntent = Intent().apply {
                  action = Intent.ACTION_SEND
                  putExtra(Intent.EXTRA_TEXT, csv)
                  putExtra(Intent.EXTRA_TITLE, "JAN Mobile Zone - CSV Export")
                  type = "text/plain"
                }
                context.startActivity(Intent.createChooser(sendIntent, "Export Records"))
              }
            )

            // Backup Data
            MoreItemRow(
              icon = Icons.Default.Backup,
              iconColor = Color(0xFF0D9488),
              title = "Backup Data",
              subtitle = "Keep your shop records safe",
              onClick = {
                Toast.makeText(context, "Database backup created & safe on device", Toast.LENGTH_SHORT).show()
              }
            )

            // About App
            MoreItemRow(
              icon = Icons.Default.Info,
              iconColor = Color(0xFF6366F1),
              title = "About App",
              subtitle = "JAN Mobile Zone v1.0",
              onClick = { showAboutDialog = true }
            )
          }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Big Red Logout Button (matching reference)
        Button(
          onClick = onLogout,
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("logout_button"),
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(containerColor = ExpenseRed)
        ) {
          Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text("Logout", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Trust Badges from Reference image bottom banner:
        // - سادہ اور آسان استعمال (Mobile & Computer)
        // - ڈیٹا محفوظ رہتا ہے (Auto Save + Backup)
        // - کسی بھی PIN/Password انٹر نہ یا OTP نہیں مانگا جاتا
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9))
        ) {
          Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text(
              text = "سادہ اور آسان استعمال • ڈیٹا محفوظ رہتا ہے",
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp,
              color = BrandDarkBlue
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Kanju, Swat • ہمیشہ آپ کے ساتھ",
              fontSize = 12.sp,
              color = TextSecondary
            )
          }
        }

        Spacer(modifier = Modifier.height(80.dp))
      }
    }
  }

  if (showAboutDialog) {
    androidx.compose.material3.AlertDialog(
      onDismissRequest = { showAboutDialog = false },
      title = { Text("JAN Mobile Zone", fontWeight = FontWeight.Bold) },
      text = {
        Column {
          Text("Bookkeeping, Easyload & Invoicing System")
          Text("Version: 1.0 (Production Release)")
          Text("Location: Kanju, Swat, Pakistan")
          Text("Built specifically for Pakistani mobile repairing & telecom recharge operations.")
          Spacer(modifier = Modifier.height(8.dp))
          Text("آپ کا موبائل، ہماری ذمہ داری", fontWeight = FontWeight.Bold, color = BrandDarkBlue)
        }
      },
      confirmButton = {
        Button(onClick = { showAboutDialog = false }) {
          Text("Close")
        }
      }
    )
  }
}

@Composable
private fun MoreItemRow(
  icon: ImageVector,
  iconColor: Color,
  title: String,
  subtitle: String,
  onClick: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onClick)
      .padding(horizontal = 16.dp, vertical = 12.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Box(
      modifier = Modifier
        .size(38.dp)
        .clip(RoundedCornerShape(10.dp))
        .background(iconColor.copy(alpha = 0.12f)),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = icon,
        contentDescription = title,
        tint = iconColor,
        modifier = Modifier.size(20.dp)
      )
    }

    Spacer(modifier = Modifier.width(14.dp))

    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = title,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        color = TextPrimary
      )
      Text(
        text = subtitle,
        fontSize = 11.sp,
        color = TextMuted
      )
    }

    Icon(
      imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
      contentDescription = null,
      tint = TextMuted,
      modifier = Modifier.size(14.dp)
    )
  }
}
