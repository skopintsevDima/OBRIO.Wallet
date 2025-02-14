package ua.obrio.common.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import ua.obrio.common.data.db.dao.AccountDao
import ua.obrio.common.data.db.dao.TransactionDao
import ua.obrio.common.data.db.entity.AccountEntity
import ua.obrio.common.data.db.entity.TransactionEntity
import ua.obrio.common.presentation.util.Constants.Database.DATABASE_VERSION

@Database(
    entities = [AccountEntity::class, TransactionEntity::class],
    version = DATABASE_VERSION,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun accountDao(): AccountDao
    abstract fun transactionDao(): TransactionDao
}