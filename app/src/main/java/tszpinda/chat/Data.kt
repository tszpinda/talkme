package tszpinda.chat

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import java.time.Instant.ofEpochMilli
import java.time.LocalDateTime
import java.time.ZoneId.systemDefault
import java.time.format.DateTimeFormatter

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
    val allMessages: Flow<List<MessageRaw>> = messageDao.getAll()
    suspend fun insert(message: MessageRaw) {
        messageDao.insert(message)
    }
}


data class Message (val id: Int, val text: String, val type: MessageType, val time: Long = System.currentTimeMillis(), val tail: Boolean = false)

private val MESSAGE_DISPLAY_DATE = DateTimeFormatter.ofPattern("EEEE HH:mm")
fun Message.displayDate(): String = LocalDateTime.ofInstant(ofEpochMilli(this.time), systemDefault()).format(MESSAGE_DISPLAY_DATE)

fun isMostRecent(msgIndex: Int, messages: List<MessageRaw>) = msgIndex == messages.size - 1
fun isSentByAnotherUser(msgIndex: Int, messages: List<MessageRaw>) = msgIndex > 0 && messages[msgIndex].type != messages[msgIndex - 1].type
fun isNextMessageAfter20Sec(msgIndex: Int, messages: List<MessageRaw>) = msgIndex != messages.size -1 && messages[msgIndex + 1].time - messages[msgIndex].time > 20 * 1000

fun tail(msgIndex: Int, messages: List<MessageRaw>): Boolean {
    return isMostRecent(msgIndex, messages) || isSentByAnotherUser(msgIndex, messages) || isNextMessageAfter20Sec(msgIndex, messages)
}
