package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CallReceived
import androidx.compose.material.icons.filled.CellTower
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TransactionEntity
import com.example.ui.theme.AmberProfit
import com.example.ui.theme.AmberProfitBg
import com.example.ui.theme.BrandDarkBlue
import com.example.ui.theme.BrandNavy
import com.example.ui.theme.BrandVibrantBlue
import com.example.ui.theme.CardSurface
import com.example.ui.theme.CashGreen
import com.example.ui.theme.EasyloadBlue
import com.example.ui.theme.EasypaisaPurple
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.PinkProfit
import com.example.ui.theme.PinkProfitBg
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * Top App Header for JAN Mobile Zone matching reference design
 */
@Composable
fun JanAppHeader(
  title: String = "JAN Mobile Zone",
  subtitle: String = "Kanju, Swat",
  showBackButton: Boolean = false,
  onBackClick: () -> Unit = {},
  onNotificationClick: () -> Unit = {},
  onMenuClick: () -> Unit = {}
) {
  Surface(
    color = BrandDarkBlue,
    modifier = Modifier.fillMaxWidth()
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 12.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      if (showBackButton) {
        IconButton(
          onClick = onBackClick,
          modifier = Modifier.testTag("header_back_button")
        ) {
          Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = "Back",
            tint = Color.White
          )
        }
      } else {
        IconButton(
          onClick = onMenuClick,
          modifier = Modifier.testTag("header_menu_button")
        ) {
          Icon(
            imageVector = Icons.Default.Menu,
            contentDescription = "Menu",
            tint = Color.White
          )
        }
      }

      Spacer(modifier = Modifier.width(8.dp))

      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = title,
          fontWeight = FontWeight.Bold,
          fontSize = 18.sp,
          color = Color.White
        )
        Text(
          text = subtitle,
          fontSize = 12.sp,
          color = Color(0xFFCBD5E1)
        )
      }

      IconButton(
        onClick = onNotificationClick,
        modifier = Modifier.testTag("header_notification_button")
      ) {
        Icon(
          imageVector = Icons.Default.Notifications,
          contentDescription = "Notifications",
          tint = Color.White
        )
      }
    }
  }
}

/**
 * Bottom Navigation Bar with prominent elevated center (+) Add action
 */
@Composable
fun JanBottomNavBar(
  currentScreen: String,
  onScreenSelect: (String) -> Unit
) {
  Surface(
    modifier = Modifier.fillMaxWidth(),
    shadowElevation = 12.dp,
    color = CardSurface
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 4.dp),
      horizontalArrangement = Arrangement.SpaceAround,
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Home
      BottomNavItem(
        icon = Icons.Default.Home,
        label = "Home",
        isSelected = currentScreen == "home",
        onClick = { onScreenSelect("home") }
      )

      // Transactions
      BottomNavItem(
        icon = Icons.Default.ReceiptLong,
        label = "Transactions",
        isSelected = currentScreen == "transactions",
        onClick = { onScreenSelect("transactions") }
      )

      // (+) Add Button prominent center pill/circle
      Box(
        modifier = Modifier
          .offset(y = (-8).dp)
          .size(52.dp)
          .shadow(8.dp, CircleShape)
          .clip(CircleShape)
          .background(BrandVibrantBlue)
          .clickable { onScreenSelect("add_transaction") }
          .testTag("nav_add_button"),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.Add,
          contentDescription = "Add Transaction",
          tint = Color.White,
          modifier = Modifier.size(28.dp)
        )
      }

      // Reports
      BottomNavItem(
        icon = Icons.Default.BarChart,
        label = "Reports",
        isSelected = currentScreen == "reports",
        onClick = { onScreenSelect("reports") }
      )

      // More
      BottomNavItem(
        icon = Icons.Default.MoreHoriz,
        label = "More",
        isSelected = currentScreen == "more",
        onClick = { onScreenSelect("more") }
      )
    }
  }
}

@Composable
private fun BottomNavItem(
  icon: ImageVector,
  label: String,
  isSelected: Boolean,
  onClick: () -> Unit
) {
  Column(
    modifier = Modifier
      .clickable(onClick = onClick)
      .padding(horizontal = 12.dp, vertical = 6.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Icon(
      imageVector = icon,
      contentDescription = label,
      tint = if (isSelected) BrandVibrantBlue else TextMuted,
      modifier = Modifier.size(22.dp)
    )
    Text(
      text = label,
      fontSize = 11.sp,
      fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
      color = if (isSelected) BrandVibrantBlue else TextMuted
    )
  }
}

/**
 * Large Account Balance Card (Cash, Easyload, Easypaisa) matching reference design
 */
@Composable
fun AccountBalanceCard(
  title: String,
  amount: Double,
  icon: ImageVector,
  backgroundGradient: Brush,
  onClick: () -> Unit = {}
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp)
      .clickable(onClick = onClick),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .background(backgroundGradient)
        .padding(horizontal = 18.dp, vertical = 14.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .size(44.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White.copy(alpha = 0.2f)),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = icon,
            contentDescription = title,
            tint = Color.White,
            modifier = Modifier.size(24.dp)
          )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column {
          Text(
            text = title,
            color = Color.White.copy(alpha = 0.9f),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = amount.formatPkr(),
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }
  }
}

