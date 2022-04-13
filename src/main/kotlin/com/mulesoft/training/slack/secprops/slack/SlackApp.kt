package com.mulesoft.training.slack.secprops.slack

import com.mulesoft.training.slack.secprops.tool.SecurePropertiesToolFacade
import com.mulesoft.training.slack.secprops.tool.SecurePropertiesToolFacade.Method
import com.mulesoft.training.slack.secprops.tool.SecurePropertiesToolFacade.Operation
import com.mulesoft.training.slack.secprops.tool.SecurePropertiesToolFacade.Operation.DECRYPT
import com.mulesoft.training.slack.secprops.tool.SecurePropertiesToolFacade.Operation.ENCRYPT
import com.slack.api.bolt.App
import com.slack.api.bolt.context.builtin.GlobalShortcutContext
import com.slack.api.bolt.context.builtin.SlashCommandContext
import com.slack.api.bolt.request.builtin.GlobalShortcutRequest
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
class SlackApp(
    private val tool: SecurePropertiesToolFacade,
    private val views: Views
) {
    companion object {
        private val log = LoggerFactory.getLogger(SlackApp::class.java)
    }

    val app = App().apply {
        command("/encrypt") { req, ctx -> cryptoBySlashCommand(ENCRYPT, req, ctx) }
        command("/decrypt") { req, ctx -> cryptoBySlashCommand(DECRYPT, req, ctx) }

        globalShortcut("encrypt") { req, ctx -> openCryptoModalByGlobalShortcut(ENCRYPT, req, ctx) }
        globalShortcut("decrypt") { req, ctx -> openCryptoModalByGlobalShortcut(DECRYPT, req, ctx) }

        viewSubmission("encrypt") { req, ctx ->
            log.info("Handling view submission for ${req.payload.view.callbackId} with value ${req.payload.view.state.values}")
            val vals = req.payload.view.state.values
            val value = vals["value"]?.get("value")?.value
            val key = vals["key"]?.get("key")?.value
            val algorithm = vals["algorithm"]?.get("algorithm")?.selectedOption?.value
            val mode = vals["mode"]?.get("mode")?.selectedOption?.value
            val useRandomIVs =
                vals["useRandomIVs"]?.get("useRandomIVs")?.selectedOptions?.getOrNull(0)?.value.toBoolean()
            log.info("encrypt $value $key $algorithm $mode $useRandomIVs")
            val result = 666
            ctx.respond(asBlocks(section { it.text(markdownText("```$result```")) }))
//            ctx.asyncClient().viewsPush {
//                it.viewAsString("")
//            }
            ctx.ack()
        }
    }

    private fun cryptoBySlashCommand(
        operation: Operation, req: SlashCommandRequest, ctx: SlashCommandContext
    ): Response {
        log.info("Handling command ${req.payload.command} with text '${req.payload.text}'")
        val args = ToolArgs.fromText(req.payload.text)
        return if (args == null) {
            ctx.ack(":warning: Missing arguments: algorithm mode key value [use random IVs]")
        } else {
            try {
                val result = tool.invoke(
                    Method.STRING, operation, args.algorithm, args.mode, args.key, args.value, args.useRandomIVs
                )
                // return result as Markdown code block in ephemeral message
                ctx.ack(asBlocks(section { it.text(markdownText("```$result```")) }))
            } catch (e: Throwable) {
                ctx.ack(":warning: ${e.message}") // failed to invoke tool, respond with exception message
            }
        }.also {
            log.info("Responding to command ${req.payload.command} with '$it'")
        }
    }

    private fun openCryptoModalByGlobalShortcut(
        operation: Operation, req: GlobalShortcutRequest, ctx: GlobalShortcutContext
    ): Response {
        log.info("Handling global shortcut ${req.payload.callbackId}")
        val view = when (operation) {
            ENCRYPT -> views.encrypt()
            DECRYPT -> views.decrypt()
        }
        // open modal view async and don't wait for completion
        ctx.asyncClient().viewsOpen {
            it.triggerId(ctx.triggerId).viewAsString(view)
        }
        return ctx.ack() // always ack, no matter the success of opening the modal
    }
}

/** Collects all additional command-line-args of the Secure Properties Tool */
private data class ToolArgs(
    val algorithm: String, val mode: String, val key: String, val value: String, val useRandomIVs: Boolean
) {
    companion object {
        fun fromText(text: String?) = try {
            text?.split(' ')?.let {
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