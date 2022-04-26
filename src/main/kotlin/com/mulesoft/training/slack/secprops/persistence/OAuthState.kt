package com.mulesoft.training.slack.secprops.persistence

import org.bson.codecs.pojo.annotations.BsonId

/**
 * The persistent representation of an OAuth state [String], for storage and retrieval in MongoDB.
 */
data class OAuthState(
    @field:BsonId var state: String? = null
)
