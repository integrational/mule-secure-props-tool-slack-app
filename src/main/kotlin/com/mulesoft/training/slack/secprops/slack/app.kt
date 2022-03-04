package com.mulesoft.training.slack.secprops

import com.slack.api.bolt.App
import org.slf4j.LoggerFactory

internal fun app() = App().apply {
    val log = LoggerFactory.getLogger(this::class.java)

    command("/encrypt") { req, ctx ->
        log.info("Handling ${req.payload.command} with ${req.payload.text}")
        ctx.ack("encrypted string is ...")
    }

    command("/decrypt") { req, ctx ->
        log.info("Handling ${req.payload.command} with ${req.payload.text}")
        ctx.ack("decrypted string is ...")
    }
}
