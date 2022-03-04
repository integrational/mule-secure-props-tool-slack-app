package com.mulesoft.training.slack.secprops.rest

import com.mulesoft.training.slack.secprops.config.QuarkusApp
import javax.enterprise.context.ApplicationScoped
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Path("/about")
@Produces(MediaType.APPLICATION_JSON)
@ApplicationScoped
class AboutResource(thisApp: QuarkusApp) {

    data class About(val app: String, val version: String)

    private val about = About(thisApp.name(), thisApp.version())

    @GET
    fun about() = about
}