package tszpinda.chat
enum class MessageType {
    IN, OUT
}
class MessageRaw (val text: String, val type: MessageType, val time: Long = System.currentTimeMillis())
class Message (val text: String, val type: MessageType, val time: Long = System.currentTimeMillis(), val tail: Boolean = false)

class Messages (val data: MutableList<Message>)

private val messages = mutableListOf(
    MessageRaw("Hello tomek! How are things\nAndroid projects suck", MessageType.IN),
    MessageRaw("they are all a bit boring", MessageType.IN),
    MessageRaw("Hey Johny, you quite right", MessageType.OUT),
    MessageRaw("have you tried iOS\n that's even worse!", MessageType.OUT),
    MessageRaw("Anyway lets go for a beer!", MessageType.IN)
)

fun isMostRecent(msgIndex: Int, messages: List<MessageRaw>) = msgIndex == messages.size - 1
fun isSentByAnotherUser(msgIndex: Int, messages: List<MessageRaw>) = msgIndex > 0 && messages[msgIndex].type != messages[msgIndex - 1].type

fun tail(msgIndex: Int, raw: MessageRaw, messages: List<MessageRaw>): Boolean {
    return isMostRecent(msgIndex, messages) && isSentByAnotherUser(msgIndex, messages)
}

fun loadData() {
    messages.mapIndexed { i, messageRaw ->
        Message(
            messageRaw.text,
            messageRaw.type,
            messageRaw.time,
            tail(i, messageRaw, messages)
        )
    }
}