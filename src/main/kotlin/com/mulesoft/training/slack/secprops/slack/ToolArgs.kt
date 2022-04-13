package com.mulesoft.training.slack.secprops.slack

import com.mulesoft.training.slack.secprops.tool.SecurePropertiesToolFacade

import com.mulesoft.training.slack.secprops.tool.SecurePropertiesToolFacade.Method
import com.mulesoft.training.slack.secprops.tool.SecurePropertiesToolFacade.Operation

/** Collects all additional command-line-args of the Secure Properties Tool */
data class ToolArgs(
    val algorithm: String, val mode: String, val key: String, val value: String, val useRandomIVs: Boolean
) {
    /** Convenience method to call [SecurePropertiesToolFacade.invoke] with this [ToolArgs] object */
    fun invokeTool(tool: SecurePropertiesToolFacade, method: Method, operation: Operation) =
        tool.invoke(method, operation, algorithm, mode, key, value, useRandomIVs)
}