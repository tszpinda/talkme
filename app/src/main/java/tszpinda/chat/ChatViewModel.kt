package tszpinda.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlin.random.Random

class ChatViewModel(private val repo: MessageRepository): ViewModel() {

    val allMessages: LiveData<List<Message>> = repo.allMessages.asLiveData()

    fun add(msg: String) = viewModelScope.launch {
        repo.insert(msg, if (Random.nextBoolean()) MessageType.OUT else MessageType.IN)
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