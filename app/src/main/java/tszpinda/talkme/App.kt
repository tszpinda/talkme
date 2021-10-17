package tszpinda.talkme

import android.app.Application
import tszpinda.talkme.chat.MessageRepository

class App : Application() {
    private val database by lazy { Db.getDatabase(this) }
    val repository by lazy { MessageRepository(database.messageDao()) }
}