package com.mulesoft.training.slack.secprops.tool

/**
 * Wraps `com.mulesoft.tools.SecurePropertiesTool` following its
 * [parameters reference](https://docs.mulesoft.com/mule-runtime/latest/secure-configuration-properties#parameter-reference).
 */
interface SecurePropertiesToolFacade {

    enum class Method(val arg: String) {
        STRING("string"), FILE("file"), FILE_LEVEL("file-level")
    }

    enum class Operation(val arg: String) {
        ENCRYPT("encrypt"), DECRYPT("decrypt")
    }

    /**
     * Invoke the Secure Properties Tool.
     * @param algorithm AES, Blowfish, DES, DESede, RC2, RCA, ...
     * @param mode CBC, CFB, ECB, OFB, ...
     * @exception RuntimeException if anything goes wrong
     */
    fun invoke(
        method: Method, operation: Operation,
        algorithm: String, mode: String, key: String, value: String, useRandomIVs: Boolean
    ): String
}