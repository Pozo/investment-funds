package com.github.pozo.investmentfunds.api.audit

import jakarta.servlet.ReadListener
import jakarta.servlet.ServletInputStream
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletRequestWrapper
import java.io.*

/**
 * A custom HttpServletRequestWrapper that caches the request body so it can be read multiple times.
 */
class CachedBodyHttpServletRequest(request: HttpServletRequest) : HttpServletRequestWrapper(request) {

    private val cachedBody: ByteArray

    init {
        // Read the request body and cache it as a byte array
        val requestInputStream = request.inputStream
        cachedBody = requestInputStream.readBytes()
    }

    override fun getInputStream(): ServletInputStream {
        val byteArrayInputStream = ByteArrayInputStream(cachedBody)
        return CachedBodyServletInputStream(byteArrayInputStream)
    }

    override fun getReader(): BufferedReader {
        return BufferedReader(InputStreamReader(this.inputStream))
    }

    /**
     * A custom ServletInputStream that reads from the cached body.
     */
    private class CachedBodyServletInputStream(private val inputStream: InputStream) : ServletInputStream() {
        override fun isFinished(): Boolean {
            return try {
                inputStream.available() == 0
            } catch (e: IOException) {
                e.printStackTrace()
                false
            }
        }

        override fun isReady(): Boolean {
            return true
        }

        override fun setReadListener(readListener: ReadListener) {
            throw UnsupportedOperationException()
        }

        @Throws(IOException::class)
        override fun read(): Int {
            return inputStream.read()
        }
    }
}
