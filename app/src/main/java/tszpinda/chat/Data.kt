package tszpinda.chat
enum class MessageType {
    IN, OUT
}
class MessageRaw (val text: String, val type: MessageType, val time: Long = System.currentTimeMillis())
class Message (val text: String, val type: MessageType, val time: Long = System.currentTimeMillis(), val tail: Boolean = false)

class Messages (val data: MutableList<Message>)

fun isMostRecent(msgIndex: Int, messages: List<MessageRaw>) = msgIndex == messages.size - 1
fun isSentByAnotherUser(msgIndex: Int, messages: List<MessageRaw>) = msgIndex > 0 && messages[msgIndex].type != messages[msgIndex - 1].type
fun isNextMessageAfter20Sec(msgIndex: Int, messages: List<MessageRaw>) = msgIndex != messages.size -1 && messages[msgIndex + 1].time - messages[msgIndex].time > 20 * 1000

fun tail(msgIndex: Int, messages: List<MessageRaw>): Boolean {
    return isMostRecent(msgIndex, messages) || isSentByAnotherUser(msgIndex, messages) || isNextMessageAfter20Sec(msgIndex, messages)
}
