package com.kvsinyuk.stickergenerator.adapter.out.http.request

import com.fasterxml.jackson.annotation.JsonProperty

data class FaceSwapRequest(
    val source: String, // base64 encoded source image
    val target: String, // base64 encoded target image
    @JsonProperty("user_id")
    val userId: String, // chatId of user
    @JsonProperty("source_type")
    val sourceType: String, // source file type
    @JsonProperty("target_type")
    val targetType: String, // target file type
    @JsonProperty("frame_processors")
    val frameProcessors: List<String> = listOf("face_swapper") // constant value of processor
)