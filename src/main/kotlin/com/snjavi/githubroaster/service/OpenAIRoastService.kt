package com.snjavi.githubroaster.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.core.publisher.Mono

@Service
class OpenAIRoastService(@Value("\${openai.api.key}") private val apiKey: String) {

    private val webClient: WebClient

    init {
        val keyPreview = if (apiKey.isNotBlank()) "${apiKey.take(7)}..." else "NOT SET"
        println("OpenAI API Key initialized: $keyPreview")

        webClient = WebClient.builder()
            .baseUrl("https://api.openai.com/v1")
            .defaultHeader("Authorization", "Bearer $apiKey")
            .defaultHeader("Content-Type", "application/json")
            .build()
    }

    fun generateRoast(insights: RoastAnalyzerService.RoastInsights): String {
        println("=== Starting roast generation for: ${insights.profileInsights.username} ===")

        if (apiKey.isBlank() || apiKey == "\${openai.api.key}") {
            println("WARN: OpenAI API key not configured, using fallback roast")
            return generateFallbackRoast(insights)
        }

        val prompt = buildPrompt(insights)
        println("Prompt built successfully, length: ${prompt.length} characters")

        val request = mapOf(
            "model" to "gpt-4o-mini",
            "messages" to listOf(
                mapOf(
                    "role" to "system",
                    "content" to """You are a witty and sarcastic GitHub roast generator. 
                        Your job is to create hilarious, clever roasts based on a developer's GitHub activity.
                        Be funny but not mean-spirited. Use programming humor and developer culture references.
                        Keep the roast SHORT and PUNCHY - aim for 2-3 sentences maximum.
                        Make it personal and specific based on the data provided.
                        Get straight to the point with the best burn.""".trimIndent()
                ),
                mapOf(
                    "role" to "user",
                    "content" to prompt
                )
            ),
            "temperature" to 0.8,
            "max_tokens" to 150
        )

        return try {
            println("Sending request to OpenAI...")

            val responseMono = webClient.post()
                .uri("/chat/completions")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Map::class.java)

            val response = responseMono.block() as? Map<String, Any>
            println("Received response from OpenAI")

            val roast = extractRoastFromResponse(response)
            println("=== Roast generated successfully ===")
            roast

        } catch (e: WebClientResponseException) {
            val errorMsg = """
                OpenAI API Error:
                Status: ${e.statusCode}
                Response: ${e.responseBodyAsString}
            """.trimIndent()
            println("ERROR: $errorMsg")

            println("Using fallback roast due to API error")
            generateFallbackRoast(insights)

        } catch (e: Exception) {
            println("ERROR calling OpenAI API: ${e.message}")
            e.printStackTrace()

            println("Using fallback roast due to exception")
            generateFallbackRoast(insights)
        }
    }

    private fun extractRoastFromResponse(response: Map<String, Any>?): String {
        return try {
            if (response == null) {
                return "Failed to generate roast: Empty response from OpenAI"
            }

            @Suppress("UNCHECKED_CAST")
            val choices = response["choices"] as? List<Map<String, Any>>
            if (choices.isNullOrEmpty()) {
                return "Failed to generate roast: No choices in response"
            }

            val firstChoice = choices.firstOrNull()

            @Suppress("UNCHECKED_CAST")
            val message = firstChoice?.get("message") as? Map<String, Any>
            val content = message?.get("content") as? String

            content ?: "Failed to generate roast: No content in response"

        } catch (e: Exception) {
            println("ERROR extracting roast from response: ${e.message}")
            e.printStackTrace()
            "Failed to parse roast response: ${e.message}"
        }
    }

    private fun generateFallbackRoast(insights: RoastAnalyzerService.RoastInsights): String {
        val profile = insights.profileInsights
        val repos = insights.repoInsights
        val commits = insights.commitInsights

        val roastLines = mutableListOf<String>()

        val allRoasts = mutableListOf<String>()
        allRoasts.addAll(profile.roastablePoints)
        allRoasts.addAll(repos.roastablePoints)
        allRoasts.addAll(commits.roastablePoints)

        val selectedRoasts = allRoasts.shuffled().take(2)

        if (selectedRoasts.isNotEmpty()) {
            roastLines.add("@${profile.username}: ${selectedRoasts.joinToString(" ")} 🔥")
        } else {
            roastLines.add("@${profile.username}, your GitHub is so unremarkable, even the roast generator gave up. 🔥")
        }

        return roastLines.joinToString(" ")
    }

    private fun buildPrompt(insights: RoastAnalyzerService.RoastInsights): String {
        val profile = insights.profileInsights
        val repos = insights.repoInsights
        val commits = insights.commitInsights
        val activity = insights.activityInsights

        return buildString {
            appendLine("Generate a SHORT, PUNCHY roast (2-3 sentences max) for GitHub user @${profile.username}.")
            appendLine("Focus on the BEST burns from this data:")
            appendLine()

            if (profile.roastablePoints.isNotEmpty()) {
                appendLine("Profile issues: ${profile.roastablePoints.take(2).joinToString("; ")}")
            }

            if (repos.roastablePoints.isNotEmpty()) {
                appendLine("Repo issues: ${repos.roastablePoints.take(2).joinToString("; ")}")
            }

            if (commits.roastablePoints.isNotEmpty()) {
                appendLine("Commit issues: ${commits.roastablePoints.take(2).joinToString("; ")}")
            }

            appendLine()
            appendLine("Key stats: ${repos.totalRepos} repos, ${commits.totalCommits} commits, ${repos.languages.entries.joinToString { it.key }}")
            appendLine()
            appendLine("Make it SHORT, WITTY, and BRUTAL. 2-3 sentences MAXIMUM!")
        }
    }
}