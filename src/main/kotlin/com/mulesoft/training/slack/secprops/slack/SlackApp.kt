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
        globalShortcut(ENCRYPT.arg) { req, ctx -> modalController.openModalByGlobalShortcut(req, ctx) }
        globalShortcut(DECRYPT.arg) { req, ctx -> modalController.openModalByGlobalShortcut(req, ctx) }

        viewSubmission(ENCRYPT.arg) { req, ctx -> modalController.cryptoByViewSubmission(req, ctx) }
        viewSubmission(DECRYPT.arg) { req, ctx -> modalController.cryptoByViewSubmission(req, ctx) }

        command("/${ENCRYPT.arg}") { req, ctx -> slashCommandController.cryptoBySlashCommand(req, ctx) }
        command("/${DECRYPT.arg}") { req, ctx -> slashCommandController.cryptoBySlashCommand(req, ctx) }
    }
}
