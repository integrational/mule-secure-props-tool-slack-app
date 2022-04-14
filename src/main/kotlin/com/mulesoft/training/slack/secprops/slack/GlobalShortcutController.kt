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
        val ENCRYPT_SHORTCUT = Operation.ENCRYPT.arg
        val DECRYPT_SHORTCUT = Operation.DECRYPT.arg

        fun operationFromShortcut(shortcut: String) = Operation.fromArg(shortcut)
    }

    private val log = LoggerFactory.getLogger(this::class.java)

    /**
     * In response to a global shortcut, open the corresponding modal.
     */
    fun openModalByGlobalShortcut(req: GlobalShortcutRequest, ctx: GlobalShortcutContext): Response {
        val shortcut = req.payload.callbackId // the global shortcut
        log.info("Handling global shortcut $shortcut")
        modalController.openModal(ctx.asyncClient(), ctx.triggerId, operationFromShortcut(shortcut))
        return ctx.ack() // always ack, no matter the success of opening the modal
    }
}