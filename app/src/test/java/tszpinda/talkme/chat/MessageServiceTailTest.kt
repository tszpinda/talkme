package tszpinda.talkme.chat

import org.junit.Test

import com.google.common.truth.Truth.assertThat
import com.google.common.truth.Truth.assertWithMessage
import tszpinda.talkme.chat.MessageService.isMostRecent
import tszpinda.talkme.chat.MessageService.isNextMessageAfter20Sec
import tszpinda.talkme.chat.MessageService.isSentByAnotherUser
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class MessageServiceTailTest {

    private fun msg(type: MessageType = MessageType.IN, dateTime: String = "2021-10-17 10:00:00") =
        MessageRaw(1, "hi", type, parseDateTime(dateTime))

    private fun parseDateTime(dt: String): Long {
        val format =  DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        return LocalDateTime.parse(dt, format).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }

    @Test
    fun most_recent_in_conversation() {
        assertThat(isMostRecent(0, listOf(msg()))).isTrue()
        assertThat(isMostRecent(1, listOf(msg(), msg()))).isTrue()
        assertThat(isMostRecent(0, listOf(msg(), msg()))).isFalse()
    }

    @Test
    fun sent_by_another_user() {
        val mUser1 = msg(MessageType.IN)
        val mUser2 = msg(MessageType.OUT)
        assertThat(isSentByAnotherUser(0, listOf(mUser1))).isFalse()
        assertThat(isSentByAnotherUser(0, listOf(mUser1, mUser1))).isFalse()
        assertThat(isSentByAnotherUser(1, listOf(mUser1, mUser1))).isFalse()
        assertThat(isSentByAnotherUser(0, listOf(mUser1, mUser2))).isFalse()
        assertThat(isSentByAnotherUser(1, listOf(mUser1, mUser2))).isTrue()
    }

    @Test
    fun next_message_sent_after_20sec() {
        val msgAt10 = msg(dateTime = "2021-10-17 10:00:00")
        val msgAfter20Sec = msg(dateTime = "2021-10-17 10:00:20")
        val msgAfter21Sec = msg(dateTime = "2021-10-17 10:00:21")
        assertWithMessage("single msg").that(isNextMessageAfter20Sec(0, listOf(msgAt10))).isFalse()
        assertWithMessage("sent to close").that(isNextMessageAfter20Sec(0, listOf(msgAt10, msgAt10))).isFalse()
        assertWithMessage("last message").that(isNextMessageAfter20Sec(1, listOf(msgAt10, msgAt10))).isFalse()
        assertWithMessage("last message").that(isNextMessageAfter20Sec(1, listOf(msgAt10, msgAfter21Sec))).isFalse()
        assertWithMessage("next sent 20sec ago").that(isNextMessageAfter20Sec(0, listOf(msgAt10, msgAfter20Sec))).isFalse()
        assertWithMessage("next sent after 20sec").that(isNextMessageAfter20Sec(0, listOf(msg(), msgAfter21Sec))).isTrue()
    }

    @Test
    fun tail() {
        assertThat(
            MessageService.tail(1, listOf(msg(MessageType.IN), msg(MessageType.OUT)))
        ).isTrue()
    }

}