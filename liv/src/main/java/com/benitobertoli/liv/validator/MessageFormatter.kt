package com.benitobertoli.liv.validator

object MessageFormatter {
    fun format(messages: List<CharSequence>, messageType: MessageType): CharSequence =
            when (messageType) {
                MessageType.NONE -> ""
                MessageType.SINGLE -> messages.firstOrNull() ?: ""
                MessageType.MULTIPLE -> messages.joinToString("\n")
            }
}