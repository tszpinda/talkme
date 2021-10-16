package tszpinda.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

private val savedMessages = mutableListOf(
    MessageRaw("Hello tomek! How are things\nAndroid projects suck", MessageType.IN),
    MessageRaw("they are all a bit boring", MessageType.IN),
    MessageRaw("Hey Johny, you quite right", MessageType.OUT),
    MessageRaw("have you tried iOS\n that's even worse!", MessageType.OUT),
    MessageRaw("Anyway lets go for a beer!", MessageType.IN)
)

fun mapMessages(raw: List<MessageRaw>): List<Message> {
    return raw.mapIndexed { i, raw ->
        Message(
            raw.text,
            raw.type,
            raw.time,
            tail(i, savedMessages)
        )
    }
}

class ChatViewModel: ViewModel() {

    private val _messages = MutableLiveData(mapMessages(savedMessages))
    val messages: LiveData<List<Message>>
        get() = _messages

    fun addMessage(msg: String) {
        savedMessages.add(MessageRaw(msg, MessageType.OUT))
        this._messages.value = mapMessages(savedMessages)
    }


}