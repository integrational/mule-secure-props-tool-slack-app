package com.mulesoft.training.slack.secprops.slack

import com.mulesoft.training.slack.secprops.tool.SecurePropertiesToolFacade
import com.mulesoft.training.slack.secprops.tool.SecurePropertiesToolFacade.Method
import com.mulesoft.training.slack.secprops.tool.SecurePropertiesToolFacade.Operation
import com.slack.api.bolt.App
import com.slack.api.bolt.context.builtin.SlashCommandContext
import com.slack.api.bolt.request.builtin.SlashCommandRequest
import com.slack.api.bolt.response.Response
import org.slf4j.LoggerFactory
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class SlackApp(val tool: SecurePropertiesToolFacade) {
    val log = LoggerFactory.getLogger(this::class.java)

    val app = App().apply {
        command("/encrypt") { req, ctx -> crypto(Operation.ENCRYPT, req, ctx) }
        command("/decrypt") { req, ctx -> crypto(Operation.DECRYPT, req, ctx) }
    }

    private fun crypto(operation: Operation, req: SlashCommandRequest, ctx: SlashCommandContext): Response {
        log.info("Handling command ${req.payload.command} with text '${req.payload.text}'")
        val result =
            ToolArgs.fromText(req.payload.text)?.let {
                try {
                    tool.invoke(Method.STRING, operation, it.algorithm, it.mode, it.key, it.value, it.useRandomIVs)
                } catch (e: Throwable) {
                    e.message
                }
            } ?: "Missing arguments: algorithm mode key value [use random IVs]"
        return ctx.ack(result).also {
            log.info("Ack-ed command ${req.payload.command} with '$it'")
        }
    }
}

/** Collects all additional command-line-args of the Secure Properties Tool */
private data class ToolArgs(
    val algorithm: String,
    val mode: String,
    val key: String,
    val value: String,
    val useRandomIVs: Boolean
) {
    companion object {
        fun fromText(text: String) =
            try {
                text.split(' ').let {
                    ToolArgs(
                        algorithm = it[0],
                        mode = it[1],
                        key = it[2],
                        value = it[3],
                        useRandomIVs = it.getOrNull(4).toBoolean()
                    )
                }
            } catch (e: Throwable) {
                null // if not sufficient args
            }
    }
}