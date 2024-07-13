package com.github.pozo.investmentfunds.api.grabber

import org.apache.camel.ProducerTemplate
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service


@Service
class GrabberService constructor(
    @Autowired val producerTemplate: ProducerTemplate
) : GrabberAPI {

    private val logger = LoggerFactory.getLogger(GrabberService::class.java)

    @Scheduled(cron = "0 0 */6 * * *")
    override fun trigger() {
        logger.info("Initiating CSV retrieval mechanism.")
        producerTemplate.sendBody("direct:grab-data", null)
    }

}

