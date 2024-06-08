package com.github.pozo.investmentfunds.api.error

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.boot.web.servlet.error.ErrorController
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus

@Controller
class ApiErrorController : ErrorController {

    @RequestMapping("/error")
    @ResponseStatus(HttpStatus.OK)
    fun handleError(request: HttpServletRequest, response: HttpServletResponse) {
        // Set the response to be empty with status OK
        response.writer.write("")
        response.writer.flush()
    }

}