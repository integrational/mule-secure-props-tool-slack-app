package com.mulesoft.training.slack.secprops.slack

import com.mulesoft.training.slack.secprops.tool.SecurePropertiesToolFacade
import com.mulesoft.training.slack.secprops.tool.SecurePropertiesToolFacade.Method
import com.mulesoft.training.slack.secprops.tool.SecurePropertiesToolFacade.Operation
import com.mulesoft.training.slack.secprops.tool.SecurePropertiesToolFacade.Operation.DECRYPT
import com.mulesoft.training.slack.secprops.tool.SecurePropertiesToolFacade.Operation.ENCRYPT
import com.slack.api.bolt.App
import com.slack.api.bolt.context.builtin.GlobalShortcutContext
import com.slack.api.bolt.context.builtin.SlashCommandContext
import com.slack.api.bolt.context.builtin.ViewSubmissionContext
import com.slack.api.bolt.request.builtin.GlobalShortcutRequest
import com.slack.api.bolt.request.builtin.SlashCommandRequest
import com.slack.api.bolt.request.builtin.ViewSubmissionRequest
import com.slack.api.bolt.response.Response
import com.slack.api.model.block.Blocks.asBlocks
import com.slack.api.model.block.Blocks.section
import com.slack.api.model.block.composition.BlockCompositions.markdownText
import com.slack.api.model.view.ViewState
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
        globalShortcut("encrypt") { req, ctx -> openCryptoModalByGlobalShortcut(ENCRYPT, req, ctx) }
        globalShortcut("decrypt") { req, ctx -> openCryptoModalByGlobalShortcut(DECRYPT, req, ctx) }

        viewSubmission("encrypt") { req, ctx -> cryptoByViewSubmission(ENCRYPT, req, ctx) }
        viewSubmission("decrypt") { req, ctx -> cryptoByViewSubmission(DECRYPT, req, ctx) }

        command("/encrypt") { req, ctx -> cryptoBySlashCommand(ENCRYPT, req, ctx) }
        command("/decrypt") { req, ctx -> cryptoBySlashCommand(DECRYPT, req, ctx) }
    }

    /**
     * In response to a global shortcut, open the corresponding modal.
     */
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

    /**
     * In response to the modal's view having been submitted, en/decrypt using the state values in the view submission.
     */
    private fun cryptoByViewSubmission(
        operation: Operation, req: ViewSubmissionRequest, ctx: ViewSubmissionContext
    ): Response {
        val cmd = req.payload.view.callbackId
        val vls = req.payload.view.state.values
        log.info("Handling view submission for $cmd with values '$vls'")
        val args = ToolArgs.fromViewSubmissionValues(vls)
        return if (args == null) {
            ctx.ackWithErrors(mapOf("Missing arguments" to "algorithm mode key value [use random IVs]"))
        } else {
            try {
                val result = args.invokeTool(tool, Method.STRING, operation)
                // return result as a new view replacing the submitted view of the modal
                val view = when (operation) {
                    ENCRYPT -> views.encryptedResult(result)
                    DECRYPT -> views.decryptedResult(result)
                }
                ctx.ackWithUpdate(view)
            } catch (e: Throwable) {
                ctx.ackWithErrors(mapOf("Execution error" to e.message))
            }
        }.also {
            log.info("Responding to view submission for $cmd with '$it'")
        }
    }

    /**
     * In response to slash command having been submitted, en/decrypt using the argument text submitted with the command.
     */
    private fun cryptoBySlashCommand(
        operation: Operation, req: SlashCommandRequest, ctx: SlashCommandContext
    ): Response {
        val cmd = req.payload.command
        val txt = req.payload.text
        log.info("Handling command $cmd with text '$txt'")
        val args = ToolArgs.fromSlashCommandText(txt)
        return if (args == null) {
            ctx.ack(":warning: Missing arguments: algorithm mode key value [use random IVs]")
        } else {
            try {
                val result = args.invokeTool(tool, Method.STRING, operation)
                // return result as Markdown code block in ephemeral message
                ctx.ack(asBlocks(section { it.text(markdownText("```$result```")) }))
            } catch (e: Throwable) {
                ctx.ack(":warning: ${e.message}") // failed to invoke tool, respond with exception message
            }
        }.also {
            log.info("Responding to command $cmd with '$it'")
        }
    }
}

/** Collects all additional command-line-args of the Secure Properties Tool */
private data class ToolArgs(
    val algorithm: String, val mode: String, val key: String, val value: String, val useRandomIVs: Boolean
) {
    companion object {
        fun fromViewSubmissionValues(vals: Map<String, Map<String, ViewState.Value>>?) = try {
            vals?.let {
                ToolArgs(
                    algorithm = it["algorithm"]?.get("algorithm")?.selectedOption!!.value,
                    mode = it["mode"]?.get("mode")?.selectedOption!!.value,
                    key = it["key"]?.get("key")!!.value,
                    value = it["value"]?.get("value")!!.value,
                    useRandomIVs = it["useRandomIVs"]?.get("useRandomIVs")?.selectedOptions?.getOrNull(0)?.value.toBoolean()
                )
            }
        } catch (e: Throwable) {
            null // if any missing arg or unexpected view structure
        }

        fun fromSlashCommandText(text: String?) = try {
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

    fun invokeTool(tool: SecurePropertiesToolFacade, method: Method, operation: Operation) =
        tool.invoke(method, operation, algorithm, mode, key, value, useRandomIVs)
}