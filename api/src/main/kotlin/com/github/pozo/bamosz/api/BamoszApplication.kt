package com.github.pozo.bamosz.api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
open class BamoszApplication

fun main(args: Array<String>) {
	runApplication<BamoszApplication>(*args)
}
