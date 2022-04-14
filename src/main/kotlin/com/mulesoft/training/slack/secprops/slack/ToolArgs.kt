package com.mulesoft.training.slack.secprops.slack

import com.mulesoft.training.slack.secprops.tool.SecurePropertiesToolFacade

import com.mulesoft.training.slack.secprops.tool.SecurePropertiesToolFacade.Method
import com.mulesoft.training.slack.secprops.tool.SecurePropertiesToolFacade.Operation

/**
 * Collects all command-line-args of the Secure Properties Tool apart from [Method] and [Operation]
 */
data class ToolArgs(
    val algorithm: String?, val mode: String?, val key: String?, val value: String?, val useRandomIVs: Boolean?
) {
    /** Init to all-`null`s */
    constructor() : this(null, null, null, null, null)

    fun isComplete() = algorithm != null && mode != null && key != null && value != null && useRandomIVs != null

    /**
     * Convenience method to call [SecurePropertiesToolFacade.invoke] with the arguments in this [ToolArgs] object.
     * @throws IllegalStateException if [isComplete] does not return `true`
     */
    fun invokeTool(tool: SecurePropertiesToolFacade, method: Method, operation: Operation) =
        if (isComplete()) tool.invoke(method, operation, algorithm!!, mode!!, key!!, value!!, useRandomIVs!!)
        else throw IllegalStateException("some args are missing")
}