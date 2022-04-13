package com.mulesoft.training.slack.secprops.slack

import com.mulesoft.training.slack.secprops.tool.SecurePropertiesToolFacade
import com.mulesoft.training.slack.secprops.tool.SecurePropertiesToolFacade.Operation.DECRYPT
import com.mulesoft.training.slack.secprops.tool.SecurePropertiesToolFacade.Operation.ENCRYPT
import com.slack.api.bolt.context.builtin.GlobalShortcutContext
import com.slack.api.bolt.context.builtin.ViewSubmissionContext
import com.slack.api.bolt.request.builtin.GlobalShortcutRequest
import com.slack.api.bolt.request.builtin.ViewSubmissionRequest
import com.slack.api.bolt.response.Response
import com.slack.api.model.view.ViewState
import org.slf4j.LoggerFactory
import javax.enterprise.context.ApplicationScoped


@ApplicationScoped
class ModalController(
    private val tool: SecurePropertiesToolFacade,
    private val views: Views
) {
    companion object {
        private val log = LoggerFactory.getLogger(ModalController::class.java)
    }

    /**
     * In response to a global shortcut, open the corresponding modal.
     */
    fun openModalByGlobalShortcut(
        operation: SecurePropertiesToolFacade.Operation, req: GlobalShortcutRequest, ctx: GlobalShortcutContext
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
    fun cryptoByViewSubmission(
        operation: SecurePropertiesToolFacade.Operation, req: ViewSubmissionRequest, ctx: ViewSubmissionContext
    ): Response {
        val cmd = req.payload.view.callbackId
        val vls = req.payload.view.state.values
        log.info("Handling view submission for $cmd with values '$vls'")
        val args = parseViewSubmissionValues(vls)
        return if (args == null) {
            ctx.ackWithErrors(mapOf("Missing arguments" to "algorithm mode key value [use random IVs]"))
        } else {
            try {
                val result = args.invokeTool(tool, SecurePropertiesToolFacade.Method.STRING, operation)
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

    private fun parseViewSubmissionValues(vals: Map<String, Map<String, ViewState.Value>>?) = try {
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
}