package com.mulesoft.training.slack.secprops

import com.mulesoft.training.slack.secprops.slack.SlackApp
import com.slack.api.bolt.servlet.SlackAppServlet
import com.slack.api.bolt.socket_mode.SocketModeApp
import io.quarkus.runtime.QuarkusApplication
import io.quarkus.runtime.annotations.QuarkusMain
import org.slf4j.LoggerFactory
import javax.servlet.annotation.WebServlet

/**
 * Main entrypoint to Slack app for all types of requests from Slack
 */
@WebServlet("/slack")
class MainServlet(app: SlackApp) : SlackAppServlet(app.app)

/**
 * Main entrypoint to Socket mode Slack app.
 * Comment-out @QuarkusMain to not start in Socket mode.
 */
//@QuarkusMain
class MainApp(private val app: SlackApp) : QuarkusApplication {
    companion object {
        private val log = LoggerFactory.getLogger(MainApp::class.java)
    }

    override fun run(vararg args: String?): Int {
        log.info("Starting Slack app in Socket mode")
        SocketModeApp(app.app).start()
        log.info("Stopped Slack app in Socket mode")

        return 0
    }
}
