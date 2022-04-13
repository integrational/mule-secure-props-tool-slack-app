package com.mulesoft.training.slack.secprops.slack

import com.mulesoft.training.slack.secprops.tool.SecurePropertiesToolFacade.Operation.DECRYPT
import com.mulesoft.training.slack.secprops.tool.SecurePropertiesToolFacade.Operation.ENCRYPT
import com.slack.api.bolt.App
import javax.enterprise.context.ApplicationScoped


/**
 * Configures and provides access to the Slack Bolt [App].
 */
@ApplicationScoped
class SlackApp(
    private val modalController: ModalController,
    private val slashCommandController: SlashCommandController
) {

    val app = App().apply {
        globalShortcut("encrypt") { req, ctx -> modalController.openModalByGlobalShortcut(ENCRYPT, req, ctx) }
        globalShortcut("decrypt") { req, ctx -> modalController.openModalByGlobalShortcut(DECRYPT, req, ctx) }

        viewSubmission("encrypt") { req, ctx -> modalController.cryptoByViewSubmission(ENCRYPT, req, ctx) }
        viewSubmission("decrypt") { req, ctx -> modalController.cryptoByViewSubmission(DECRYPT, req, ctx) }

        command("/encrypt") { req, ctx -> slashCommandController.cryptoBySlashCommand(ENCRYPT, req, ctx) }
        command("/decrypt") { req, ctx -> slashCommandController.cryptoBySlashCommand(DECRYPT, req, ctx) }
    }
}
