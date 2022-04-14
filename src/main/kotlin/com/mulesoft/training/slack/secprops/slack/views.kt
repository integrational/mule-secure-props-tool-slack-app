package com.mulesoft.training.slack.secprops.slack

import com.mulesoft.training.slack.secprops.slack.ModalController.Companion.DECRYPT_CALLBACK_ID
import com.mulesoft.training.slack.secprops.slack.ModalController.Companion.ENCRYPT_CALLBACK_ID
import com.mulesoft.training.slack.secprops.slack.Templates.cryptoResultView
import com.mulesoft.training.slack.secprops.slack.Templates.cryptoView
import com.mulesoft.training.slack.secprops.tool.SecurePropertiesToolFacade.Operation
import com.mulesoft.training.slack.secprops.tool.SecurePropertiesToolFacade.Operation.DECRYPT
import com.mulesoft.training.slack.secprops.tool.SecurePropertiesToolFacade.Operation.ENCRYPT
import io.quarkus.qute.CheckedTemplate
import io.quarkus.qute.TemplateInstance
import javax.enterprise.context.ApplicationScoped

/**
 * Facade to Slack Block Kit Views rendered from JSON template files.
 */
@ApplicationScoped
class Views {
    fun crypto(operation: Operation) = when (operation) {
        ENCRYPT -> cryptoView("Encrypt", ENCRYPT_CALLBACK_ID)
        DECRYPT -> cryptoView("Decrypt", DECRYPT_CALLBACK_ID)
    }.render()

    fun cryptoResult(operation: Operation, result: String) = when (operation) {
        ENCRYPT -> cryptoResultView("Encrypted", result)
        DECRYPT -> cryptoResultView("Decrypted", result)
    }.render()
}

/**
 * Follow the guidelines/conventions for Top-level type-safe Qute templates in Quarkus.
 * Template files are in `resources/templates` and must match the names of methods in this class.
 */
@CheckedTemplate
object Templates {
    @JvmStatic
    external fun cryptoView(operationTitleCase: String, callbackId: String): TemplateInstance

    @JvmStatic
    external fun cryptoResultView(operationPastTenseTitleCase: String, result: String): TemplateInstance
}