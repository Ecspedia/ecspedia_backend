package com.grupo3

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class Grupo3Application

fun main(args: Array<String>) {
    runApplication<Grupo3Application>(*args)

}
