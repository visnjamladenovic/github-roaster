package com.snjavi.githubroaster.controller

import com.snjavi.githubroaster.dto.GitHubCommit
import com.snjavi.githubroaster.dto.GitHubEvent
import com.snjavi.githubroaster.dto.GitHubRepo
import com.snjavi.githubroaster.dto.GitHubUser
import com.snjavi.githubroaster.service.GitHubService
import com.snjavi.githubroaster.service.OpenAIRoastService
import com.snjavi.githubroaster.service.RoastAnalyzerService
import com.snjavi.githubroaster.service.RoastData
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/github")
class GitHubController(
    private val gitHubService: GitHubService,
    private val roastAnalyzerService: RoastAnalyzerService,
    private val openAIRoastService: OpenAIRoastService
) {

    @GetMapping("/repo/{owner}/{repoName}")
    fun getRepository(
        @PathVariable owner: String,
        @PathVariable repoName: String
    ): GitHubRepo? {
        return gitHubService.getRepository(owner, repoName)
    }

    @GetMapping("/user/{username}/repos")
    fun getUserRepos(@PathVariable username: String): List<GitHubRepo> {
        return gitHubService.getUserRepositories(username)
    }

    @GetMapping("/user/{username}")
    fun getUserProfile(@PathVariable username: String): GitHubUser? {
        return gitHubService.getUserProfile(username)
    }

    @GetMapping("/repo/{owner}/{repoName}/commits")
    fun getCommits(
        @PathVariable owner: String,
        @PathVariable repoName: String
    ): List<GitHubCommit> {
        return gitHubService.getRepositoryCommits(owner, repoName)
    }

    @GetMapping("/user/{username}/events")
    fun getUserEvents(
        @PathVariable username: String,
        @RequestParam(defaultValue = "30") limit: Int
    ): List<GitHubEvent> {
        return gitHubService.getUserEvents(username, limit)
    }

    @GetMapping("/roast-data/{username}")
    fun getRoastData(@PathVariable username: String): RoastData = gitHubService.getRoastData(username)

    @GetMapping("/debug/{username}")
    fun debugUser(@PathVariable username: String): Map<String, Any> {
        val roastData = gitHubService.getRoastData(username)
        val insights = roastAnalyzerService.analyzeForRoast(roastData)

        return mapOf(
            "profile" to mapOf(
                "username" to insights.profileInsights.username,
                "repoCount" to insights.profileInsights.repoCount,
                "followerCount" to insights.profileInsights.followerCount,
                "roastablePoints" to insights.profileInsights.roastablePoints
            ),
            "repos" to mapOf(
                "totalRepos" to insights.repoInsights.totalRepos,
                "languages" to insights.repoInsights.languages,
                "avgStars" to insights.repoInsights.avgStars,
                "roastablePoints" to insights.repoInsights.roastablePoints
            ),
            "commits" to mapOf(
                "totalCommits" to insights.commitInsights.totalCommits,
                "lateNightCommits" to insights.commitInsights.lateNightCommits,
                "roastablePoints" to insights.commitInsights.roastablePoints
            ),
            "rawData" to mapOf(
                "profilePublicRepos" to (roastData.profile?.publicRepos ?: 0),
                "repositoriesListSize" to roastData.repositories.size,
                "commitsListSize" to roastData.commits.size,
                "eventsListSize" to roastData.events.size
            )
        )
    }

    @GetMapping("/roast/{username}")
    fun getRoast(@PathVariable username: String): Map<String, String> {
        try {
            val roastData = gitHubService.getRoastData(username)
            val insights = roastAnalyzerService.analyzeForRoast(roastData)
            val roast = openAIRoastService.generateRoast(insights)

            return mapOf("roast" to roast)
        } catch (e: Exception) {
            println("Error generating roast: ${e.message}")
            e.printStackTrace()
            throw e
        }
    }
}