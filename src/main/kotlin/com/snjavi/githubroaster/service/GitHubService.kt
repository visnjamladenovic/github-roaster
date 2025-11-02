package com.snjavi.githubroaster.service

import com.snjavi.githubroaster.dto.GitHubCommit
import com.snjavi.githubroaster.dto.GitHubEvent
import com.snjavi.githubroaster.dto.GitHubRepo
import com.snjavi.githubroaster.dto.GitHubUser
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForObject

@Service
class GitHubService {
    private val restTemplate = RestTemplate()
    private val githubApiUrl = "https://api.github.com"

    fun getRepository(owner: String, repoName: String): GitHubRepo? {
        val url = "$githubApiUrl/repos/$owner/$repoName"
        return try {
            restTemplate.getForObject<GitHubRepo>(url)
        } catch (e: Exception) {
            println("Error fetching repository: ${e.message}")
            null
        }
    }

    fun getUserRepositories(username: String): List<GitHubRepo> {
        val url = "$githubApiUrl/users/$username/repos"
        return try {
            restTemplate.getForObject<Array<GitHubRepo>>(url)?.toList() ?: emptyList()
        } catch (e: Exception) {
            println("Error fetching repositories: ${e.message}")
            emptyList<GitHubRepo>()
        }
    }

    fun getUserProfile(username: String): GitHubUser? {
        val url = "$githubApiUrl/users/$username"
        return try {
            restTemplate.getForObject<GitHubUser>(url)
        } catch (e: Exception) {
            println("Error fetching user profile: ${e.message}")
            null
        }
    }

    fun getRepositoryCommits(owner: String, repoName: String, limit: Int = 30): List<GitHubCommit> {
        val url = "$githubApiUrl/repos/$owner/$repoName/commits?per_page=$limit"
        return try {
            restTemplate.getForObject<Array<GitHubCommit>>(url)?.toList() ?: emptyList()
        } catch (e: Exception) {
            println("Error: ${e.message}")
            emptyList()
        }
    }

    fun getUserEvents(username: String, limit: Int = 30): List<GitHubEvent> {
        val url = "$githubApiUrl/users/$username/events?per_page=$limit"
        return try {
            restTemplate.getForObject<Array<GitHubEvent>>(url)?.toList() ?: emptyList()
        } catch (e: Exception) {
            println("Error fetching user events: ${e.message}")
            emptyList<GitHubEvent>()
        }
    }

    fun getRoastData(username: String): RoastData {
        val profile = getUserProfile(username)
        val repos = getUserRepositories(username)
        val events = getUserEvents(username)

        val recentCommits = repos.take(5).flatMap { repo ->
            getRepositoryCommits(repo.owner.login, repo.name, 10)
        }
        return RoastData(
            profile = profile,
            repositories = repos,
            commits = recentCommits,
            events = events
        )
    }
}
    data class RoastData(
        val profile:GitHubUser?,
        val repositories:List<GitHubRepo>,
        val commits:List<GitHubCommit>,
        val events:List<GitHubEvent>
    )