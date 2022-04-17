package com.mulesoft.training.slack.secprops.persistence

import org.bson.codecs.pojo.annotations.BsonId

data class UserSettings(
    @field:BsonId var userID: String? = null,
    var algorithm: String? = null,
    var mode: String? = null,
    var key: String? = null,
    var useRandomIVs: Boolean? = null
)