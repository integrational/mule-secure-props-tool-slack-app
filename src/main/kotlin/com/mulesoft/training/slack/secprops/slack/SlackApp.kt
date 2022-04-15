package com.mulesoft.training.slack.secprops.slack

import com.mulesoft.training.slack.secprops.slack.GlobalShortcutController.Companion.DECRYPT_SHORTCUT_CBID
import com.mulesoft.training.slack.secprops.slack.GlobalShortcutController.Companion.ENCRYPT_SHORTCUT_CBID
import com.mulesoft.training.slack.secprops.slack.ModalController.Companion.DECRYPT_VIEW_CBID
import com.mulesoft.training.slack.secprops.slack.ModalController.Companion.ENCRYPT_VIEW_CBID
import com.mulesoft.training.slack.secprops.slack.SlashCommandController.Companion.DECRYPT_SLASH_CMD
import com.mulesoft.training.slack.secprops.slack.SlashCommandController.Companion.ENCRYPT_SLASH_CMD
import com.slack.api.bolt.App
import com.slack.api.model.event.AppHomeOpenedEvent
import javax.enterprise.context.ApplicationScoped


/**
 * Configures and provides access to the Slack Bolt [App].
 */
@ApplicationScoped
class SlackApp(
    private val appHomeController: AppHomeController,
    private val globalShortcutController: GlobalShortcutController,
    private val modalController: ModalController,
    private val slashCommandController: SlashCommandController
) {
    val app = App().apply {
        asOAuthApp(true)

        event(AppHomeOpenedEvent::class.java) { payload, ctx -> appHomeController.publishAppHome(payload, ctx) }

        globalShortcut(ENCRYPT_SHORTCUT_CBID) { req, ctx -> globalShortcutController.openModal(req, ctx) }
        globalShortcut(DECRYPT_SHORTCUT_CBID) { req, ctx -> globalShortcutController.openModal(req, ctx) }

        viewSubmission(ENCRYPT_VIEW_CBID) { req, ctx -> modalController.cryptoByViewSubmission(req, ctx) }
        viewSubmission(DECRYPT_VIEW_CBID) { req, ctx -> modalController.cryptoByViewSubmission(req, ctx) }

        command(ENCRYPT_SLASH_CMD) { req, ctx -> slashCommandController.cryptoBySlashCommand(req, ctx) }
        command(DECRYPT_SLASH_CMD) { req, ctx -> slashCommandController.cryptoBySlashCommand(req, ctx) }
    }
}
