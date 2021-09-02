package com.zanoapp.applediseaseIdentification.localDataPersistence.transactionsDB

import android.app.Application
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.zanoapp.applediseaseIdentification.localDataPersistence.userDB.UserDao
import com.zanoapp.applediseaseIdentification.localDataPersistence.userDB.UserDatabase
import com.zanoapp.applediseaseIdentification.utils.Converters
import kotlin.coroutines.coroutineContext

@Database(entities = arrayOf(Transaction::class), version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class TransactionDatabase: RoomDatabase() {
    abstract fun transactionDao(): TransactionDAO

    companion object {
        @Volatile
        private var INSTANCE: TransactionDatabase? = null

        fun getInstance(context: Context): TransactionDatabase {

            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        TransactionDatabase::class.java,
                        "transactionDB.db"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}