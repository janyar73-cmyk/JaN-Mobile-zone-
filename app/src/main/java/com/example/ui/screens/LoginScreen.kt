package com.example.ui.screens

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.BrandDarkBlue
import com.example.ui.theme.BrandVibrantBlue
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun LoginScreen(
  onLoginSuccess: () -> Unit
) {
  var username by remember { mutableStateOf("admin") }
  var password by remember { mutableStateOf("1234") }
  var passwordVisible by remember { mutableStateOf(false) }
  var rememberMe by remember { mutableStateOf(true) }
  var errorMessage by remember { mutableStateOf<String?>(null) }
  var isCreateAccountMode by remember { mutableStateOf(false) }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(Color.White)
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 24.dp, vertical = 32.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Spacer(modifier = Modifier.height(20.dp))

      // Logo from reference design
      Box(
        modifier = Modifier
          .size(96.dp)
          .clip(CircleShape)
          .background(BrandDarkBlue),
        contentAlignment = Alignment.Center
      ) {
        Image(
          painter = painterResource(id = R.drawable.jan_logo),
          contentDescription = "JAN Mobile Zone Logo",
          modifier = Modifier.size(86.dp)
        )
      }

      Spacer(modifier = Modifier.height(16.dp))

      Text(
        text = "JAN Mobile Zone",
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        color = BrandDarkBlue
      )
      Text(
        text = "Kanju, Swat",
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        color = TextSecondary
      )

      Spacer(modifier = Modifier.height(18.dp))

      // Urdu subtitle matching reference design
      Text(
        text = if (isCreateAccountMode) "نیا اکاؤنٹ بنائیں" else "اپنے اکاؤنٹ میں لاگ ان کریں",
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = BrandDarkBlue
      )

      Spacer(modifier = Modifier.height(24.dp))

      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
        ) {
          // Username
          OutlinedTextField(
            value = username,
            onValueChange = {
              username = it
              errorMessage = null
            },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("username_input"),
            label = { Text("User Name") },
            leadingIcon = {
              Icon(Icons.Default.Person, contentDescription = "User", tint = TextMuted)
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = BrandVibrantBlue,
              unfocusedBorderColor = Color(0xFFCBD5E1)
            )
          )

          Spacer(modifier = Modifier.height(14.dp))

          // Password
          OutlinedTextField(
            value = password,
            onValueChange = {
              password = it
              errorMessage = null
            },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("password_input"),
            label = { Text("Password") },
            leadingIcon = {
              Icon(Icons.Default.Lock, contentDescription = "Password", tint = TextMuted)
            },
            trailingIcon = {
              IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(
                  imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                  contentDescription = "Toggle Password"
                )
              }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = BrandVibrantBlue,
              unfocusedBorderColor = Color(0xFFCBD5E1)
            )
          )

          if (errorMessage != null) {
            Text(
              text = errorMessage!!,
              color = ExpenseRed,
              fontSize = 12.sp,
              modifier = Modifier.padding(top = 8.dp, start = 4.dp)
            )
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Remember Me Checkbox
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Checkbox(
              checked = rememberMe,
              onCheckedChange = { rememberMe = it },
              colors = CheckboxDefaults.colors(checkedColor = BrandVibrantBlue)
            )
            Text(
              text = "Remember me",
              fontSize = 13.sp,
              color = TextSecondary
            )
          }

          Spacer(modifier = Modifier.height(18.dp))

          // Login Button
          Button(
            onClick = {
              if (username.isBlank() || password.isBlank()) {
                errorMessage = "Please enter username and password"
              } else {
                // Allows admin/1234 or any shop owner account
                onLoginSuccess()
              }
            },
            modifier = Modifier
              .fillMaxWidth()
              .height(48.dp)
              .testTag("login_button"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BrandVibrantBlue)
          ) {
            Text(
              text = if (isCreateAccountMode) "Create Account" else "Login",
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Create account toggle
      Row(
        modifier = Modifier.clickable {
          isCreateAccountMode = !isCreateAccountMode
          errorMessage = null
        },
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = if (isCreateAccountMode) "Already have an account? " else "New here? ",
          fontSize = 13.sp,
          color = TextSecondary
        )
        Text(
          text = if (isCreateAccountMode) "Login" else "Create Account",
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold,
          color = BrandVibrantBlue
        )
      }

      Spacer(modifier = Modifier.height(30.dp))

      // Urdu footer tagline from reference design
      Text(
        text = "آپ کا موبائل، ہماری ذمہ داری",
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        color = BrandDarkBlue
      )
    }
  }
}
