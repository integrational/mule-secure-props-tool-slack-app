package com.mulesoft.training.slack.secprops.persistence

import io.quarkus.mongodb.panache.kotlin.PanacheMongoRepositoryBase
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class UserSettingsRepository : PanacheMongoRepositoryBase<UserSettings, String> {}