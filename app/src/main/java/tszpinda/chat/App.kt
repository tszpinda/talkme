package tszpinda.chat

import android.app.Application

class App : Application() {
    private val database by lazy { Db.getDatabase(this) }
    val repository by lazy { MessageRepository(database.messageDao()) }
}