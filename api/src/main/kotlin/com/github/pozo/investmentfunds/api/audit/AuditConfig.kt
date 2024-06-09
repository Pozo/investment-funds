package com.github.pozo.investmentfunds.api.audit

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer


/**
 * Configuration class to register the AuditInterceptor.
 */
@Configuration
open class AuditConfig : WebMvcConfigurer {

    @Autowired
    private lateinit var auditInterceptor: AuditInterceptor

    override fun addInterceptors(registry: InterceptorRegistry) {
        // Register the audit interceptor to intercept all incoming requests
        // registry.addInterceptor(auditInterceptor)
    }
}
