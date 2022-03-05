package com.mulesoft.training.slack.secprops.tool

import com.mulesoft.training.slack.secprops.tool.SecurePropertiesToolFacade.Method
import com.mulesoft.training.slack.secprops.tool.SecurePropertiesToolFacade.Operation
import org.slf4j.LoggerFactory
import java.net.URL
import java.net.URLClassLoader
import javax.enterprise.context.Dependent

@Dependent
class SecurePropertiesToolDelegate : SecurePropertiesToolFacade {
    companion object {
        private const val toolJarFileName = "secure-properties-tool.jar" // must match get-secure-props-tool.sh
        private const val toolMainClassName = "com.mulesoft.tools.SecurePropertiesTool"

        /**
         * Tool method to use for [invoke] with [Method.STRING]:
         * ```
         * public static String applyOverString(
         *   String action, String algorithm, String mode, String key, boolean useRandomIVs,
         *   String value
         * )
         * ```
         */
        private const val toolMethodNameForString = "applyOverString"

        /**
         * Tool method to use for [invoke] with [Method.FILE]:
         * ```
         * public static void applyOverFile(
         *   String action, String algorithm, String mode, String key, boolean useRandomIVs,
         *   String inputFilePath, String outputFilePath
         * )
         * ```
         */
        private const val toolMethodNameForFile = "applyOverFile"

        /**
         * Tool method to use for [invoke] with [Method.FILE_LEVEL]:
         * ```
         * public static void applyHoleFile(
         *   String action, String algorithm, String mode, String key, boolean useRandomIVs,
         *   String inputFilePath, String outputFilePath
         * )
         * ```
         */
        private const val toolMethodNameForFileLevel = "applyHoleFile"

        private val log = LoggerFactory.getLogger(SecurePropertiesToolDelegate::class.java)
    }

    override fun invoke(
        method: Method,
        operation: Operation,
        algorithm: String,
        mode: String,
        key: String,
        value: String,
        useRandomIVs: Boolean
    ): String {
        //
        // The main() method of the Mule Secure Properties Tool writes the result to stdout (and errors to stderr).
        // This can not be used when invoked in-process like here.
        // Therefore have to invoke the public methods to which main() delegates.
        // These are public and static but not otherwise part of the contract of the tool: this is a brittle solution!
        //

        log.info("Invoking Mule Secure Properties Tool with $method $operation $algorithm $mode $key $value $useRandomIVs")
        try {
            return when (method) {
                Method.STRING -> invokeForString(operation, algorithm, mode, key, value, useRandomIVs)
                Method.FILE -> TODO("File method not supported")
                Method.FILE_LEVEL -> TODO("File-level method not supported")
            }.also {
                log.info("Invoked Mule Secure Properties Tool with result $it")
            }
        } catch (e: Throwable) {
            throw RuntimeException("Failed to invoke Mule Secure Properties Tool: ${e.cause?.message}", e).also {
                log.error(it.message, e)
            }
        }
    }

    private fun invokeForString(
        operation: Operation,
        algorithm: String,
        mode: String,
        key: String,
        value: String,
        useRandomIVs: Boolean
    ) = toolMethodForString().invoke(null, operation.arg, algorithm, mode, key, useRandomIVs, value).toString()

    private fun toolMethodForString() = toolMainClass().getMethod(
        toolMethodNameForString, String::class.java, // String action (= operation)
        String::class.java, // String algorithm
        String::class.java, // String mode
        String::class.java, // String key
        Boolean::class.java, // boolean useRandomIVs
        String::class.java // String value
    )

    private fun toolMainClass(): Class<*> {
        val toolJarFile = javaClass.classLoader.getResource("/$toolJarFileName")
        val classLoader = URLClassLoader.newInstance(arrayOf(URL("jar:${toolJarFile.toExternalForm()}!/")))
        return classLoader.loadClass(toolMainClassName)
    }
}