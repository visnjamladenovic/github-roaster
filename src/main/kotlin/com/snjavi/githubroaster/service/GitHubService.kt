package com.snjavi.githubroaster.service

import com.snjavi.githubroaster.dto.GitHubCommit
import com.snjavi.githubroaster.dto.GitHubEvent
import com.snjavi.githubroaster.dto.GitHubRepo
import com.snjavi.githubroaster.dto.GitHubUser
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate

@Service
class GitHubService(@Value("\${github.token:}") private val githubToken: String) {
    private val restTemplate = RestTemplate()
    private val githubApiUrl = "https://api.github.com"

    init {
        val tokenPreview = if (githubToken.isNotBlank()) "${githubToken.take(7)}..." else "NOT SET (using unauthenticated requests)"
        println("GitHub Token initialized: $tokenPreview")
    }

    private fun createHeaders(): HttpHeaders {
        val headers = HttpHeaders()
        if (githubToken.isNotBlank()) {
            headers.set("Authorization", "Bearer $githubToken")
        }
        headers.set("Accept", "application/vnd.github.v3+json")
        return headers
    }

    fun getRepository(owner: String, repoName: String): GitHubRepo? {
        val url = "$githubApiUrl/repos/$owner/$repoName"
        return try {
            println("Fetching repository: $url")
            val entity = HttpEntity<String>(createHeaders())
            val response = restTemplate.exchange(url, HttpMethod.GET, entity, GitHubRepo::class.java)
            response.body
        } catch (e: Exception) {
            println("Error fetching repository: ${e.message}")
            null
        }
    }

    fun getUserRepositories(username: String): List<GitHubRepo> {
        val url = "$githubApiUrl/users/$username/repos?per_page=100"
        return try {
            println("Fetching repositories for user: $username from $url")
            val entity = HttpEntity<String>(createHeaders())
            val response = restTemplate.exchange(url, HttpMethod.GET, entity, Array<GitHubRepo>::class.java)
            val repos = response.body?.toList() ?: emptyList()
            println("Found ${repos.size} repositories for $username")
            repos
        } catch (e: HttpClientErrorException.Forbidden) {
            println("ERROR: GitHub API rate limit exceeded!")
            println("Add a GitHub token to your .env file to increase rate limits")
            emptyList()
        } catch (e: Exception) {
            println("Error fetching repositories: ${e.message}")
            e.printStackTrace()
            emptyList()
        }
    }

    fun getUserProfile(username: String): GitHubUser? {
        val url = "$githubApiUrl/users/$username"
        return try {
            println("Fetching user profile: $url")
            val entity = HttpEntity<String>(createHeaders())
            val response = restTemplate.exchange(url, HttpMethod.GET, entity, GitHubUser::class.java)
            val user = response.body
            println("User profile fetched: ${user?.login}, repos: ${user?.publicRepos}")
            user
        } catch (e: HttpClientErrorException.Forbidden) {
            println("ERROR: GitHub API rate limit exceeded!")
            println("Add a GitHub token to your .env file to increase rate limits")
            null
        } catch (e: Exception) {
            println("Error fetching user profile: ${e.message}")
            e.printStackTrace()
            null
        }
    }

    fun getRepositoryCommits(owner: String, repoName: String, limit: Int = 30): List<GitHubCommit> {
        val url = "$githubApiUrl/repos/$owner/$repoName/commits?per_page=$limit"
        return try {
            println("Fetching commits from: $url")
            val entity = HttpEntity<String>(createHeaders())
            val response = restTemplate.exchange(url, HttpMethod.GET, entity, Array<GitHubCommit>::class.java)
            val commits = response.body?.toList() ?: emptyList()
            println("Found ${commits.size} commits in $owner/$repoName")
            commits
        } catch (e: HttpClientErrorException.Forbidden) {
            println("WARN: Rate limit hit for commits in $owner/$repoName, skipping...")
            emptyList()
        } catch (e: Exception) {
            println("Error fetching commits from $owner/$repoName: ${e.message}")
            emptyList()
        }
    }

    fun getUserEvents(username: String, limit: Int = 30): List<GitHubEvent> {
        val url = "$githubApiUrl/users/$username/events?per_page=$limit"
        return try {
            println("Fetching events for user: $username")
            val entity = HttpEntity<String>(createHeaders())
            val response = restTemplate.exchange(url, HttpMethod.GET, entity, Array<GitHubEvent>::class.java)
            val events = response.body?.toList() ?: emptyList()
            println("Found ${events.size} events for $username")
            events
        } catch (e: HttpClientErrorException.Forbidden) {
            println("WARN: Rate limit hit for events, skipping...")
            emptyList()
        } catch (e: Exception) {
            println("Error fetching user events: ${e.message}")
            e.printStackTrace()
            emptyList()
        }
    }

    fun getRoastData(username: String): RoastData {
        println("=== Getting roast data for: $username ===")

        val profile = getUserProfile(username)
        val repos = getUserRepositories(username)
        val events = getUserEvents(username)

        println("Profile: ${if (profile != null) "✓" else "✗"}")
        println("Repositories: ${repos.size}")
        println("Events: ${events.size}")

        val recentCommits = repos.take(5).flatMap { repo ->
            println("Getting commits for repo: ${repo.name}")
            getRepositoryCommits(repo.owner.login, repo.name, 10)
        }

        println("Total commits collected: ${recentCommits.size}")
        println("=== Roast data collection complete ===")

        return RoastData(
            profile = profile,
            repositories = repos,
            commits = recentCommits,
            events = events
        )
    }
}

data class RoastData(
    val profile: GitHubUser?,
    val repositories: List<GitHubRepo>,
    val commits: List<GitHubCommit>,
    val events: List<GitHubEvent>
)