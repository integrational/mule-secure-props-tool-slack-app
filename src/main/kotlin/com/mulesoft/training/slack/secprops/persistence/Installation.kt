package com.mulesoft.training.slack.secprops.persistence

import com.slack.api.bolt.model.Installer
import org.bson.codecs.pojo.annotations.BsonId

/**
 * The persistent representation of [Installer], for storage and retrieval in MongoDB.
 */
data class Installation constructor(
    @field:BsonId var key: Key? = null,
    var installer: Installer? = null
) {
    companion object {
        data class Key(
            var appDbId: String? = null, // to distinguish this app's persistent (DB) state from that of other apps
            var enterpriseId: String? = null,
            var teamId: String? = null,
            var installerUserId: String? = null
        ) {
            constructor(appDbId: String, i: Installer) : this(appDbId, i.enterpriseId, i.teamId, i.installerUserId)
        }
    }

    constructor(appDbId: String, i: Installer) : this(Key(appDbId, i), i)
}
