package com.mulesoft.training.slack.secprops.rest

import com.mulesoft.training.slack.secprops.config.QuarkusApp
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Path("/about")
@Produces(MediaType.APPLICATION_JSON)
class AboutResource(val quarkusApp: QuarkusApp) {

    @GET
    fun about() = About(quarkusApp.name(), quarkusApp.version())

    data class About(val app: String, val version: String)
}