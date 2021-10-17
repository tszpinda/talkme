package tszpinda.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlin.random.Random

fun mapMessages(list: List<MessageRaw>): List<Message> {
    val messages = list.mapIndexed { i: Int, raw: MessageRaw ->
        Message(
            raw.id,
            raw.text,
            raw.type,
            raw.time,
            tail(i, list)
        )
    }


    val withTimeSeparator = messages.toMutableList()
    var lastTime = 0L
    val iterator = withTimeSeparator.listIterator()
    while (iterator.hasNext()) {
        val m = iterator.next()
        if (m.time - lastTime <= 1000 * 60 * 60)
            continue
        val date = m.displayDate()
        iterator.previous()
        iterator.add(m.copy(text = date, type = MessageType.DATE))
        iterator.next()
        lastTime = m.time
    }

    return withTimeSeparator
}

class ChatViewModel(private val repo: MessageRepository): ViewModel() {

    val allMessages: LiveData<List<MessageRaw>> = repo.allMessages.asLiveData();

    fun add(msg: String) = viewModelScope.launch {
        repo.insert(MessageRaw(id=0, msg, if (Random.nextBoolean()) MessageType.OUT else MessageType.IN))
    }

}

class ChatViewModelFactory(private val repository: MessageRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChatViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}