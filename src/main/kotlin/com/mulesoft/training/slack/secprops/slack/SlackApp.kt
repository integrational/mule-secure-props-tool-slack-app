package com.mulesoft.training.slack.secprops.slack

import com.mulesoft.training.slack.secprops.slack.GlobalShortcutController.Companion.DECRYPT_SHORTCUT
import com.mulesoft.training.slack.secprops.slack.GlobalShortcutController.Companion.ENCRYPT_SHORTCUT
import com.mulesoft.training.slack.secprops.slack.ModalController.Companion.DECRYPT_CALLBACK_ID
import com.mulesoft.training.slack.secprops.slack.ModalController.Companion.ENCRYPT_CALLBACK_ID
import com.mulesoft.training.slack.secprops.slack.SlashCommandController.Companion.DECRYPT_SLASH_CMD
import com.mulesoft.training.slack.secprops.slack.SlashCommandController.Companion.ENCRYPT_SLASH_CMD
import com.slack.api.bolt.App
import javax.enterprise.context.ApplicationScoped


/**
 * Configures and provides access to the Slack Bolt [App].
 */
@ApplicationScoped
class SlackApp(
    private val globalShortcutController: GlobalShortcutController,
    private val modalController: ModalController,
    private val slashCommandController: SlashCommandController
) {

    val app = App().apply {
        globalShortcut(ENCRYPT_SHORTCUT) { req, ctx -> globalShortcutController.openModalByGlobalShortcut(req, ctx) }
        globalShortcut(DECRYPT_SHORTCUT) { req, ctx -> globalShortcutController.openModalByGlobalShortcut(req, ctx) }

        viewSubmission(ENCRYPT_CALLBACK_ID) { req, ctx -> modalController.cryptoByViewSubmission(req, ctx) }
        viewSubmission(DECRYPT_CALLBACK_ID) { req, ctx -> modalController.cryptoByViewSubmission(req, ctx) }

        command(ENCRYPT_SLASH_CMD) { req, ctx -> slashCommandController.cryptoBySlashCommand(req, ctx) }
        command(DECRYPT_SLASH_CMD) { req, ctx -> slashCommandController.cryptoBySlashCommand(req, ctx) }
    }
}
