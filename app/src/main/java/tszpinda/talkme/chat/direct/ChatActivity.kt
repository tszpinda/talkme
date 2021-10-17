package tszpinda.talkme.chat.direct

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
import tszpinda.talkme.databinding.ChatBubbleInBinding
import tszpinda.talkme.databinding.ChatBubbleOutBinding
import tszpinda.talkme.databinding.ChatDateDividerBinding
import tszpinda.talkme.App
import tszpinda.talkme.R
import tszpinda.talkme.chat.Message
import tszpinda.talkme.chat.MessageType
import tszpinda.talkme.databinding.ActivityChatBinding

// TODO
// - dark mode
// - nav bar
class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private val viewModel: ChatViewModel by viewModels {
        ChatViewModelFactory((application as App).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val results: RecyclerView = binding.messages
        val layoutManager = LinearLayoutManager(this)
        val adapter = MessageDataAdapter()

        layoutManager.stackFromEnd = true

        results.layoutManager = layoutManager
        results.adapter = adapter

        viewModel.allMessages.observe(this) {
            adapter.submitList(it)
            layoutManager.scrollToPositionWithOffset(it.size - 1, 0)
        }
        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                layoutManager.scrollToPositionWithOffset(positionStart, 0)
            }
        })

        binding.send.setOnClickListener {
            with(binding.messageInput) {
                if (text.toString() != "") {
                    viewModel.add(text.toString())
                    text.clear()
                }
            }
        }

    }
}

// Separate MessageHolders for In and Out message as they are likely
// diff in functionality. Out could have delivery/read status added.
// In could have a option to be delete a message.

abstract class MessageViewHolder(binding: ViewBinding) : RecyclerView.ViewHolder(binding.root) {

    abstract fun bind(msg: Message)

    class InMessageHolder(private val binding: ChatBubbleInBinding) : MessageViewHolder(binding) {
        override fun bind(msg: Message) {
            binding.chatMessage.text = msg.text
            val bg = if (msg.tail) R.drawable.chat_bubble_in_tail else R.drawable.chat_bubble_in
            binding.chatMessage.setBackgroundResource(bg)
        }
    }

    class OutMessageHolder(private val binding: ChatBubbleOutBinding) : MessageViewHolder(binding) {
        override fun bind(msg: Message) {
            binding.chatMessage.text = msg.text
            val bg = if (msg.tail) R.drawable.chat_bubble_out_tail else R.drawable.chat_bubble_out
            binding.chatMessage.setBackgroundResource(bg)
        }
    }

    class DateMessageHolder(private val binding: ChatDateDividerBinding) : MessageViewHolder(binding) {
        override fun bind(msg: Message) {
            binding.date.text = msg.text
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
            MessageType.IN.ordinal -> MessageViewHolder.InMessageHolder(
                ChatBubbleInBinding.inflate(LayoutInflater.from(vg.context),
                vg,
                false)
            )
            MessageType.OUT.ordinal -> MessageViewHolder.OutMessageHolder(
                ChatBubbleOutBinding.inflate(LayoutInflater.from(vg.context),
                    vg,
                    false)
            )
            else -> MessageViewHolder.DateMessageHolder(
                ChatDateDividerBinding.inflate(LayoutInflater.from(vg.context),
                    vg,
                    false)
            )
        }
    }

    override fun onBindViewHolder(viewHolder: MessageViewHolder, position: Int) {
        viewHolder.bind(getItem(position))
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position).type.ordinal
    }

}