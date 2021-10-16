package tszpinda.chat
enum class MessageType {
    IN, OUT
}
class Message (val text: String, val type: MessageType, val time: Long = System.currentTimeMillis())

class Messages (val data: MutableList<Message>)