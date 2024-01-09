package com.kvsinyuk.stickergenerator.adapter.`in`.telegram.handlers.sticker

import com.kvsinyuk.stickergenerator.adapter.`in`.telegram.handlers.TelegramUpdateHandler
import com.kvsinyuk.stickergenerator.applicaiton.port.`in`.CreateStickerUseCase
import com.kvsinyuk.stickergenerator.applicaiton.port.out.telegram.TelegramMessagePort
import com.kvsinyuk.stickergenerator.applicaiton.port.out.mongo.DeleteBotDataPort
import com.kvsinyuk.stickergenerator.applicaiton.port.out.mongo.FindBotDataPort
import com.kvsinyuk.stickergenerator.applicaiton.port.out.mongo.GetBotDataPort
import com.kvsinyuk.stickergenerator.applicaiton.utils.mapToByteArray
import com.kvsinyuk.stickergenerator.domain.TelegramUpdateMessage
import com.kvsinyuk.stickergenerator.domain.sticker.CreateStickerData
import com.kvsinyuk.stickergenerator.domain.sticker.StickerStatus
import org.springframework.stereotype.Component

@Component
class BottomTextHandler(
    private val telegramMessagePort: TelegramMessagePort,
    private val getBotDataPort: GetBotDataPort,
    private val findBotDataPort: FindBotDataPort,
    private val createStickerUseCase: CreateStickerUseCase,
    private val deleteBotDataPort: DeleteBotDataPort
) : TelegramUpdateHandler {
    override fun process(update: TelegramUpdateMessage) {
        val stickerData = getBotDataPort.getByChatId(update.chatId)
        stickerData.getAsCreateStickerData()
            .apply { bottomText = update.message.takeIf { it != "*" } ?: "" }

        val stickerFile = stickerData
            .let { createStickerUseCase.createSticker(it) }
            .mapToByteArray()

        telegramMessagePort.sendDocument(update.chatId, stickerFile, (stickerData.commandData as CreateStickerData).originalFilename)
            .also { deleteBotDataPort.delete(update.chatId) }
    }

    override fun canApply(update: TelegramUpdateMessage): Boolean {
        if (!update.message.isNullOrBlank()) {
            val botData = findBotDataPort.findByChatId(update.chatId)
            return botData?.commandData?.isStickerData() == true
                    && botData.getAsCreateStickerData().status == StickerStatus.TOP_TEXT_ADDED
        }
        return false
    }
}
