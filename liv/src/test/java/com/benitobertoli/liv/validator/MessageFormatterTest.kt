package com.benitobertoli.liv.validator

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class MessageFormatterTest {
    private val messages = listOf("A", "B", "C")

    @Test
    fun `when NONE return empty`() {
        assertThat(MessageFormatter.format(messages, MessageType.NONE)).isEqualTo("")
    }

    @Test
    fun `when SINGLE return the first item`() {
        assertThat(MessageFormatter.format(messages, MessageType.SINGLE)).isEqualTo("A")
    }

    @Test
    fun `when MULTIPLE return all items`() {
        val expected = "A\nB\nC"
        assertThat(MessageFormatter.format(messages, MessageType.MULTIPLE)).isEqualTo(expected)
    }
}