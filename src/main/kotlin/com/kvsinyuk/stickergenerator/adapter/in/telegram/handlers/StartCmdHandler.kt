package com.kvsinyuk.stickergenerator.adapter.`in`.telegram.handlers

import com.kvsinyuk.stickergenerator.applicaiton.port.MessageSourcePort
import com.kvsinyuk.stickergenerator.applicaiton.port.out.mongo.DeleteStickerDataPort
import com.kvsinyuk.stickergenerator.applicaiton.port.out.telegram.TelegramMessagePort
import com.kvsinyuk.stickergenerator.domain.TelegramUpdateMessage
import com.kvsinyuk.stickergenerator.utils.Commands.START_CMD
import org.springframework.stereotype.Component

@Component
class StartCmdHandler(
    private val telegramMessagePort: TelegramMessagePort,
    private val deleteStickerDataPort: DeleteStickerDataPort,
    private val messagePort: MessageSourcePort
) : TelegramUpdateHandler {
    override fun process(update: TelegramUpdateMessage) {
        deleteStickerDataPort.delete(update.chatId)
        val responseMsg = messagePort.getMessage("command.start.response")
        telegramMessagePort.sendMessage(update.chatId, responseMsg)
    }

    override fun canApply(update: TelegramUpdateMessage) =
        update.message == START_CMD
}
