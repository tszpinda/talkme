package tszpinda.chat

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class MessageTailTest {

    private fun msg(type: MessageType = MessageType.IN, time: Long = System.currentTimeMillis()) =
        MessageRaw("hi", type, time)

    @Test
    fun most_recent_in_conversation() {
        assertTrue(isMostRecent(0, listOf(msg())))
        assertTrue(isMostRecent(1, listOf(msg(), msg())))
        assertFalse(isMostRecent(0, listOf(msg(), msg())))
    }

    @Test
    fun sent_by_another_user() {
        val m1 = msg(MessageType.IN)
        val m2 = msg(MessageType.OUT)
        assertFalse(isSentByAnotherUser(0, listOf(m1)))
        assertFalse(isSentByAnotherUser(0, listOf(m1, m1)))
        assertFalse(isSentByAnotherUser(1, listOf(m1, m1)))
        assertFalse(isSentByAnotherUser(0, listOf(m1, m2)))
        assertTrue(isSentByAnotherUser(1, listOf(m1, m2)))
    }

    // A message has a tail when any of the following 3 criteria are met:
    // - It is the most recent message in the conversation
    // - The message after it is sent by the other user
    // - The message after it was sent more than 20 seconds afterwards

    // Item sectioning
    // - If a previous message was sent more than an hour ago,
    // - there is no previous messages
    // sectioned by "{day} {timestamp}".
}