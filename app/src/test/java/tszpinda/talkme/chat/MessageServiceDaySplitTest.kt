package tszpinda.talkme.chat

import org.junit.Test
import com.google.common.truth.Truth.assertThat
import com.google.common.truth.Correspondence
import tszpinda.talkme.chat.MessageService.mapMessages
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

val messageTypeTransform: Correspondence<Message, MessageType> =
    Correspondence.transforming(
        { input -> input?.type },
        "type"
    )

class MessageServiceDaySplitTest {

    private fun msg(dateTime: String) =
        MessageRaw(1,
            "hi",
            MessageType.IN,
            parseDateTime(dateTime))

    private fun parseDateTime(t: String): Long {
        var format =  DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        return LocalDateTime.parse(t, format).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }

    @Test
    fun adds_date_type_no_previous_message() {
        val results = mapMessages(listOf(
            msg("2021-10-17 10:00"),
        ))
        assertThat(results)
            .comparingElementsUsing(messageTypeTransform)
            .containsExactlyElementsIn(listOf(MessageType.DATE, MessageType.IN))
            .inOrder()
    }

    @Test
    fun adds_date_type_day_later() {
        val results = mapMessages(listOf(
            msg("2021-10-17 10:00"),
            msg("2021-10-18 10:00"),
        ))
        assertThat(results)
            .comparingElementsUsing(messageTypeTransform)
            .containsExactlyElementsIn(listOf(MessageType.DATE, MessageType.IN, MessageType.DATE, MessageType.IN))
            .inOrder()
    }

    @Test
    fun adds_date_type_hour_later() {
        val results = mapMessages(listOf(
            msg("2021-10-17 10:00"),
            msg("2021-10-17 11:01"),
        ))
        assertThat(results)
            .comparingElementsUsing(messageTypeTransform)
            .containsExactlyElementsIn(listOf(MessageType.DATE, MessageType.IN, MessageType.DATE, MessageType.IN))
            .inOrder()
    }

    @Test
    fun no_date_type_exactly_after_hour() {
        val results = mapMessages(listOf(
            msg("2021-10-17 10:00"),
            msg("2021-10-17 11:00"),
        ))
        assertThat(results)
            .comparingElementsUsing(messageTypeTransform)
            .containsExactlyElementsIn(listOf(MessageType.DATE, MessageType.IN, MessageType.IN))
            .inOrder()
    }

    @Test
    fun no_date_type_exactly_1_min() {
        val results = mapMessages(listOf(
            msg("2021-10-17 10:00"),
            msg("2021-10-17 10:01"),
        ))
        assertThat(results)
            .comparingElementsUsing(messageTypeTransform)
            .containsExactlyElementsIn(listOf(MessageType.DATE, MessageType.IN, MessageType.IN))
            .inOrder()
    }
}