/**
 * Profit Summary Card
 */
@Composable
fun ProfitCard(
  title: String,
  amount: Double,
  icon: ImageVector,
  containerColor: Color,
  textColor: Color,
  modifier: Modifier = Modifier,
  onClick: () -> Unit = {}
) {
  Card(
    modifier = modifier.clickable(onClick = onClick),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = containerColor)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp, vertical = 10.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Icon(
        imageVector = icon,
        contentDescription = title,
        tint = textColor,
        modifier = Modifier.size(22.dp)
      )
      Spacer(modifier = Modifier.width(8.dp))
      Column {
        Text(
          text = title,
          fontSize = 11.sp,
          color = TextSecondary,
          fontWeight = FontWeight.Medium
        )
        Text(
          text = amount.formatPkr(),
          fontSize = 15.sp,
          fontWeight = FontWeight.Bold,
          color = textColor
        )
      }
    }
  }
}

/**
 * Quick Action Button matching the 2x2 grid in reference design
 */
@Composable
fun QuickActionButton(
  title: String,
  icon: ImageVector,
  iconColor: Color,
  backgroundColor: Color,
  modifier: Modifier = Modifier,
  onClick: () -> Unit
) {
  Card(
    modifier = modifier.clickable(onClick = onClick),
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = backgroundColor)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 14.dp, vertical = 14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(36.dp)
          .clip(RoundedCornerShape(8.dp))
          .background(iconColor.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = title,
          tint = iconColor,
          modifier = Modifier.size(20.dp)
        )
      }
      Spacer(modifier = Modifier.width(12.dp))
      Text(
        text = title,
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold,
        color = TextPrimary
      )
    }
  }
}

/**
 * Transaction List Item Row
 */
@Composable
fun TransactionRow(
  transaction: TransactionEntity,
  onClick: () -> Unit = {}
) {
  val isPositive = when (transaction.type) {
    "CASH_IN" -> true
    "CASH_OUT" -> false
    "EASYLOAD" -> transaction.subCategory == "Load Added"
    "EASYPAISA" -> transaction.subCategory.contains("Received", ignoreCase = true)
    else -> true
  }

  val displaySign = if (isPositive) "+" else "-"
  val displayColor = if (isPositive) CashGreen else ExpenseRed

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp)
      .clickable(onClick = onClick),
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = CardSurface)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 14.dp, vertical = 12.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Type Icon
      val icon = when (transaction.type) {
        "CASH_IN" -> Icons.Default.Payments
        "CASH_OUT" -> Icons.Default.CallReceived
        "EASYLOAD" -> Icons.Default.CellTower
        "EASYPAISA" -> Icons.Default.Wallet
        else -> Icons.Default.ReceiptLong
      }
      val iconBg = when (transaction.type) {
        "CASH_IN" -> CashGreen.copy(alpha = 0.15f)
        "CASH_OUT" -> ExpenseRed.copy(alpha = 0.15f)
        "EASYLOAD" -> EasyloadBlue.copy(alpha = 0.15f)
        "EASYPAISA" -> EasypaisaPurple.copy(alpha = 0.15f)
        else -> Color(0xFFE2E8F0)
      }
      val iconTint = when (transaction.type) {
        "CASH_IN" -> CashGreen
        "CASH_OUT" -> ExpenseRed
        "EASYLOAD" -> EasyloadBlue
        "EASYPAISA" -> EasypaisaPurple
        else -> BrandDarkBlue
      }

      Box(
        modifier = Modifier
          .size(40.dp)
          .clip(CircleShape)
          .background(iconBg),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = transaction.type,
          tint = iconTint,
          modifier = Modifier.size(20.dp)
        )
      }

      Spacer(modifier = Modifier.width(12.dp))

      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = if (transaction.customerName.isNotBlank()) "${transaction.subCategory} - ${transaction.customerName}" else transaction.subCategory,
          fontWeight = FontWeight.SemiBold,
          fontSize = 14.sp,
          color = TextPrimary
        )
        Text(
          text = "${formatDatePkt(transaction.timestamp)} ${formatTimePkt(transaction.timestamp)}",
          fontSize = 11.sp,
          color = TextMuted
        )
      }

      Column(horizontalAlignment = Alignment.End) {
        Text(
          text = "$displaySign${transaction.amount.formatPkr()}",
          fontWeight = FontWeight.Bold,
          fontSize = 14.sp,
          color = displayColor
        )
        if (transaction.commission > 0) {
          Text(
            text = "Profit: ${transaction.commission.formatPkr()}",
            fontSize = 10.sp,
            color = AmberProfit,
            fontWeight = FontWeight.Medium
          )
        }
      }
    }
  }
}

fun formatDatePkt(timestamp: Long): String {
  val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
  sdf.timeZone = TimeZone.getTimeZone("Asia/Karachi")
  return sdf.format(Date(timestamp))
}

fun formatTimePkt(timestamp: Long): String {
  val sdf = SimpleDateFormat("hh:mm a", Locale.ENGLISH)
  sdf.timeZone = TimeZone.getTimeZone("Asia/Karachi")
  return sdf.format(Date(timestamp))
}
