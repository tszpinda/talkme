package tszpinda.talkme.chat

object MessageService {
    fun isMostRecent(msgIndex: Int, messages: List<MessageRaw>) = msgIndex == messages.size - 1
    fun isSentByAnotherUser(msgIndex: Int, messages: List<MessageRaw>) =
        msgIndex > 0 && messages[msgIndex].type != messages[msgIndex - 1].type

    fun isNextMessageAfter20Sec(msgIndex: Int, messages: List<MessageRaw>) =
        msgIndex != messages.size - 1 && messages[msgIndex + 1].time - messages[msgIndex].time > 20 * 1000

    fun tail(msgIndex: Int, messages: List<MessageRaw>): Boolean {
        return isMostRecent(msgIndex, messages) || isSentByAnotherUser(msgIndex,
            messages) || isNextMessageAfter20Sec(msgIndex, messages)
    }

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
            val msg = iterator.next()
            val withinHourFromLastMessage = msg.time - lastTime <= 1000 * 60 * 60
            if (withinHourFromLastMessage)
                continue
            iterator.previous()
            iterator.add(msg.copy(id = -1, type = MessageType.DATE))
            iterator.next()
            lastTime = msg.time
        }

        return withTimeSeparator
    }

}
