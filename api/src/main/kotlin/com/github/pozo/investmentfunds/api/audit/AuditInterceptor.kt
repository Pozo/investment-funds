package com.github.pozo.investmentfunds.api.audit

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import java.io.IOException
import java.util.stream.Collectors


/**
 * An interceptor that logs the request URL, method, parameters, and body.
 */
@Component
class AuditInterceptor : HandlerInterceptor {

    private val logger = LoggerFactory.getLogger(AuditInterceptor::class.java)

    @Throws(IOException::class)
    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        // Wrap the request to cache the body for multiple reads
        val cachedRequest = CachedBodyHttpServletRequest(request)

        // Extract and log request details
        val requestURI = cachedRequest.requestURI
        val method = cachedRequest.method
        val params = getParameters(cachedRequest)
        val body = getBody(cachedRequest)

        logger.info("Request URL: $requestURI, Method: $method, Parameters: $params, Body: $body")

        return true
    }

    /**
     * Retrieves all request parameters and formats them as a string.
     */
    private fun getParameters(request: HttpServletRequest): String {
        return request.parameterMap.entries.joinToString("&") { "${it.key}=${it.value.joinToString(",")}" }
    }

    /**
     * Reads the request body and converts it to a string.
     */
    @Throws(IOException::class)
    private fun getBody(request: HttpServletRequest): String {
        return request.reader.lines().collect(Collectors.joining(System.lineSeparator()))
    }
}