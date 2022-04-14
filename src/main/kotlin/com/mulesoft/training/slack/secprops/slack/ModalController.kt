package com.mulesoft.training.slack.secprops.slack

import com.mulesoft.training.slack.secprops.tool.SecurePropertiesToolFacade
import com.mulesoft.training.slack.secprops.tool.SecurePropertiesToolFacade.Method.STRING
import com.mulesoft.training.slack.secprops.tool.SecurePropertiesToolFacade.Operation
import com.slack.api.bolt.context.builtin.GlobalShortcutContext
import com.slack.api.bolt.context.builtin.ViewSubmissionContext
import com.slack.api.bolt.request.builtin.GlobalShortcutRequest
import com.slack.api.bolt.request.builtin.ViewSubmissionRequest
import com.slack.api.bolt.response.Response
import com.slack.api.methods.AsyncMethodsClient
import com.slack.api.model.view.ViewState
import org.slf4j.LoggerFactory
import javax.enterprise.context.ApplicationScoped


@ApplicationScoped
class ModalController(
    private val tool: SecurePropertiesToolFacade,
    private val views: Views
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    /**
     * In response to a global shortcut, open the corresponding modal.
     */
    fun openModalByGlobalShortcut(req: GlobalShortcutRequest, ctx: GlobalShortcutContext): Response {
        val cmd = req.payload.callbackId // the global shortcut: "encrypt" or "decrypt"
        log.info("Handling global shortcut $cmd")
        val oper = Operation.fromArg(cmd)
        openModal(ctx.asyncClient(), ctx.triggerId, oper)
        return ctx.ack() // always ack, no matter the success of opening the modal
    }

    /**
     * Open modal view async and don't wait for completion
     */
    fun openModal(client: AsyncMethodsClient, triggerId: String, operation: Operation) =
        client.viewsOpen {
            it.triggerId(triggerId).viewAsString(views.crypto(operation))
        }

    /**
     * In response to the modal's view having been submitted, en/decrypt using the state values in the view submission.
     */
    fun cryptoByViewSubmission(req: ViewSubmissionRequest, ctx: ViewSubmissionContext): Response {
        val cmd = req.payload.view.callbackId // the callback ID as stated in the submitted view: "encrypt" or "decrypt"
        val vls = req.payload.view.state.values // the state values in the view submission
        log.info("Handling view submission for $cmd with values '$vls'")
        val oper = Operation.fromArg(cmd)
        val args = parseViewSubmissionValues(vls)
        return if (args == null) {
            ctx.ackWithErrors(mapOf("Missing arguments" to "algorithm mode key value [use random IVs]"))
        } else {
            try {
                val result = args.invokeTool(tool, STRING, oper)
                // return result as a new view replacing the submitted view of the modal
                ctx.ackWithUpdate(views.cryptoResult(oper, result))
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