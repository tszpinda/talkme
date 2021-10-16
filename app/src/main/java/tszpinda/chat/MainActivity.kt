package tszpinda.chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import tszpinda.chat.databinding.ActivityMainBinding
import tszpinda.chat.databinding.ChatBubbleInBinding
import tszpinda.chat.databinding.ChatBubbleOutBinding

// TODO
// - dark mode
// - nav bar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val results: RecyclerView = binding.messages
        val layoutManager = LinearLayoutManager(this)
        layoutManager.stackFromEnd = true
        results.layoutManager = layoutManager

        val viewModel = ViewModelProvider(this).get(ChatViewModel::class.java)
        val adapter = MessageDataAdapter()
        results.adapter = adapter
        viewModel.messages.observe(this) {
            adapter.setMessages(Messages(it.toMutableList()))
        }


        binding.send.setOnClickListener { _ ->
            with(binding.messageInput) {
                viewModel.addMessage(text.toString())
                text.clear()
            }
            binding.messages.smoothScrollToPosition(adapter.itemCount)
        }

    }
}

sealed class MessageViewHolder(binding: ViewBinding) : RecyclerView.ViewHolder(binding.root) {

    class MessageInHolder(private val binding: ChatBubbleInBinding) : MessageViewHolder(binding) {
        fun bind(msg: Message) {
            binding.chatMessage.text = msg.text
        }
    }

    class MessageOutHolder(private val binding: ChatBubbleOutBinding) : MessageViewHolder(binding) {
        fun bind(msg: Message) {
            binding.chatMessage.text = msg.text
        }
    }
}

class MessageDataAdapter : RecyclerView.Adapter<MessageViewHolder>() {

    private var messages: Messages = Messages(mutableListOf())

    fun setMessages(messages: Messages) {
        this.messages = Messages(messages.data.toMutableList())
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(vg: ViewGroup, viewType: Int): MessageViewHolder {
        return when (viewType) {
            MessageType.IN.ordinal -> MessageViewHolder.MessageInHolder(
                ChatBubbleInBinding.inflate(LayoutInflater.from(vg.context),
                vg,
                false)
            )
            else -> MessageViewHolder.MessageOutHolder(
                ChatBubbleOutBinding.inflate(LayoutInflater.from(vg.context),
                    vg,
                    false)
            )
        }
    }

    override fun onBindViewHolder(viewHolder: MessageViewHolder, position: Int) {
        val msg = messages.data[position]
        val nt = msg.text + "\ntail?: ${msg.tail}"
        val m = Message(nt, msg.type, msg.time)

        when (viewHolder) {
            is MessageViewHolder.MessageInHolder -> viewHolder.bind(m);
            is MessageViewHolder.MessageOutHolder -> viewHolder.bind(m);
        }
    }

    override fun getItemCount() = messages.data.size

    override fun getItemViewType(position: Int): Int {
        return messages.data[position].type.ordinal
    }

}