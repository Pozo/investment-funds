package com.github.pozo.investmentfunds.api.grabber

import io.swagger.v3.oas.annotations.Hidden
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@Hidden
class GrabberController @Autowired constructor(private val grabberAPI: GrabberAPI) {

    private val logger = LoggerFactory.getLogger(GrabberController::class.java)

    @PostMapping("/grabber/trigger")
    fun funds() {
        logger.info("POST /grabber/trigger")
        return grabberAPI.trigger()
    }

}