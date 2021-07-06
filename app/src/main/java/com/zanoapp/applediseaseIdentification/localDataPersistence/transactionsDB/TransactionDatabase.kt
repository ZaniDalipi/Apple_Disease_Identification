package com.zanoapp.applediseaseIdentification.localDataPersistence.transactionsDB

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.zanoapp.applediseaseIdentification.localDataPersistence.userDB.UserDatabase

@Database(entities = [Transaction::class], version = 1)
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
                        context,
                        TransactionDatabase::class.java,
                        "transaction.db"
                    )
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}