package tszpinda.chat

import org.junit.Test

import org.junit.Assert.*

class MessageTailTest {

    private fun msg(type: MessageType = MessageType.IN, time: Long = System.currentTimeMillis()) =
        MessageRaw(1, "hi", type, time)

    @Test
    fun most_recent_in_conversation() {
        assertTrue(isMostRecent(0, listOf(msg())))
        assertTrue(isMostRecent(1, listOf(msg(), msg())))
        assertFalse(isMostRecent(0, listOf(msg(), msg())))
    }

    @Test
    fun sent_by_another_user() {
        val mIn = msg(MessageType.IN)
        val mOut = msg(MessageType.OUT)
        assertFalse(isSentByAnotherUser(0, listOf(mIn)))
        assertFalse(isSentByAnotherUser(0, listOf(mIn, mIn)))
        assertFalse(isSentByAnotherUser(1, listOf(mIn, mIn)))
        assertFalse(isSentByAnotherUser(0, listOf(mIn, mOut)))
        assertTrue(isSentByAnotherUser(1, listOf(mIn, mOut)))
    }

    @Test
    fun next_message_sent_after_20sec() {
        val msgAfter20Sec = msg(time = System.currentTimeMillis() + 21 * 1000)
        assertFalse("single msg", isNextMessageAfter20Sec(0, listOf(msg())))
        assertFalse("sent to close", isNextMessageAfter20Sec(0, listOf(msg(), msg())))
        assertFalse("last message", isNextMessageAfter20Sec(1, listOf(msg(), msg())))
        assertFalse("last message", isNextMessageAfter20Sec(1, listOf(msg(), msgAfter20Sec)))
        assertFalse("next sent 20sec ago", isNextMessageAfter20Sec(0, listOf(msg(), msg(time = System.currentTimeMillis() + 20 * 1000))))
        assertTrue("next sent after 20sec", isNextMessageAfter20Sec(0, listOf(msg(), msgAfter20Sec)))
    }

    @Test
    fun tail() {
        assertTrue(
            tail(1, listOf(msg(MessageType.IN), msg(MessageType.OUT)))
        )
    }

    // Item sectioning
    // - If a previous message was sent more than an hour ago,
    // - there is no previous messages
    // sectioned by "{day} {timestamp}".
}