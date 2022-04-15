package com.mulesoft.training.slack.secprops.slack

import com.mulesoft.training.slack.secprops.tool.SecurePropertiesToolFacade.Operation
import com.slack.api.bolt.context.builtin.GlobalShortcutContext
import com.slack.api.bolt.request.builtin.GlobalShortcutRequest
import com.slack.api.bolt.response.Response
import org.slf4j.LoggerFactory
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class GlobalShortcutController(private val modalController: ModalController) {
    companion object {
        val ENCRYPT_SHORTCUT_CBID = Operation.ENCRYPT.arg // callback ID of encrypt global shortcut
        val DECRYPT_SHORTCUT_CBID = Operation.DECRYPT.arg // callback ID of decrypt global shortcut

        val ENCRYPT_SHORTCUT_NAME = "Encrypt" // user-visible name of encrypt global shortcut
        val DECRYPT_SHORTCUT_NAME = "Decrypt" // user-visible name of decrypt global shortcut

        fun operationFromCallbackId(cbId: String) = Operation.fromArg(cbId)
    }

    private val log = LoggerFactory.getLogger(this::class.java)

    /**
     * In response to a global shortcut, open the corresponding modal.
     */
    fun openModal(req: GlobalShortcutRequest, ctx: GlobalShortcutContext): Response {
        val cbid = req.payload.callbackId // the global shortcut's callback ID
        log.info("Handling global shortcut with callback ID $cbid")
        modalController.openModal(ctx.asyncClient(), ctx.triggerId, operationFromCallbackId(cbid))
        return ctx.ack() // always ack, no matter the success of opening the modal
    }
}