package com.mulesoft.training.slack.secprops.tool

import org.slf4j.LoggerFactory
import javax.enterprise.context.Dependent

@Dependent
class SecurePropertiesToolDelegate : SecurePropertiesToolFacade() {
    private val toolJarDir = "src/main/resources"
    private val toolJarFile = "secure-properties-tool.jar" // must match ./get-secure-props-tool.sh

    companion object {
        val log = LoggerFactory.getLogger(SecurePropertiesToolDelegate::class.java)
    }

    override fun invoke(
        method: Method, operation: Operation,
        algorithm: String, mode: String, key: String, value: String, useRandomIVs: Boolean
    ): String {
        log.info("Invoke with $method $operation $algorithm $mode $key $value $useRandomIVs")
        return "TODO output of $method $operation $algorithm $mode $key $value $useRandomIVs"
    }
}