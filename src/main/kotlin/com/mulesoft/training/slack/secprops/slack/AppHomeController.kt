package com.mulesoft.training.slack.secprops.slack

import com.slack.api.app_backend.events.payload.EventsApiPayload
import com.slack.api.bolt.context.builtin.EventContext
import com.slack.api.bolt.response.Response
import com.slack.api.model.event.AppHomeOpenedEvent
import org.slf4j.LoggerFactory
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class AppHomeController(private val views: Views) {
    private val log = LoggerFactory.getLogger(this::class.java)

    /**
     * In response to an [AppHomeOpenedEvent], publish the App Home view.
     */
    fun publishAppHome(payload: EventsApiPayload<AppHomeOpenedEvent>, ctx: EventContext): Response {
        log.info("Handling event ${payload.event.type}")
        ctx.asyncClient().viewsPublish {
            it.userId(payload.event.user).viewAsString(views.appHome())
        }
        return ctx.ack() // always ack, no matter the success of publishing the view
    }
}