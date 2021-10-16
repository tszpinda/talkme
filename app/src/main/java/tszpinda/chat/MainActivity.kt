package tszpinda.chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import tszpinda.chat.databinding.ActivityMainBinding
import tszpinda.chat.databinding.ChatBubbleInBinding
import tszpinda.chat.databinding.ChatBubbleOutBinding

// TODO
// - dark mode
// - nav bar
// 
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: ChatViewModel by viewModels {
        ChatViewModelFactory((application as App).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val results: RecyclerView = binding.messages
        val layoutManager = LinearLayoutManager(this)
        layoutManager.stackFromEnd = true
        results.layoutManager = layoutManager

        val adapter = MessageDataAdapter()
        results.adapter = adapter

        viewModel.allMessages.observe(this) {
            val mapped = mapMessages(it)
            adapter.submitList(mapped)
            layoutManager.scrollToPositionWithOffset(mapped.size - 1, 0)
        }
        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                layoutManager.scrollToPositionWithOffset(positionStart, 0)
            }
        })


        binding.send.setOnClickListener {
            with(binding.messageInput) {
                viewModel.add(text.toString())
                text.clear()
            }
        }

    }
}


sealed class MessageViewHolder(binding: ViewBinding) : RecyclerView.ViewHolder(binding.root) {

    class MessageInHolder(private val binding: ChatBubbleInBinding) : MessageViewHolder(binding) {
        fun bind(msg: Message) {
            binding.chatMessage.text = msg.text
            val bg = if (msg.tail) R.drawable.chat_bubble_in_tail else R.drawable.chat_bubble_in
            binding.chatMessage.setBackgroundResource(bg)
        }
    }

    class MessageOutHolder(private val binding: ChatBubbleOutBinding) : MessageViewHolder(binding) {
        fun bind(msg: Message) {
            binding.chatMessage.text = msg.text
            val bg = if (msg.tail) R.drawable.chat_bubble_out_tail else R.drawable.chat_bubble_out
            binding.chatMessage.setBackgroundResource(bg)
        }
    }
}

object MessageDiff: DiffUtil.ItemCallback<Message>() {
    override fun areItemsTheSame(oldItem: Message, newItem: Message) = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: Message, newItem: Message) = oldItem == newItem
}

class MessageDataAdapter : ListAdapter<Message, MessageViewHolder>(MessageDiff) {

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
        val msg = getItem(position)
        when (viewHolder) {
            is MessageViewHolder.MessageInHolder -> viewHolder.bind(msg)
            is MessageViewHolder.MessageOutHolder -> viewHolder.bind(msg)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position).type.ordinal
    }

}