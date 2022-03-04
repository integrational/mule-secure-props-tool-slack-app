package com.mulesoft.training.slack.secprops.tool

/**
 * Wraps `com.mulesoft.tools.SecurePropertiesTool` following its
 * [parameters reference](https://docs.mulesoft.com/mule-runtime/latest/secure-configuration-properties#parameter-reference).
 */
abstract class SecurePropertiesToolFacade {
    enum class Method(val arg: String) {
        STRING("string"), FILE("file"), FILE_LEVEL("file-level")
    }

    enum class Operation(val arg: String) {
        ENCRYPT("encrypt"), DECRYPT("decrypt")
    }

    val USE_RANDOM_IVS_ARG = "--use-random-iv"

    /**
     * Invoke the Secure Properties Tool.
     * @param algorithm AES, Blowfish, DES, DESede, RC2, RCA, ...
     * @param mode CBC, CFB, ECB, OFB, ...
     */
    abstract fun invoke(
        method: Method, operation: Operation,
        algorithm: String, mode: String, key: String, value: String, useRandomIVs: Boolean
    ): String

    protected fun useRandomIVsArgFromBoolean(useRandomIVs: Boolean) = if (useRandomIVs) USE_RANDOM_IVS_ARG else ""
}