package com.kvsinyuk.stickergenerator.adapter.`in`.telegram.handlers.sticker

import com.kvsinyuk.stickergenerator.adapter.`in`.telegram.handlers.TelegramUpdateHandler
import com.kvsinyuk.stickergenerator.applicaiton.port.MessageSourcePort
import com.kvsinyuk.stickergenerator.applicaiton.port.`in`.SaveStickerDataUseCase
import com.kvsinyuk.stickergenerator.applicaiton.port.out.mongo.FindBotDataPort
import com.kvsinyuk.stickergenerator.applicaiton.port.out.mongo.GetBotDataPort
import com.kvsinyuk.stickergenerator.applicaiton.port.out.telegram.TelegramFilePort
import com.kvsinyuk.stickergenerator.applicaiton.port.out.telegram.TelegramMessagePort
import com.kvsinyuk.stickergenerator.applicaiton.utils.getBufferedImage
import com.kvsinyuk.stickergenerator.domain.TelegramUpdateMessage
import com.kvsinyuk.stickergenerator.domain.sticker.StickerStatus
import org.springframework.stereotype.Component

@Component
class ImageHandler(
    private val telegramFilePort: TelegramFilePort,
    private val telegramMessagePort: TelegramMessagePort,
    private val getBotDataPort: GetBotDataPort,
    private val findBotDataPort: FindBotDataPort,
    private val saveStickerDataUseCase: SaveStickerDataUseCase,
    private val messagePort: MessageSourcePort
) : TelegramUpdateHandler {

    override fun process(update: TelegramUpdateMessage) {
        val botData = getBotDataPort.getByChatId(update.chatId)
        val file = telegramFilePort.getFileContent(update.document!!.fileId())
        botData.getAsCreateStickerData()
            .apply {
                image = file
                status = StickerStatus.SOURCE_FILE_ADDED
                originalFilename = update.document.fileName()
            }
        saveStickerDataUseCase.save(botData)
        telegramMessagePort.sendMessage(
            update.chatId,
            messagePort.getMessage("command.mk-sticker.image-added.response")
        )
    }

    override fun canApply(update: TelegramUpdateMessage) =
        update.document != null
                && findBotDataPort.findByChatId(update.chatId)?.commandData?.isStickerData() == true
}
