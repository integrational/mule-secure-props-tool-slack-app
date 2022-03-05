package com.mulesoft.training.slack.secprops.slack

import com.mulesoft.training.slack.secprops.tool.SecurePropertiesToolFacade
import com.mulesoft.training.slack.secprops.tool.SecurePropertiesToolFacade.Method
import com.mulesoft.training.slack.secprops.tool.SecurePropertiesToolFacade.Operation
import com.slack.api.app_backend.slash_commands.response.SlashCommandResponse
import com.slack.api.bolt.App
import com.slack.api.bolt.context.builtin.SlashCommandContext
import com.slack.api.bolt.request.builtin.SlashCommandRequest
import com.slack.api.bolt.response.Response
import com.slack.api.model.block.Blocks.asBlocks
import com.slack.api.model.block.Blocks.section
import com.slack.api.model.block.composition.BlockCompositions.markdownText
import org.slf4j.LoggerFactory
import javax.enterprise.context.ApplicationScoped


/**
 * Configures and provides access to the Slack Bolt [App].
 */
@ApplicationScoped
class SlackApp(private val tool: SecurePropertiesToolFacade) {
    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
    }

    val app = App().apply {
        command("/encrypt") { req, ctx -> crypto(Operation.ENCRYPT, req, ctx) }
        command("/decrypt") { req, ctx -> crypto(Operation.DECRYPT, req, ctx) }
    }

    private fun crypto(operation: Operation, req: SlashCommandRequest, ctx: SlashCommandContext): Response {
        log.info("Handling command ${req.payload.command} with text '${req.payload.text}'")
        val args = ToolArgs.fromText(req.payload.text)
        return if (args == null) {
            ctx.ack(":warning: Missing arguments: algorithm mode key value [use random IVs]")
        } else {
            try {
                val result = tool.invoke(
                    Method.STRING, operation, args.algorithm, args.mode, args.key, args.value, args.useRandomIVs
                )
                // return result as Markdown code block
                ctx.ack(asBlocks(section { it.text(markdownText("```$result```")) }))
            } catch (e: Throwable) {
                ctx.ack(":warning: ${e.message}") // failed to invoke tool, respond with exception message
            }
        }.also {
            log.info("Responding to command ${req.payload.command} with '$it'")
        }
    }
}

/** Collects all additional command-line-args of the Secure Properties Tool */
private data class ToolArgs(
    val algorithm: String, val mode: String, val key: String, val value: String, val useRandomIVs: Boolean
) {
    companion object {
        fun fromText(text: String) = try {
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