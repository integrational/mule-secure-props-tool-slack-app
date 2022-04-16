package com.mulesoft.training.slack.secprops.slack.service

import com.slack.api.bolt.model.Bot
import com.slack.api.bolt.model.Installer
import com.slack.api.bolt.service.InstallationService
import org.slf4j.LoggerFactory

class InstallationServiceDelegate(private val delegate: InstallationService) : InstallationService {

    companion object {
        private val log = LoggerFactory.getLogger(InstallationServiceDelegate::class.java)
    }

    override fun isHistoricalDataEnabled(): Boolean {
        log.debug("isHistoricalDataEnabled() called")
        return delegate.isHistoricalDataEnabled().also {
            log.debug("isHistoricalDataEnabled() returned $it")
        }
    }

    override fun setHistoricalDataEnabled(isHistoricalDataEnabled: Boolean) {
        log.debug("setHistoricalDataEnabled() called with $isHistoricalDataEnabled")
        return delegate.setHistoricalDataEnabled(isHistoricalDataEnabled).also {
            log.debug("setHistoricalDataEnabled() returned")
        }
    }

    override fun saveInstallerAndBot(installer: Installer?) {
        log.debug("saveInstallerAndBot() called with $installer")
        return delegate.saveInstallerAndBot(installer).also {
            log.debug("saveInstallerAndBot() returned")
        }
    }

    override fun deleteBot(bot: Bot?) {
        log.debug("deleteBot() called with $bot")
        return delegate.deleteBot(bot).also {
            log.debug("deleteBot() returned")
        }
    }

    override fun deleteInstaller(installer: Installer?) {
        log.debug("deleteInstaller() called with $installer")
        return delegate.deleteInstaller(installer).also {
            log.debug("deleteInstaller() returned")
        }
    }

    override fun findBot(enterpriseId: String?, teamId: String?): Bot {
        log.debug("findBot() called with $enterpriseId and $teamId")
        return delegate.findBot(enterpriseId, teamId).also {
            log.debug("findBot() returned $it")
        }
    }

    override fun findInstaller(enterpriseId: String?, teamId: String?, userId: String?): Installer {
        log.debug("findInstaller() called with $enterpriseId and $teamId and $userId")
        return delegate.findInstaller(enterpriseId, teamId, userId).also {
            log.debug("findInstaller() returned $it")
        }
    }
}