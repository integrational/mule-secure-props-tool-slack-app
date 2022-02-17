package com.mulesoft.training.slack.secprops

import com.slack.api.bolt.servlet.SlackAppServlet
import javax.servlet.annotation.WebServlet

@WebServlet("/slack/events")
class Main() : SlackAppServlet(app())
