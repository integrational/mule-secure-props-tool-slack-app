package com.mulesoft.training.slack.secprops.persistence

import com.slack.api.bolt.model.Installer
import org.bson.codecs.pojo.annotations.BsonId

data class Installation constructor(
    @field:BsonId var key: Key? = null,
    var installer: Installer? = null
) {
    companion object {
        data class Key(
            var enterpriseId: String? = null,
            var teamId: String? = null,
            var installerUserId: String? = null
        ) {
            constructor(i: Installer) : this(i.enterpriseId, i.teamId, i.installerUserId)
        }
    }

    constructor(i: Installer) : this(Key(i), i)
}
