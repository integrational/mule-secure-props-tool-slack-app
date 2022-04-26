package com.mulesoft.training.slack.secprops.slack.service

import com.mulesoft.training.slack.secprops.persistence.BotInstallation
import com.mulesoft.training.slack.secprops.persistence.BotInstallationRepository
import com.mulesoft.training.slack.secprops.persistence.Installation
import com.mulesoft.training.slack.secprops.persistence.InstallationRepository
import com.slack.api.bolt.model.Bot
import com.slack.api.bolt.model.Installer
import com.slack.api.bolt.service.InstallationService
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.slf4j.LoggerFactory
import javax.enterprise.context.ApplicationScoped

/**
 * Implementation of [InstallationService] that delegates to [InstallationRepository] and [BotInstallationRepository].
 */
@ApplicationScoped
class InstallationServiceRepoAdapter(
    @field:ConfigProperty(name = "appDbId")
    private val appDbId: String,
    private val instRepo: InstallationRepository,
    private val botRepo: BotInstallationRepository
) : InstallationService {

    companion object {
        private val log = LoggerFactory.getLogger(InstallationServiceRepoAdapter::class.java)
    }

    /** Noop: always returns `false` */
    override fun isHistoricalDataEnabled(): Boolean {
        log.debug("isHistoricalDataEnabled() called")
        return false.also {
            log.debug("isHistoricalDataEnabled() returned $it")
        }
    }

    /** Noop: never enables historical data */
    override fun setHistoricalDataEnabled(enable: Boolean) {
        log.debug("setHistoricalDataEnabled() called with $enable")
        if (enable) log.warn("Not enabling historical data")
        log.debug("setHistoricalDataEnabled() returned")
    }

    override fun saveInstallerAndBot(inst: Installer?) {
        log.debug("saveInstallerAndBot() called with $inst")
        if (inst != null) {
            instRepo.persistOrUpdate(Installation(appDbId, inst))
            val bot = inst.toBot()
            if (bot != null) botRepo.persistOrUpdate(BotInstallation(appDbId, bot))
        }
        log.debug("saveInstallerAndBot() returned")
    }

    override fun deleteBot(bot: Bot?) {
        log.debug("deleteBot() called with $bot")
        if (bot != null) botRepo.deleteById(BotInstallation.Companion.Key(appDbId, bot))
        log.debug("deleteBot() returned")
    }

    override fun deleteInstaller(inst: Installer?) {
        log.debug("deleteInstaller() called with $inst")
        if (inst != null) instRepo.deleteById(Installation.Companion.Key(appDbId, inst))
        log.debug("deleteInstaller() returned")
    }

    override fun findBot(enterpriseId: String?, teamId: String?): Bot? {
        log.debug("findBot() called with $enterpriseId and $teamId")
        // try finding enterprise-level bot
        val enterpriseBot = if (enterpriseId != null) {
            botRepo.findById(BotInstallation.Companion.Key(enterpriseId, null))?.bot
        } else null
        // try finding workspace-level bot if no enterprise-level bot found
        val wsBot = if (enterpriseBot == null) {
            botRepo.findById(BotInstallation.Companion.Key(enterpriseId, teamId))?.bot
        } else null
        return when {
            enterpriseBot != null -> enterpriseBot.also { log.debug("Found enterprise-level bot") }
            wsBot != null -> wsBot.also { log.debug("Found workspace-level bot") }
            else -> null.also { log.info("No bot found") }
        }.also {
            log.debug("findBot() returned $it")
        }
    }

    override fun findInstaller(enterpriseId: String?, teamId: String?, userId: String?): Installer? {
        log.debug("findInstaller() called with $enterpriseId and $teamId and $userId")
        // try finding enterprise-level installation
        val enterpriseInst = if (enterpriseId != null) {
            instRepo.findById(Installation.Companion.Key(enterpriseId, null, userId))?.installer
        } else null
        // try finding workspace-level installation if no enterprise-level installation found
        val wsInst = if (enterpriseInst == null) {
            instRepo.findById(Installation.Companion.Key(enterpriseId, teamId, userId))?.installer
        } else null
        return when {
            enterpriseInst != null -> enterpriseInst.also { log.debug("Found enterprise-level installation") }
            wsInst != null -> wsInst.also { log.debug("Found workspace-level installation") }
            else -> null.also { log.info("No installation found") }
        }.also {
            log.debug("findInstaller() returned $it")
        }
    }
}