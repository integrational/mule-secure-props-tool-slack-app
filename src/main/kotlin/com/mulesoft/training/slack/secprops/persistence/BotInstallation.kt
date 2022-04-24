package com.mulesoft.training.slack.secprops.persistence

import com.slack.api.bolt.model.Bot
import org.bson.codecs.pojo.annotations.BsonId

data class BotInstallation(
    @field:BsonId var key: Key? = null,
    var bot: Bot? = null
) {
    companion object {
        data class Key(
            var enterpriseId: String? = null,
            var teamId: String? = null
        ) {
            constructor(b: Bot) : this(b.enterpriseId, b.teamId)
        }
    }

    constructor(b: Bot) : this(Key(b), b)
}
