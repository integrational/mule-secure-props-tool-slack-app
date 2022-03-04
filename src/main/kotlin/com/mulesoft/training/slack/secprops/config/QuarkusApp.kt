package com.mulesoft.training.slack.secprops.config

import io.smallrye.config.ConfigMapping

/**
 * Makes default quarkus.application.* config properties available for injections as an object.
 */
@ConfigMapping(prefix = "quarkus.application")
interface QuarkusApp {
    fun name(): String
    fun version(): String
}