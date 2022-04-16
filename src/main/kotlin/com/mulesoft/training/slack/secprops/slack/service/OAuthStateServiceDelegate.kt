package com.mulesoft.training.slack.secprops.slack.service

import com.slack.api.bolt.service.OAuthStateService
import org.slf4j.LoggerFactory

class OAuthStateServiceDelegate(private val delegate: OAuthStateService) : OAuthStateService {
    companion object {
        private val log = LoggerFactory.getLogger(OAuthStateServiceDelegate::class.java)
    }

    override fun addNewStateToDatastore(state: String?) {
        log.debug("addNewStateToDatastore() called with $state")
        return delegate.addNewStateToDatastore(state).also {
            log.debug("addNewStateToDatastore() returned")
        }
    }

    override fun isAvailableInDatabase(state: String?): Boolean {
        log.debug("isAvailableInDatabase() called with $state")
        return delegate.isAvailableInDatabase(state).also {
            log.debug("isAvailableInDatabase() returned $it")
        }
    }

    override fun deleteStateFromDatastore(state: String?) {
        log.debug("deleteStateFromDatastore() called with $state")
        return delegate.deleteStateFromDatastore(state).also {
            log.debug("deleteStateFromDatastore() returned")
        }
    }
}