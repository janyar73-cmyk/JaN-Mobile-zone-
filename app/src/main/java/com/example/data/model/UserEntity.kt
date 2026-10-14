package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val username: String,
  val passwordHash: String,
  val shopName: String = "JAN Mobile Zone",
  val location: String = "Kanju, Swat",
  val phone: String = "+92 345 0000000",
  val isRemembered: Boolean = true
)
