package com.vocablink.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.vocablink.data.dao.WordDao
import com.vocablink.data.entity.Word

@Database(entities = [Word::class], version = 1, exportSchema = false)
abstract class VocabDatabase : RoomDatabase() {
    abstract fun wordDao(): WordDao

    companion object {
        @Volatile
        private var INSTANCE: VocabDatabase? = null

        fun getDatabase(context: Context): VocabDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    VocabDatabase::class.java,
                    "vocab_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
