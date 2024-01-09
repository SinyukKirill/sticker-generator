package com.kvsinyuk.stickergenerator.adapter.out.http

import com.fasterxml.jackson.databind.ObjectMapper
import com.kvsinyuk.stickergenerator.adapter.out.http.request.FaceSwapRequest
import com.kvsinyuk.stickergenerator.adapter.out.http.response.FaceSwapResponse
import com.kvsinyuk.stickergenerator.applicaiton.port.out.http.FaceSwapPort
import com.kvsinyuk.stickergenerator.domain.faceswap.Image
import mu.KLogging
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@Component
class FaceSwapAdapter(
    private val client: OkHttpClient,
    private val objectMapper: ObjectMapper,
): FaceSwapPort {

    @Value("\${http.face-swap.base-url}")
    private lateinit var faceSwapBaseUrl: String

    val JSON: MediaType = "application/json; charset=utf-8".toMediaType()


    @OptIn(ExperimentalEncodingApi::class)
    override fun swapFace(chatId: Long, sourceImage: Image, targetImage: Image): ByteArray {
        val response = callForFaceSwap(chatId, sourceImage, targetImage)
        return Base64.decode(objectMapper.readValue(response.body!!.charStream(), FaceSwapResponse::class.java)
            .output)
    }

    @OptIn(ExperimentalEncodingApi::class)
    private fun callForFaceSwap(chatId: Long, sourceImage: Image, targetImage: Image): Response {
        val request: Request = Request.Builder()
            .url("$faceSwapBaseUrl/")
            .post(
                objectMapper.writeValueAsString(FaceSwapRequest(
                    Base64.encode(sourceImage.image),
                    Base64.encode(targetImage.image),
                    chatId.toString(),
                    sourceImage.fileName,
                    targetImage.fileName,
                )).toRequestBody(JSON)
            )
            .build()

        return client.newCall(request)
            .execute()
    }

    companion object: KLogging()
}