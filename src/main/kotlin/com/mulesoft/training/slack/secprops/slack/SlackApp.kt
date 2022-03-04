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
        command("/encrypt") { req, ctx -> encrypt(req, ctx) }
        command("/decrypt") { req, ctx -> decrypt(req, ctx) }
    }

    private fun encrypt(req: SlashCommandRequest, ctx: SlashCommandContext): Response {
        log.info("Handling ${req.payload.command} with ${req.payload.text}")
        val result = try {
            val args = ToolArgs.fromText(req.payload.text)
            tool.invoke(
                Method.STRING, Operation.ENCRYPT,
                args.algorithm, args.mode, args.key, args.value, args.useRandomIVs
            )
        } catch (e: Throwable) {
            "Please provide: algorithm mode key value [use random IVs]"
        }
        return ctx.ack(result)
    }

    private fun decrypt(req: SlashCommandRequest, ctx: SlashCommandContext): Response {
        log.info("Handling ${req.payload.command} with ${req.payload.text}")
        val result = try {
            val args = ToolArgs.fromText(req.payload.text)
            tool.invoke(
                Method.STRING, Operation.DECRYPT,
                args.algorithm, args.mode, args.key, args.value, args.useRandomIVs
            )
        } catch (e: Throwable) {
            "Please provide: algorithm mode key value [use random IVs]"
        }
        return ctx.ack(result)
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
        fun fromText(text: String) = text.split(' ').let {
            ToolArgs(
                algorithm = it[0],
                mode = it[1],
                key = it[2],
                value = it[3],
                useRandomIVs = it.getOrNull(4).toBoolean()
            )
        }
    }
}