package com.kvsinyuk.stickergenerator.adapter.`in`.telegram.handlers

import com.kvsinyuk.stickergenerator.applicaiton.port.`in`.CreateStickerUseCase
import com.kvsinyuk.stickergenerator.applicaiton.port.out.telegram.TelegramMessagePort
import com.kvsinyuk.stickergenerator.applicaiton.port.out.mongo.DeleteStickerDataUseCase
import com.kvsinyuk.stickergenerator.applicaiton.port.out.mongo.FindStickerDataUseCase
import com.kvsinyuk.stickergenerator.domain.Status
import com.kvsinyuk.stickergenerator.domain.TelegramUpdateMessage
import org.springframework.stereotype.Component

@Component
class BottomTextHandler(
    private val telegramMessagePort: TelegramMessagePort,
    private val findStickerDataUseCase: FindStickerDataUseCase,
    private val createStickerUseCase: CreateStickerUseCase,
    private val deleteStickerDataUseCase: DeleteStickerDataUseCase
) : TelegramUpdateHandler {
    override fun process(update: TelegramUpdateMessage) {
        val stickerData = findStickerDataUseCase.findByChatId(update.chatId)!!
            .apply { bottomText = update.message.takeIf { it != "*" } ?: "" }

        val stickerFile = stickerData
            .let { createStickerUseCase.createSticker(it) }
            .also { deleteStickerDataUseCase.delete(update.chatId) }

        telegramMessagePort.sendDocument(update.chatId, stickerFile, stickerData.originalFilename)
    }

    override fun canApply(update: TelegramUpdateMessage) =
        !update.message.isNullOrBlank()
                && findStickerDataUseCase.findByChatId(update.chatId)?.status == Status.TOP_TEXT_ADDED
}