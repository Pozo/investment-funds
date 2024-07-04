package com.github.pozo.investmentfunds.api.grabber

import com.github.pozo.investmentfunds.api.redis.RedisService
import com.github.pozo.investmentfunds.domain.DataFlowConstants
import org.apache.camel.ProducerTemplate
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


@Service
class GrabberService constructor(
    @Autowired val producerTemplate: ProducerTemplate
) : GrabberAPI {

    private val logger = LoggerFactory.getLogger(GrabberService::class.java)

    private val format = SimpleDateFormat(DataFlowConstants.GRAB_DATA_COMMAND_DATE_FORMAT.field)

    @Scheduled(cron = "0 * * * * *")
    override fun trigger() {
        val latestEndDate = RedisService.jedis.get(DataFlowConstants.GRAB_DATA_LATEST_DATE_KEY.field)

        logger.info("Initiating CSV retrieval mechanism. The last trigger date was $latestEndDate.")

        if (latestEndDate == null || latestEndDate.isEmpty()) {
            logger.info("Retrieving data from '${DataFlowConstants.START_YEAR_DATE.field}'")
            producerTemplate.sendBody(
                "direct:grab-data",
                "${DataFlowConstants.START_YEAR_DATE.field}${DataFlowConstants.GRAB_DATA_COMMAND_SEPARATOR.field}${format.format(Date())}"
            )
        } else {
            logger.info("Retrieving data from '$latestEndDate'")
            producerTemplate.sendBody(
                "direct:grab-data",
                "$latestEndDate,${format.format(Date())}"
            )
        }
    }
}