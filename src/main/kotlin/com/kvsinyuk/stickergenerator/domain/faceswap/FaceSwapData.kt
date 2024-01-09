package com.kvsinyuk.stickergenerator.domain.faceswap

import com.kvsinyuk.stickergenerator.domain.CommandData
import org.springframework.data.annotation.TypeAlias

@TypeAlias("face_swap")
data class FaceSwapData(
    var status: FaceSwapStatus = FaceSwapStatus.INIT
): CommandData() {

    lateinit var sourceImage: Image
    lateinit var targetImage: Image

    override fun isFaceSwapData() = true
}