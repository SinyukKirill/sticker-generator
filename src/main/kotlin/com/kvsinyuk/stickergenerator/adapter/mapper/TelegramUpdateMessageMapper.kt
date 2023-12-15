package com.kvsinyuk.stickergenerator.adapter.mapper

import com.kvsinyuk.stickergenerator.config.MapstructConfig
import com.kvsinyuk.stickergenerator.domain.TelegramUpdateMessage
import com.pengrad.telegrambot.model.Update
import org.mapstruct.Mapper
import org.mapstruct.Mapping

@Mapper(config = MapstructConfig::class)
abstract class TelegramUpdateMessageMapper {

    @Mapping(target = "chatId", expression = "java(update.message().chat().id())")
    @Mapping(target = "message", expression = "java(update.message().text())")
    @Mapping(target = "document", expression = "java(update.message().document())")
    @Mapping(target = "photos", expression = "java(update.message().photo())")
    abstract fun toMessage(update: Update): TelegramUpdateMessage
}