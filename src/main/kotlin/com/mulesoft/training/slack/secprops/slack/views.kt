package com.mulesoft.training.slack.secprops.slack

import com.mulesoft.training.slack.secprops.slack.Templates.cryptoView
import com.mulesoft.training.slack.secprops.slack.Templates.cryptoResultView
import io.quarkus.qute.CheckedTemplate
import io.quarkus.qute.TemplateInstance
import javax.enterprise.context.ApplicationScoped

/**
 * Facade to Slack Block Kit Views rendered from JSON template files.
 */
@ApplicationScoped
class Views {
    fun encrypt() = cryptoView("Encrypt").render()
    fun decrypt() = cryptoView("Decrypt").render()

    fun encryptedResult(result: String) = cryptoResultView("Encrypted", result).render()
    fun decryptedResult(result: String) = cryptoResultView("Decrypted", result).render()
}

/**
 * Follow the guidelines/conventions for Top-level type-safe Qute templates in Quarkus.
 * Template files are in `resources/templates` and must match the names of methods in this class.
 */
@CheckedTemplate
object Templates {
    @JvmStatic
    external fun cryptoView(operationTitleCase: String): TemplateInstance

    @JvmStatic
    external fun cryptoResultView(operationPastTenseTitleCase: String, result: String): TemplateInstance
}