package tszpinda.chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
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

    private val messages = mutableListOf(
        Message("Hello tomek! How are things\nAndroid projects suck", MessageType.IN),
        Message("they are all a bit boring", MessageType.IN),
        Message("Hey Johny, you quite right", MessageType.OUT),
        Message("have you tried iOS\n that's even worse!", MessageType.OUT),
        Message("Anyway lets go for a beer!", MessageType.IN)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val results: RecyclerView = binding.messages
        val layoutManager = LinearLayoutManager(this)
        layoutManager.stackFromEnd = true
        results.layoutManager = layoutManager

        val adapter = MessageDataAdapter(Messages(messages))
        results.adapter = adapter

        binding.send.setOnClickListener { _ ->
            with(binding.messageInput) {
                // TODO add to db
                adapter.addMessage(Message(text.toString(), MessageType.OUT))
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

class MessageDataAdapter(private val messages: Messages) : RecyclerView.Adapter<MessageViewHolder>() {

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
        val lastMessage = position - 1 == messages.data.size
        val lastMessageDiffType = position > 0 && msg.type != messages.data[position - 1].type
        val nextMessageAfter20sec = position < messages.data.size - 1 && messages.data[position + 1].time - msg.time > 20 * 1000
        val tail = lastMessage || lastMessageDiffType || nextMessageAfter20sec

        val nt = msg.text + "\ntail?: $tail"
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

    fun addMessage(msg: Message) {
        messages.data.add(msg)
        notifyItemChanged(itemCount - 1)
    }
}