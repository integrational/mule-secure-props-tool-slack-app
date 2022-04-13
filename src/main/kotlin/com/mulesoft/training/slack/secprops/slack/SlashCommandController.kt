package com.mulesoft.training.slack.secprops.slack

import com.mulesoft.training.slack.secprops.tool.SecurePropertiesToolFacade
import com.slack.api.bolt.context.builtin.SlashCommandContext
import com.slack.api.bolt.request.builtin.SlashCommandRequest
import com.slack.api.bolt.response.Response
import com.slack.api.model.block.Blocks
import com.slack.api.model.block.composition.BlockCompositions
import org.slf4j.LoggerFactory
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class SlashCommandController(
    private val tool: SecurePropertiesToolFacade
) {
    companion object {
        private val log = LoggerFactory.getLogger(SlashCommandController::class.java)
    }

    /**
     * In response to slash command having been submitted, en/decrypt using the argument text submitted with the command.
     */
    fun cryptoBySlashCommand(
        operation: SecurePropertiesToolFacade.Operation, req: SlashCommandRequest, ctx: SlashCommandContext
    ): Response {
        val cmd = req.payload.command
        val txt = req.payload.text
        log.info("Handling command $cmd with text '$txt'")
        val args = parseSlashCommandText(txt)
        return if (args == null) {
            ctx.ack(":warning: Missing arguments: algorithm mode key value [use random IVs]")
        } else {
            try {
                val result = args.invokeTool(tool, SecurePropertiesToolFacade.Method.STRING, operation)
                // return result as Markdown code block in ephemeral message
                ctx.ack(Blocks.asBlocks(Blocks.section { it.text(BlockCompositions.markdownText("```$result```")) }))
            } catch (e: Throwable) {
                ctx.ack(":warning: ${e.message}") // failed to invoke tool, respond with exception message
            }
        }.also {
            log.info("Responding to command $cmd with '$it'")
        }
    }

    private fun parseSlashCommandText(text: String?) = try {
        text?.split(' ')?.let {
            ToolArgs(
                algorithm = it[0],
                mode = it[1],
                key = it[2],
                value = it[3],
                useRandomIVs = it.getOrNull(4).toBoolean()
            )
        }
    } catch (e: Throwable) {
        null // if not sufficient args
    }
}