package com.mulesoft.training.slack.secprops

import com.slack.api.bolt.App
import com.slack.api.bolt.response.Response

internal fun app() = App().apply {
    command("/hello") { ctx, req ->
        Response.ok(":wave: Hi there!")
    }
}
