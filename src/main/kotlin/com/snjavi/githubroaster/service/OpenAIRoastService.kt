package com.snjavi.githubroaster.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono


@Service
class OpenAIRoastService(@Value("\${openai.api.key") private val apiKey: String) {
    private val webClient = WebClient.builder()
        .baseUrl("https://api.openai.com/v1")
        .defaultHeader("Authorization", "Bearer $apiKey")
        .defaultHeader("Content-Type", "application/json")
        .build()

    fun generateRoast(insights: RoastAnalyzerService.RoastInsights): String {
        val prompt = buildPrompt(insights)

        val request = mapOf(
            "model" to "gpt-4o-mini",
            "messages" to listOf(
                mapOf(
                    "role" to "system",
                    "content" to """You are a witty and sarcastic GitHub roast generator. 
                        Your job is to create hilarious, clever roasts based on a developer's GitHub activity.
                        Be funny but not mean-spirited. Use programming humor and developer culture references.
                        Keep the roast concise - aim for 3-5 paragraphs maximum.
                        Make it personal and specific based on the data provided.""".trimIndent()
                ),

                mapOf(
                    "role" to "user",
                    "content" to prompt
                )
            ),
            "temperature" to 0.8,
            "max_tokens" to 500
        )

        val response = webClient.post()
            .uri("/chat/completions")
            .bodyValue(request)
            .retrieve()
            .bodyToMono(String::class.java)
            .block()

        return try {
            val responseMono: Mono<Map<*, *>> = webClient.post()
                .uri("/chat/completions")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Map::class.java)

            val response: Map<*, *>? = responseMono.block()

            @Suppress("UNCHECKED_CAST")
            val typedResponse = response as? Map<String, Any>

            extractRoastFromResponse(typedResponse)
        } catch (e: Exception) {
            "Failed to call OpenAI API: ${e.message}"
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun extractRoastFromResponse(response: Map<String, Any>?): String {
        return try {
            val choices = response?.get("choices") as? List<Map<String, Any>>
            val firstChoice = choices?.firstOrNull()
            val message = firstChoice?.get("message") as? Map<String, Any>
            message?.get("content") as? String ?: "Failed to generate roast"
        } catch (e: Exception) {
            "Failed to generate roast ${e.message}"
        }
    }

    private fun buildPrompt(insights: RoastAnalyzerService.RoastInsights): String {
        val profile = insights.profileInsights
        val repos = insights.repoInsights
        val commits = insights.commitInsights
        val activity = insights.activityInsights

        return buildString {
            appendLine("Generate a hilarious roast for GitHub user @${profile.username}. Here's what I found:")
            appendLine()

            appendLine(" PROFILE:")
            appendLine("- Username: ${profile.username}")
            appendLine("- Bio: ${if (profile.hasBio) "Available" else "Not available"}")
            appendLine("- Followers: ${profile.followerCount}")
            appendLine("- Following: ${profile.followingCount}")
            appendLine("- Public repos: ${profile.repoCount}")
            appendLine("- Account age: ${profile.accountAge}")
            if (profile.roastablePoints.isNotEmpty()) {
                appendLine("- Red flags:")
                profile.roastablePoints.forEach {
                    appendLine("  - $it")
                }
            }
            appendLine()

            appendLine(" REPOSITORIES:")
            appendLine("- Total repos: ${repos.totalRepos}")
            appendLine("- Languages: ${repos.languages.entries.joinToString(", ") { "${it.key} (${it.value})" }}")
            appendLine("- Average forks: ${"%.2f".format(repos.avgStars)}")
            if (repos.genericNames.isNotEmpty()) {
                appendLine("- Generic repo names: ${repos.genericNames.take(5).joinToString(", ")}")
            }
            if (repos.roastablePoints.isNotEmpty()) {
                appendLine("- Repository issues: ${repos.roastablePoints.joinToString("; ")}")
            }
            appendLine()

            appendLine(" COMMITS:")
            appendLine("- Total recent commits: ${commits.totalCommits}")
            appendLine("- Late night commits: ${commits.lateNightCommits}")
            if (commits.lazyMessages.isNotEmpty()) {
                appendLine(
                    "- Lazy commit messages found: ${
                        commits.lazyMessages.take(5).joinToString(", ") { "\"$it\"" }
                    }"
                )
            }
            if (commits.recentCommits.isNotEmpty()) {
                appendLine("- Recent commits: ${commits.recentCommits.take(3).joinToString("; ")}")
            }
            if (commits.roastablePoints.isNotEmpty()) {
                appendLine("- Commit problems: ${commits.roastablePoints.joinToString("; ")}")
            }
            appendLine()

            appendLine(" ACTIVITY:")
            appendLine("- Total recent events: ${activity.totalEvents}")
            appendLine("- Event breakdown: ${activity.eventTypes.entries.joinToString(", ") { "${it.key}: ${it.value}" }}")
            if (activity.roastablePoints.isNotEmpty()) {
                appendLine("- Activity concerns: ${activity.roastablePoints.joinToString("; ")}")
            }
            appendLine()

            appendLine("Based on all this data, write a funny, witty roast. Be creative and use specific details!")
        }
    }
}