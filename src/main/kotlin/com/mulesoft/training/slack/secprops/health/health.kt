package com.mulesoft.training.slack.secprops.health

import org.eclipse.microprofile.health.*
import javax.enterprise.context.ApplicationScoped

@Startup
@ApplicationScoped
class DefaultStartupHealthCheck : HealthCheck {
    override fun call() = HealthCheckResponse.up("started")
}

@Liveness
@ApplicationScoped
class DefaultLivenessHealthCheck : HealthCheck {
    override fun call() = HealthCheckResponse.up("live")
}

@Readiness
@ApplicationScoped
class DefaultReadinessHealthCheck : HealthCheck {
    override fun call() = HealthCheckResponse.up("ready")
}
