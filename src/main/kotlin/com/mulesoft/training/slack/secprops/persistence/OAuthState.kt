package com.mulesoft.training.slack.secprops.persistence

import org.bson.codecs.pojo.annotations.BsonId

data class OAuthState(
    @field:BsonId var state: String? = null
)
