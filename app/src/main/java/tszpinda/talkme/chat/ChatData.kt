package tszpinda.talkme.chat

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import java.time.Instant.ofEpochMilli
import java.time.LocalDateTime
import java.time.ZoneId.systemDefault
import java.time.format.DateTimeFormatter

data class Message (val id: Int, val text: String, val type: MessageType, val time: Long = System.currentTimeMillis(), val tail: Boolean = false)

private val MESSAGE_DISPLAY_DATE = DateTimeFormatter.ofPattern("EEEE HH:mm")
fun Message.displayDate(): String = LocalDateTime.ofInstant(ofEpochMilli(this.time), systemDefault()).format(MESSAGE_DISPLAY_DATE)

enum class MessageType {
    IN, OUT, DATE
}

@Entity(tableName = "messages")
class MessageRaw(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val text: String,
    val type: MessageType,
    @ColumnInfo(name = "message_time")
    val time: Long = System.currentTimeMillis())


@Dao
interface MessageDao {

    @Query("select * from messages order by message_time asc")
    fun getAll(): Flow<List<MessageRaw>>

    @Insert
    suspend fun insert(msg: MessageRaw)
}


class MessageRepository(private val messageDao: MessageDao) {
    val allMessages: Flow<List<Message>> = messageDao.getAll().mapLatest { MessageService.mapMessages(it) }
    suspend fun insert(text: String, type: MessageType) {
        messageDao.insert(MessageRaw(0, text, type))
    }
}


