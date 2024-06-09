package com.github.pozo.investmentfunds.api.audit

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import java.io.IOException


/**
 * An interceptor that logs the request URL, method, parameters, and body.
 */
@Component
class AuditInterceptor : HandlerInterceptor {

    private val logger = LoggerFactory.getLogger(AuditInterceptor::class.java)

    @Throws(IOException::class)
    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        logger.info("Request URL: ${request.requestURI}, Method: ${request.method}, Parameters: ${getParameters(request)}")

        return true
    }

    /**
     * Retrieves all request parameters and formats them as a string.
     */
    private fun getParameters(request: HttpServletRequest): String {
        return request.parameterMap.entries.joinToString("&") { "${it.key}=${it.value.joinToString(",")}" }
    }

}