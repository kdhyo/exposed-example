package com.example.exposed.exposedexample

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ExposedExampleApplication

fun main(args: Array<String>) {
    runApplication<ExposedExampleApplication>(*args)
}
