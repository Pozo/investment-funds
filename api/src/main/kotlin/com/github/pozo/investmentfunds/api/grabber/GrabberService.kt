package com.github.pozo.investmentfunds.api.grabber

import com.github.pozo.investmentfunds.DataFlowConstants
import com.github.pozo.investmentfunds.api.redis.RedisService
import org.springframework.stereotype.Service
import java.text.SimpleDateFormat
import java.util.*

@Service
class GrabberService : GrabberAPI {

    private val format = SimpleDateFormat(DataFlowConstants.GRAB_DATA_COMMAND_DATE_FORMAT.field)

    override fun trigger() {
        val latestEndDate = RedisService.jedis.get(DataFlowConstants.GRAB_DATA_LATEST_DATE_KEY.field)

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