package com.kvsinyuk.stickergenerator.applicaiton.impl

import com.kvsinyuk.stickergenerator.applicaiton.port.AddTextService
import com.kvsinyuk.stickergenerator.applicaiton.port.CropImageService
import com.kvsinyuk.stickergenerator.applicaiton.port.ResizeImageService
import com.kvsinyuk.stickergenerator.applicaiton.port.`in`.CreateStickerUseCase
import com.kvsinyuk.stickergenerator.applicaiton.port.out.RemoveBackgroundPort
import com.kvsinyuk.stickergenerator.applicaiton.utils.mapToByteArray
import com.kvsinyuk.stickergenerator.domain.StickerData
import org.springframework.stereotype.Component

@Component
class CreateStickerUseCaseImpl(
    private val removeBackgroundPort: RemoveBackgroundPort,
    private val cropImageService: CropImageService,
    private val resizeImageService: ResizeImageService,
    private val addTextService: AddTextService
) : CreateStickerUseCase {

    override fun createSticker(stickerData: StickerData) =
        removeBackgroundPort.removeBackground(stickerData)
            .let { cropImageService.cropImage(it) }
            .let { resizeImageService.resizeBufferedImage(it) }
            .let { addTextService.addText(it, stickerData.topText, true) }
            .let { addTextService.addText(it, stickerData.bottomText, false) }
            .mapToByteArray()
}