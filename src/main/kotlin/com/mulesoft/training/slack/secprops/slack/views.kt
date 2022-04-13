package com.mulesoft.training.slack.secprops.slack

import com.mulesoft.training.slack.secprops.slack.Templates.cryptoView
import io.quarkus.qute.CheckedTemplate
import io.quarkus.qute.TemplateInstance
import javax.enterprise.context.ApplicationScoped

/**
 * Facade to Slack Block Kit Views rendered from JSON template files.
 */
@ApplicationScoped
class Views {
    fun encrypt(): String = cryptoView("Encrypt").render()
    fun decrypt(): String = cryptoView("Decrypt").render()
}

/**
 * Follow the guidelines/conventions for Top-level type-safe Qute templates in Quarkus.
 * Template files are in `resources/templates` and must match the names of methods in this class.
 */
@CheckedTemplate
object Templates {
    @JvmStatic
    external fun cryptoView(operationTitleCase: String): TemplateInstance
}