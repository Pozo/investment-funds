package com.github.pozo.investmentfunds.api.grabber

import com.github.pozo.investmentfunds.api.redis.RedisService
import com.github.pozo.investmentfunds.domain.DataFlowConstants
import org.apache.camel.ProducerTemplate
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import redis.clients.jedis.resps.Tuple
import java.text.SimpleDateFormat
import java.util.*


@Service
class GrabberService constructor(
    @Autowired val producerTemplate: ProducerTemplate
) : GrabberAPI {

    private val logger = LoggerFactory.getLogger(GrabberService::class.java)

    private val format = SimpleDateFormat(DataFlowConstants.GRAB_DATA_COMMAND_DATE_FORMAT.field)

    private val redisEntryFormat = SimpleDateFormat(DataFlowConstants.RATES_KEY_DATE_FORMAT.field)

    @Scheduled(cron = "0 0 * * * *")
    override fun trigger() {
        val lastSuccessfulGrabbing = RedisService.jedis.get(DataFlowConstants.GRAB_DATA_LATEST_DATE_KEY.field)

        logger.info("Initiating CSV retrieval mechanism. The last trigger date was $lastSuccessfulGrabbing.")

        if (lastSuccessfulGrabbing == null || lastSuccessfulGrabbing.isEmpty()) {
            logger.info("Initiating full retrieval. Retrieving data from '${DataFlowConstants.START_YEAR_DATE.field}'")
            initialDataRetriaval(DataFlowConstants.START_YEAR_DATE.field)
        } else {
            val latestEntry = getMostRecentEntry()
            if (latestEntry == null) {
                // The redis is empty
                logger.info("Initiating full retrieval. The redis store contains invalid data.")
                initialDataRetriaval(DataFlowConstants.START_YEAR_DATE.field)
            } else {
                val latestEntryDate = redisEntryFormat.parse(extractDate(latestEntry.element))
                val lastSuccessfulGrabbingDate = format.parse(lastSuccessfulGrabbing)

                if (lastSuccessfulGrabbingDate.before(latestEntryDate)) {
                    // The data is invalid
                    // The last grabbing mechanism failed at some point
                    logger.info("Initiating partially retrieval. The redis store contains invalid data, the last grabbing failed at some point.")
                    initialDataRetriaval(lastSuccessfulGrabbing)
                } else {
                    // Normal behaviour
                    // It's still possible that the CSV files are empty, because we don't have new data every day
                    logger.info("Initiating data retrieval.")
                    initialDataRetriaval(format.format(latestEntryDate))
                }
            }
        }
    }

    private fun initialDataRetriaval(startDate: String?) {
        logger.info("Retrieving data from '$startDate'")
        producerTemplate.sendBody(
            "direct:grab-data",
            "$startDate${DataFlowConstants.GRAB_DATA_COMMAND_SEPARATOR.field}${format.format(Date())}"
        )
    }

    private fun getMostRecentEntry(): Tuple? {
        return RedisService.jedis.pipelined().use { pipeline ->
            val results = RedisService.jedis.keys("rate:keys#*")
                .map { RedisService.jedis.zrevrangeByScoreWithScores(it, "+inf", "-inf", 0, 1) }
                .mapNotNull { it.firstOrNull() }
                .maxByOrNull { it.score }
            pipeline.sync()
            return@use results
        }
    }

    private fun extractDate(input: String): String? {
        return input.split("#").takeIf { it.size > 1 }?.get(1)
    }
}

