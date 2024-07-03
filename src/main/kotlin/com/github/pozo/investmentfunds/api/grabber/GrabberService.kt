package com.github.pozo.investmentfunds.api.grabber

import com.github.pozo.investmentfunds.api.redis.RedisService
import com.github.pozo.investmentfunds.domain.DataFlowConstants
import org.apache.camel.ProducerTemplate
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.text.SimpleDateFormat
import java.util.*


@Service
class GrabberService constructor(
    @Autowired val producerTemplate: ProducerTemplate
) : GrabberAPI {

    private val logger = LoggerFactory.getLogger(GrabberService::class.java)

    private val format = SimpleDateFormat(DataFlowConstants.GRAB_DATA_COMMAND_DATE_FORMAT.field)

    override fun trigger() {
        val latestEndDate = RedisService.jedis.get(DataFlowConstants.GRAB_DATA_LATEST_DATE_KEY.field)

        logger.info("Initiating CSV retrieval mechanism. The last trigger date was $latestEndDate.")

        if (latestEndDate == null || latestEndDate.isEmpty()) {
            producerTemplate.sendBody(
                "direct:grab-data",
                "2024.06.01${DataFlowConstants.GRAB_DATA_COMMAND_SEPARATOR.field}${format.format(Date())}"
            )
        } else {
            producerTemplate.sendBody(
                "direct:grab-data",
                "$latestEndDate,${format.format(Date())}"
            )
        }
    }
}