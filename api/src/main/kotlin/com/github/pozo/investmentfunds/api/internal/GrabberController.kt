package com.github.pozo.investmentfunds.api.internal

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class GrabberController @Autowired constructor(private val grabberAPI: GrabberAPI) {

    @PostMapping("/grabber/trigger")
    fun funds() {
        return grabberAPI.trigger()
    }

}