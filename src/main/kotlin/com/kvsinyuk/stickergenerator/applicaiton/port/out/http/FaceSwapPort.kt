package com.kvsinyuk.stickergenerator.applicaiton.port.out.http

import com.kvsinyuk.stickergenerator.domain.faceswap.Image

interface FaceSwapPort {

    fun swapFace(chatId: Long, sourceImage: Image, targetImage: Image): ByteArray
}
