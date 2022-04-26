package com.mulesoft.training.slack.secprops.persistence

import com.slack.api.bolt.model.Bot
import org.bson.codecs.pojo.annotations.BsonId

/**
 * The persistent representation of [Bot], for storage and retrieval in MongoDB.
 */
data class BotInstallation(
    @field:BsonId var key: Key? = null,
    var bot: Bot? = null
) {
    companion object {
        data class Key(
            var appDbId: String? = null, // to distinguish this app's persistent (DB) state from that of other apps
            var enterpriseId: String? = null,
            var teamId: String? = null
        ) {
            constructor(appDbId: String, b: Bot) : this(appDbId, b.enterpriseId, b.teamId)
        }
    }

    constructor(appDbId: String, b: Bot) : this(Key(appDbId, b), b)
}
