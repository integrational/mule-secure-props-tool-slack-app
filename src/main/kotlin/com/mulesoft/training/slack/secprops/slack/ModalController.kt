package com.mulesoft.training.slack.secprops.slack

import com.mulesoft.training.slack.secprops.tool.SecurePropertiesToolFacade
import com.mulesoft.training.slack.secprops.tool.SecurePropertiesToolFacade.Method
import com.mulesoft.training.slack.secprops.tool.SecurePropertiesToolFacade.Operation
import com.slack.api.bolt.context.builtin.ViewSubmissionContext
import com.slack.api.bolt.request.builtin.ViewSubmissionRequest
import com.slack.api.bolt.response.Response
import com.slack.api.methods.AsyncMethodsClient
import com.slack.api.model.view.ViewState
import org.slf4j.LoggerFactory
import javax.enterprise.context.ApplicationScoped


@ApplicationScoped
class ModalController(
    private val views: Views,
    private val tool: SecurePropertiesToolFacade
) {
    companion object {
        val ENCRYPT_CALLBACK_ID = Operation.ENCRYPT.arg
        val DECRYPT_CALLBACK_ID = Operation.DECRYPT.arg

        fun operationFromCallbackId(cbId: String) = Operation.fromArg(cbId)
    }

    private val log = LoggerFactory.getLogger(this::class.java)

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
        val cbId = req.payload.view.callbackId // the callback ID as stated in the submitted view
        val vals = req.payload.view.state.values // the state values in the view submission
        log.info("Handling view submission for $cbId with values '$vals'")
        val oper = operationFromCallbackId(cbId)
        val args = parseViewSubmissionValues(vals)
        return if (!args.isComplete()) {
            ctx.ackWithErrors(mapOf("Missing arguments" to "algorithm mode key value [use random IVs]"))
        } else {
            try {
                val result = args.invokeTool(tool, Method.STRING, oper)
                // return result as a new view replacing the submitted view of the modal
                ctx.ackWithUpdate(views.cryptoResult(oper, result))
            } catch (e: Throwable) {
                ctx.ackWithErrors(mapOf("Execution error" to e.message))
            }
        }.also {
            log.info("Responding to view submission for $cbId with '$it'")
        }
    }

    private fun parseViewSubmissionValues(vals: Map<String, Map<String, ViewState.Value>>?) = vals?.let {
        ToolArgs(
            algorithm = tryOrNull { it["algorithm"]?.get("algorithm")?.selectedOption?.value },
            mode = tryOrNull { it["mode"]?.get("mode")?.selectedOption?.value },
            key = tryOrNull { it["key"]?.get("key")?.value },
            value = tryOrNull { it["value"]?.get("value")?.value },
            useRandomIVs = tryOrNull { it["useRandomIVs"]?.get("useRandomIVs")?.selectedOptions?.getOrNull(0)?.value.toBoolean() }
        )
    } ?: ToolArgs()

    private inline fun <T> tryOrNull(f: () -> T) =
        try {
            f()
        } catch (_: Exception) {
            null
        }
}