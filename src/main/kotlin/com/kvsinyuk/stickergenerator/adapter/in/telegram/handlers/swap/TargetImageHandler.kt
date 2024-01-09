package com.kvsinyuk.stickergenerator.adapter.`in`.telegram.handlers.swap

import com.kvsinyuk.stickergenerator.adapter.`in`.telegram.handlers.TelegramUpdateHandler
import com.kvsinyuk.stickergenerator.applicaiton.port.out.http.FaceSwapPort
import com.kvsinyuk.stickergenerator.applicaiton.port.out.mongo.DeleteBotDataPort
import com.kvsinyuk.stickergenerator.applicaiton.port.out.mongo.FindBotDataPort
import com.kvsinyuk.stickergenerator.applicaiton.port.out.mongo.GetBotDataPort
import com.kvsinyuk.stickergenerator.applicaiton.port.out.telegram.TelegramFilePort
import com.kvsinyuk.stickergenerator.applicaiton.port.out.telegram.TelegramMessagePort
import com.kvsinyuk.stickergenerator.domain.TelegramUpdateMessage
import com.kvsinyuk.stickergenerator.domain.faceswap.FaceSwapStatus
import com.kvsinyuk.stickergenerator.domain.faceswap.Image
import org.springframework.stereotype.Component

@Component
class TargetImageHandler(
    private val telegramMessagePort: TelegramMessagePort,
    private val deleteBotDataPort: DeleteBotDataPort,
    private val getBotDataPort: GetBotDataPort,
    private val findBotDataPort: FindBotDataPort,
    private val telegramFilePort: TelegramFilePort,
    private val faceSwapPort: FaceSwapPort,
) : TelegramUpdateHandler {

    override fun process(update: TelegramUpdateMessage) {
        val file = telegramFilePort.getFileContent(update.document!!.fileId())
        val faceSwapData = getBotDataPort.getByChatId(update.chatId)
            .getAsFaceSwapData()
            .apply { targetImage = Image(file, update.document.fileName()) }

        val resultImage = faceSwapPort.swapFace(update.chatId, faceSwapData.sourceImage, faceSwapData.targetImage)
        telegramMessagePort.sendDocument(update.chatId, resultImage, faceSwapData.targetImage.fileName)
            .also { deleteBotDataPort.delete(update.chatId) }
    }

    override fun canApply(update: TelegramUpdateMessage): Boolean {
        if (update.document != null) {
            val botData = findBotDataPort.findByChatId(update.chatId)
            return botData?.commandData?.isFaceSwapData() == true
                    && botData.getAsFaceSwapData().status == FaceSwapStatus.SOURCE_FILE_ADDED
        }
        return false
    }
}
