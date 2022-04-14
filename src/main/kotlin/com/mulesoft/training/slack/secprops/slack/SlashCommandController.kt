package com.mulesoft.training.slack.secprops.slack

import com.mulesoft.training.slack.secprops.tool.SecurePropertiesToolFacade
import com.mulesoft.training.slack.secprops.tool.SecurePropertiesToolFacade.Method
import com.mulesoft.training.slack.secprops.tool.SecurePropertiesToolFacade.Operation
import com.slack.api.bolt.context.builtin.SlashCommandContext
import com.slack.api.bolt.request.builtin.SlashCommandRequest
import com.slack.api.bolt.response.Response
import com.slack.api.model.block.Blocks
import com.slack.api.model.block.composition.BlockCompositions
import org.slf4j.LoggerFactory
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class SlashCommandController(
    private val modalController: ModalController,
    private val tool: SecurePropertiesToolFacade
) {
    companion object {
        val ENCRYPT_SLASH_CMD = "/" + Operation.ENCRYPT.arg
        val DECRYPT_SLASH_CMD = "/" + Operation.DECRYPT.arg

        val ENCRYPT_ARGS = "algorithm mode key value [use random IVs]"
        val DECRYPT_ARGS = "algorithm mode key value [use random IVs]"

        fun operationFromSlashCommand(cmd: String) = Operation.fromArg(cmd.trimStart('/')) // remove leading '/'
    }

    private val log = LoggerFactory.getLogger(this::class.java)

    /**
     * In response to slash command having been submitted, en/decrypt using the argument text submitted with the command.
     */
    fun cryptoBySlashCommand(req: SlashCommandRequest, ctx: SlashCommandContext): Response {
        val cmd = req.payload.command // the literal slash command
        val txt = req.payload.text // the argument text after the slash command itself
        log.info("Handling command $cmd with text '$txt'")
        val oper = operationFromSlashCommand(cmd)
        val args = parseSlashCommandText(txt)
        return if (!args.isComplete()) {
            log.info("Missing arguments: opening modal instead of performing crypto")
            modalController.openModal(ctx.asyncClient(), ctx.triggerId, oper)
            ctx.ack() // always ack, no matter the success of opening the modal
        } else {
            try {
                val result = args.invokeTool(tool, Method.STRING, oper)
                // return result as Markdown code block in ephemeral message
                ctx.ack(Blocks.asBlocks(Blocks.section { it.text(BlockCompositions.markdownText("```$result```")) }))
            } catch (e: Throwable) {
                ctx.ack(":warning: ${e.message}") // failed to invoke tool, respond with exception message
            }
        }.also {
            log.info("Responding to command $cmd with '$it'")
        }
    }

    private fun parseSlashCommandText(text: String?) = text?.split(' ')?.let {
        ToolArgs(
            algorithm = it.getOrNull(0),
            mode = it.getOrNull(1),
            key = it.getOrNull(2),
            value = it.getOrNull(3),
            useRandomIVs = it.getOrNull(4).toBoolean()
        )
    } ?: ToolArgs()

}