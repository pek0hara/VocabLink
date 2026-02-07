package com.vocablink

import android.app.Application
import com.vocablink.data.database.VocabDatabase
import com.vocablink.data.repository.WordRepository

class VocabLinkApplication : Application() {
    val database by lazy { VocabDatabase.getDatabase(this) }
    val repository by lazy { WordRepository(database.wordDao()) }
}
