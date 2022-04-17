package com.mulesoft.training.slack.secprops.persistence

import io.quarkus.mongodb.panache.kotlin.PanacheMongoRepositoryBase
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class OAuthStateRepository : PanacheMongoRepositoryBase<OAuthState, String> {}