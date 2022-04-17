package com.mulesoft.training.slack.secprops.slack.service

import com.mulesoft.training.slack.secprops.persistence.OAuthState
import com.mulesoft.training.slack.secprops.persistence.OAuthStateRepository
import com.slack.api.bolt.service.OAuthStateService
import org.slf4j.LoggerFactory
import javax.enterprise.context.ApplicationScoped

/**
 * Implementation of [OAuthStateService] that delegates to [OAuthStateRepository].
 */
@ApplicationScoped
class OAuthStateServiceAdapter(private val repo: OAuthStateRepository) : OAuthStateService {
    companion object {
        private val log = LoggerFactory.getLogger(OAuthStateServiceAdapter::class.java)
    }

    override fun addNewStateToDatastore(state: String?) {
        log.debug("addNewStateToDatastore() called with $state")
        repo.persistOrUpdate(OAuthState(state))
        log.debug("addNewStateToDatastore() returned")
    }

    override fun isAvailableInDatabase(state: String?): Boolean {
        log.debug("isAvailableInDatabase() called with $state")
        val found = if (state == null) false else repo.findById(state) != null
        return found.also {
            log.debug("isAvailableInDatabase() returned $it")
        }
    }

    override fun deleteStateFromDatastore(state: String?) {
        log.debug("deleteStateFromDatastore() called with $state")
        if (state != null) repo.deleteById(state)
        log.debug("deleteStateFromDatastore() returned")
    }
}