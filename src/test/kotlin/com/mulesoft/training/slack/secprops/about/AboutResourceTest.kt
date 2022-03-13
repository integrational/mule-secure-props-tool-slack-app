package com.mulesoft.training.slack.secprops.about

import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured.given
import org.hamcrest.CoreMatchers.equalTo
import org.junit.jupiter.api.Test

@QuarkusTest
class AboutResourceTest {

    @Test
    fun testAbout() = given()
        .`when`().get("/about")
        .then()
        .statusCode(200)
        .body("app", equalTo("test-app")) // must match test/application.properties
        .body("version", equalTo("0.0.0")) // must match test/application.properties

}