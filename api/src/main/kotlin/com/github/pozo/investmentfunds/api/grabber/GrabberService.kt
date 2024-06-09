package com.github.pozo.investmentfunds.api.grabber

import com.github.pozo.investmentfunds.DataFlowConstants
import com.github.pozo.investmentfunds.api.audit.AuditInterceptor
import com.github.pozo.investmentfunds.api.redis.RedisService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.text.SimpleDateFormat
import java.util.*

@Service
class GrabberService : GrabberAPI {

    private val logger = LoggerFactory.getLogger(AuditInterceptor::class.java)

    private val format = SimpleDateFormat(DataFlowConstants.GRAB_DATA_COMMAND_DATE_FORMAT.field)

    override fun trigger() {
        val latestEndDate = RedisService.jedis.get(DataFlowConstants.GRAB_DATA_LATEST_DATE_KEY.field)

        logger.info("Initiating CSV retrieval mechanism via Redis. The last trigger date was $latestEndDate.")

        if (latestEndDate == null || latestEndDate.isEmpty()) {
            RedisService.jedis.publish(
                DataFlowConstants.GRAB_DATA_COMMAND_CHANNEL_NAME.field,
                "2024.05.01${DataFlowConstants.GRAB_DATA_COMMAND_SEPARATOR.field}${format.format(Date())}"
            )
        } else {
            RedisService.jedis.publish(
                DataFlowConstants.GRAB_DATA_COMMAND_CHANNEL_NAME.field,
                "$latestEndDate,${format.format(Date())}"
            )
        }
    }
}