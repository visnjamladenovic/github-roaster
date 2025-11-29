package com.snjavi.githubroaster.controller

import com.snjavi.githubroaster.dto.GitHubCommit
import com.snjavi.githubroaster.dto.GitHubEvent
import com.snjavi.githubroaster.dto.GitHubRepo
import com.snjavi.githubroaster.dto.GitHubUser
import com.snjavi.githubroaster.service.GitHubService
import com.snjavi.githubroaster.service.RoastData
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/github")
class GitHubController(private val gitHubService: GitHubService) {

    //get a specific repository
    @GetMapping("/repo/{owner}/{repoName}")
    fun getRepository(
        @PathVariable owner: String,
        @PathVariable repoName: String
    ): GitHubRepo? {
        return gitHubService.getRepository(owner, repoName)
    }

    @GetMapping("/ping")
    fun ping(): String {
        return "Backend reached successfully"
    }

    //get all repos for one user
    @GetMapping("/user/{username}/repos")
    fun getUserRepos(@PathVariable username: String): List<GitHubRepo> {
        return gitHubService.getUserRepositories(username)
    }

    //gets all data for a specific user
    @GetMapping("/user/{username}")
    fun getUserProfile(@PathVariable username: String): GitHubUser? {
        return gitHubService.getUserProfile(username)
    }

    //all commits for one repo
    @GetMapping("/repo/{owner}/{repoName}/commits")
    fun getCommits(
        @PathVariable owner: String,
        @PathVariable repoName: String
    ): List<GitHubCommit> {
        return gitHubService.getRepositoryCommits(owner, repoName)
    }

    //get user's recent events
    @GetMapping("/user/{username}/events")
    fun getUserEvents(
        @PathVariable username: String,
        @RequestParam(defaultValue = "30") limit: Int
    ): List<GitHubEvent> {
        return gitHubService.getUserEvents(username, limit)

    }

    //get all roast data for a user profile + repos + commits + events
    @GetMapping("/roast-data/{username}")
    fun getRoastData(@PathVariable username: String): RoastData = gitHubService.getRoastData(username)
}