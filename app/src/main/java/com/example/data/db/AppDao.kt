package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.AccountBalanceEntity
import com.example.data.model.InvoiceEntity
import com.example.data.model.InvoiceItemEntity
import com.example.data.model.TransactionEntity
import com.example.data.model.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
  // Transactions
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertTransaction(transaction: TransactionEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertTransactions(transactions: List<TransactionEntity>)

  @Update
  suspend fun updateTransaction(transaction: TransactionEntity)

  @Delete
  suspend fun deleteTransaction(transaction: TransactionEntity)

  @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
  fun getAllTransactions(): Flow<List<TransactionEntity>>

  @Query("SELECT * FROM transactions WHERE type = :type ORDER BY timestamp DESC")
  fun getTransactionsByType(type: String): Flow<List<TransactionEntity>>

  @Query("SELECT * FROM transactions WHERE timestamp >= :startTime AND timestamp <= :endTime ORDER BY timestamp DESC")
  fun getTransactionsBetween(startTime: Long, endTime: Long): Flow<List<TransactionEntity>>

  @Query("SELECT * FROM transactions WHERE id = :id LIMIT 1")
  suspend fun getTransactionById(id: Long): TransactionEntity?

  @Query("DELETE FROM transactions")
  suspend fun clearAllTransactions()

  // Opening Balances
  @Query("SELECT * FROM account_balances")
  fun getAllBalances(): Flow<List<AccountBalanceEntity>>

  @Query("SELECT * FROM account_balances WHERE accountKey = :key LIMIT 1")
  suspend fun getBalanceByKey(key: String): AccountBalanceEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun setBalance(balance: AccountBalanceEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun setBalances(balances: List<AccountBalanceEntity>)

  // Invoices
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertInvoice(invoice: InvoiceEntity): Long

  @Update
  suspend fun updateInvoice(invoice: InvoiceEntity)

  @Delete
  suspend fun deleteInvoice(invoice: InvoiceEntity)

  @Query("SELECT * FROM invoices ORDER BY dateTimestamp DESC")
  fun getAllInvoices(): Flow<List<InvoiceEntity>>

  @Query("SELECT * FROM invoices WHERE id = :id LIMIT 1")
  suspend fun getInvoiceById(id: Long): InvoiceEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertInvoiceItems(items: List<InvoiceItemEntity>)

  @Query("SELECT * FROM invoice_items WHERE invoiceId = :invoiceId")
  fun getInvoiceItems(invoiceId: Long): Flow<List<InvoiceItemEntity>>

  @Query("DELETE FROM invoice_items WHERE invoiceId = :invoiceId")
  suspend fun deleteInvoiceItems(invoiceId: Long)

  // Users / Auth
  @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
  suspend fun getUserByUsername(username: String): UserEntity?

  @Query("SELECT * FROM users LIMIT 1")
  suspend fun getFirstUser(): UserEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertUser(user: UserEntity): Long

  @Update
  suspend fun updateUser(user: UserEntity)
}
