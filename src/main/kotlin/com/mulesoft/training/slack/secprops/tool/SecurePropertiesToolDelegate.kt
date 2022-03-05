package com.mulesoft.training.slack.secprops.tool

import com.mulesoft.training.slack.secprops.tool.SecurePropertiesToolFacade.Method
import com.mulesoft.training.slack.secprops.tool.SecurePropertiesToolFacade.Operation
import org.slf4j.LoggerFactory
import java.net.URL
import java.net.URLClassLoader
import javax.enterprise.context.ApplicationScoped

/**
 * Dynamically loads the Mule Secure Properties Tool jar file from its authoritative URL
 * and invokes it in-process (in this JVM) in order to satisfy the [SecurePropertiesToolFacade] interface.
 */
@ApplicationScoped
class SecurePropertiesToolDelegate : SecurePropertiesToolFacade {
    companion object {
        /** THe Mule Runtime version that determines the URL of the Mule Secure Properties Tool */
        private const val muleVersion = "4.2"

        /** The authoritative URL of the Mule Secure Properties Tool jar file */
        private val toolJarFileUrl =
            URL("https://docs.mulesoft.com/downloads/mule-runtime/$muleVersion/secure-properties-tool.jar")

        /** Fully qualified class name of the Main Java class of the Mule Secure Properties Tool */
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

        /** Load the Main class of the Mule Secure Properties Tool jar file from its JAR file at its authoritative URL */
        private fun loadToolMainClass() =
            URLClassLoader.newInstance(arrayOf(URL("jar:${toolJarFileUrl.toExternalForm()}!/")))
                .loadClass(toolMainClassName)
    }

    private val toolMainClass by lazy { loadToolMainClass() } // lazy also implies re-try upon exception
    private val toolMethodForString by lazy {
        toolMainClass.getMethod(
            toolMethodNameForString,
            String::class.java, // String action (= operation)
            String::class.java, // String algorithm
            String::class.java, // String mode
            String::class.java, // String key
            Boolean::class.java, // boolean useRandomIVs
            String::class.java // String value
        )
    }

    override fun invoke(
        method: Method,
        operation: Operation,
        algorithm: String,
        mode: String,
        key: String,
        value: String,
        useRandomIVs: Boolean
    ): String = synchronized(this) { // defensive synchronized in case tool is not thread safe
        //
        // The main() method of the Mule Secure Properties Tool writes the result to stdout (and errors to stderr).
        // This can not be used when invoked in-process like here.
        // Therefor have to invoke the public methods to which main() delegates.
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
        operation: Operation, algorithm: String, mode: String, key: String, value: String, useRandomIVs: Boolean
    ) = toolMethodForString.invoke(null, operation.arg, algorithm, mode, key, useRandomIVs, value).toString()
}