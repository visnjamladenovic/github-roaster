package com.snjavi.githubroaster.controller

import io.github.cdimascio.dotenv.Dotenv
import io.github.cdimascio.dotenv.DotenvException
import jakarta.annotation.PostConstruct
import org.springframework.context.annotation.Configuration

@Configuration
class DotenvConfig {

    @PostConstruct
    fun loadEnv() {
        try {
            val dotenv = Dotenv.configure()
                .ignoreIfMissing()
                .load()

            dotenv.entries().forEach { entry ->
                System.setProperty(entry.key, entry.value)
                println("✅ Loaded env variable: ${entry.key}")
            }

            println(".env file loaded successfully!")

        } catch (e: DotenvException) {
            println("No .env file found. Using system environment variables.")
        } catch (e: Exception) {
            println("Error loading .env file: ${e.message}")
        }
    }
}