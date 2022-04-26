package com.mulesoft.training.slack.secprops.persistence

import com.slack.api.bolt.model.Bot
import com.slack.api.bolt.model.Installer
import com.slack.api.bolt.model.builtin.DefaultBot
import com.slack.api.bolt.model.builtin.DefaultInstaller
import org.bson.codecs.Codec
import org.bson.codecs.configuration.CodecProvider
import org.bson.codecs.configuration.CodecRegistry

class InstallerCodecProvider : CodecProvider {
    @Suppress("UNCHECKED_CAST")
    override fun <T> get(clazz: Class<T>?, registry: CodecRegistry?): Codec<T>? =
        if (clazz == Installer::class.java) registry?.get(DefaultInstaller::class.java) as Codec<T>
        else null
}

class BotCodecProvider : CodecProvider {
    @Suppress("UNCHECKED_CAST")
    override fun <T> get(clazz: Class<T>?, registry: CodecRegistry?): Codec<T>? =
        if (clazz == Bot::class.java) registry?.get(DefaultBot::class.java) as Codec<T>
        else null
}
