package com.kvsinyuk.stickergenerator.domain.faceswap

data class Image(
    val image: ByteArray,
    val fileName: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Image

        if (fileName != other.fileName) return false
        if (image.size != other.image.size) return false

        return true
    }

    override fun hashCode(): Int {
        return fileName.hashCode() + image.size
    }
}