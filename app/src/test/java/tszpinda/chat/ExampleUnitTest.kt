package tszpinda.chat

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class MessageTailTest {
    @Test
    fun most_recent_in_conversation() {
        assertEquals(4, 2 + 2)
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