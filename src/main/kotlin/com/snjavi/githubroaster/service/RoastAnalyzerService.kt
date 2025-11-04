package com.snjavi.githubroaster.service

import com.snjavi.githubroaster.dto.GitHubCommit
import com.snjavi.githubroaster.dto.GitHubEvent
import com.snjavi.githubroaster.dto.GitHubRepo
import com.snjavi.githubroaster.dto.GitHubUser
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
class RoastAnalyzerService {
    fun analyzeForRoast(data: RoastData): RoastInsights {
        return RoastInsights(
            profileInsights = analyzeProfile(data.profile),
            repoInsights = analyzeRepositories(data.repositories),
            commitInsights = analyzeCommits(data.commits),
            activityInsights = analyzeActivity(data.events)
        )
    }

    private fun analyzeProfile(profile: GitHubUser?): ProfileInsights {
        if (profile == null) {
            return ProfileInsights(
                username = "",
                hasBio = false,
                followerCount = 0,
                followingCount = 0,
                repoCount = 0,
                accountAge = "",
                roastablePoints = emptyList()
            )
        }

        val roastablePoints = mutableListOf<String>()

        if (profile.bio.isNullOrBlank()) {
            roastablePoints.add("No bio - too lazy to write about yourself?")
        }

        when {
            profile.followers == 0 -> roastablePoints.add("I am not even going to try to roast you")
            profile.followers < 10 -> roastablePoints.add("${profile.followers} followers - quality over quantity, right?")
            profile.followers > 1000 -> roastablePoints.add("What - are you trying to be Linus Torvalds?")

        }

        if (profile.following > profile.followers * 2) {
            roastablePoints.add("Following ${profile.following} but only ${profile.followers} follow back - desprate much?")
        }

        when {
            profile.publicRepos == 0 -> roastablePoints.add("0 public repos - all talk, no code")
            profile.publicRepos > 30 -> roastablePoints.add("${profile.publicRepos} repos - ever heard of finishing what you start?")
        }

        return ProfileInsights(
            username = profile.login,
            hasBio = !profile.bio.isNullOrBlank(),
            followerCount = profile.followers,
            followingCount = profile.following,
            repoCount = profile.publicRepos,
            accountAge = profile.createdAt,
            roastablePoints = roastablePoints
        )
    }

    private fun analyzeRepositories(repos: List<GitHubRepo>): RepoInsights {
        val roastablePoints = mutableListOf<String>()

        if (repos.isEmpty()) {
            roastablePoints.add("No repos found - are you even a developer?")
            return RepoInsights(
                totalRepos = 0,
                languages = emptyMap(),
                avgStars = 0.0,
                genericNames = emptyList(),
                roastablePoints = roastablePoints
            )
        }

        val languages = repos.mapNotNull { it.language }.groupingBy { it }.eachCount()

        if (languages.containsKey("JavaScript")) {
            roastablePoints.add("JavaScript developer - spending more time fixing 'undefined' than coding")
        }

        if (languages.containsKey("PHP")) {
            roastablePoints.add("PHP? The 90s called, they want their language back")
        }

        if (languages.containsKey("Kotlin")) {
            roastablePoints.add("Looks like you converted this from Java and prayed the IDE would fix the rest.")
        }
        if (languages.containsKey("Python")) {
            roastablePoints.add("This repo runs perfectly — until someone upgrades Python.")
        }
        if (languages.containsKey("Java")) {
            roastablePoints.add("This repo has more boilerplate than an IKEA manual.")
        }
        if (languages.containsKey("Scala")) {
            roastablePoints.add("This repo is so complex even the compiler needs therapy.")
        }
        if (languages.containsKey("C#")) {
            roastablePoints.add("This repo looks like Java wearing a Microsoft badge.")
        }

        val avgStars = if (repos.isNotEmpty()) {
            repos.map { it.stars }.average()
        } else 0.0

        if (avgStars == 0.0 && repos.isNotEmpty()) {
            roastablePoints.add("Zero average forks - not even your mom starred your repos")
        }

        val genericKeywords = listOf(
            "test",
            "demo",
            "practice",
            "example",
            "template",
            "untitled",
            "sample",
            "hello-world",
            "helloWorld",
            "my-project",
            "new-project",
            "learning"
        )

        val genericRepos = repos.filter { repo ->
            genericKeywords.any { keyword ->
                repo.name.lowercase().contains(keyword)
            }
        }.map { it.name }

        if (genericRepos.isNotEmpty()) {
            roastablePoints.add("Repo names like ${genericRepos.take(3).joinToString(", ")} - did ChatGPT name these?")
        }

        val lowEngagementRepos = repos.filter { it.forksCount == 0 }
        if (lowEngagementRepos.size > repos.size / 2 && repos.size > 5) {
            roastablePoints.add("Over half your repos have zero forks - at least you're consistent at being ignored")
        }

        if (languages.size == 1 && repos.size > 5) {
            val singleLang = languages.keys.first()
            roastablePoints.add("Only coding in $singleLang? - Ever hear of being well-rounded?")
        }

        val noDescriptionCount = repos.count { it.description.isNullOrBlank() }
        if (noDescriptionCount > repos.size / 2) {
            roastablePoints.add("$noDescriptionCount repos with no description - let me guess, 'TODO: add description'?")
        }

        return RepoInsights(
            totalRepos = repos.size,
            languages = languages,
            avgStars = avgStars,
            genericNames = genericRepos,
            roastablePoints = roastablePoints
        )
    }

    private fun analyzeCommits(commits: List<GitHubCommit>): CommitInsights {
        val roastablePoints = mutableListOf<String>()

        if (commits.isEmpty()) {
            roastablePoints.add("No recent commits found - thinking about changing profession?")
            return CommitInsights(
                totalCommits = 0,
                lateNightCommits = 0,
                roastablePoints = roastablePoints,
                recentCommits = emptyList(),
                lazyMessages = emptyList()
            )
        }

        val lazyKeywords = listOf(
            "fix",
            "update",
            "wip",
            "test",
            "asdf",
            ".",
            "...",
            "changes",
            "stuff",
            "idk",
            "whatever",
            "yolo",
            "temp",
            "final",
            "temp2",
            "final2"
        )

        val lazyMessages = commits.map { it.commit.message }.filter { message ->
            val msg = message.lowercase().trim()
            msg.length < 5 || lazyKeywords.any { it == msg || msg.startsWith("$it ") }
        }

        val lateNightCommits = commits.count() { commit ->
            try {
                val dateTime = LocalDateTime.parse(
                    commit.commit.author.date, DateTimeFormatter.ISO_DATE_TIME
                )
                val hour = dateTime.hour
                hour >= 23 || hour < 5
            } catch (e: Exception) {
                false
            }
        }

        if (lazyMessages.isNotEmpty()) {
            val examples = lazyMessages.take(3).joinToString(", ") { "\"$it\"" }
            roastablePoints.add("Lazy commit messages like $examples - very professional!")

            if (lateNightCommits > commits.size / 2) {
                roastablePoints.add("${lateNightCommits} late night commits - healthy sleep schedule? Never heard of it")
            }

            val commitMessages = commits.map { it.commit.message.lowercase() }

            if (commitMessages.count { it.contains("fuck") || it.contains("shit") || it.contains("damn") } > 0) {
                roastablePoints.add("Profanity in commit messages - I'm sure your team lead loves that")
            }

            if (commitMessages.count { it.contains("final") } > 3) {
                roastablePoints.add("Multiple 'final' commits - we both know it wasn't final")
            }

            if (commits.size < 10) {
                roastablePoints.add("Only ${commits.size} recent commits - taking it easy, aren't we?")
            }
        }

        val recentCommitMessages = commits.take(5).map { it.commit.message }

        return CommitInsights(
            totalCommits = commits.size,
            lateNightCommits = lateNightCommits,
            roastablePoints = roastablePoints,
            recentCommits = recentCommitMessages,
            lazyMessages = lazyMessages
        )
    }

    private fun analyzeActivity(events: List<GitHubEvent>): ActivityInsights {
        val roastablePoints = mutableListOf<String>()

        if (events.isEmpty()) {
            roastablePoints.add("No recent activity - GitHub thinks you're a ghost")
            return ActivityInsights(
                totalEvents = 0,
                roastablePoints = roastablePoints,
                eventTypes = emptyMap()
            )
        }
        val eventTypes = events.groupingBy { it.type }.eachCount()

        val pushEvents = eventTypes["PushEvent"] ?: 0
        val issueEvents = (eventTypes["IssueEvent"] ?: 0) + (eventTypes["IssueCommentEvent"] ?: 0)
        val prEvents = (eventTypes["PullRequestEvent"] ?: 0) + (eventTypes["PullRequestReviewEvent"] ?: 0)
        val watchEvents = eventTypes["WatchEvent"] ?: 0
        val forkEvents = eventTypes["ForkEvent"] ?: 0

        if (pushEvents == 0) {
            roastablePoints.add("Fork collector - do you actually contribute or just hoard?")
        }

        if (forkEvents > pushEvents * 2) {
            roastablePoints.add("More watching than pushing - professional repo stalker")
        }

        if (watchEvents > pushEvents && pushEvents > 0) {
            roastablePoints.add("More watching than pushing - professional repo stalker")
        }

        if (issueEvents == 0 && prEvents == 0 && events.size > 10) {
            roastablePoints.add("No issues or PRs - not a team player or just a solo coder?")
        }

        if (eventTypes.size == 1) {
            val singleEventType = eventTypes.keys.first()
            roastablePoints.add("Only $singleEventType activities - ever tried doing something different?")
        }

        if (events.size < 10) {
            roastablePoints.add("Only ${events.size} recent events - part-time developer?")
        }
        return ActivityInsights(
            totalEvents = events.size,
            roastablePoints = roastablePoints,
            eventTypes = eventTypes
        )
    }

    data class RoastInsights(
        val profileInsights: ProfileInsights,
        val repoInsights: RepoInsights,
        val commitInsights: CommitInsights,
        val activityInsights: ActivityInsights
    )

    data class ProfileInsights(
        val username: String,
        val hasBio: Boolean,
        val followerCount: Int,
        val followingCount: Int,
        val repoCount: Int,
        val accountAge: String,
        val roastablePoints: List<String>
    )

    data class RepoInsights(
        val totalRepos: Int,
        val languages: Map<String, Int>,
        val avgStars: Double,
        val genericNames: List<String>,
        val roastablePoints: List<String>
    )

    data class CommitInsights(
        val totalCommits: Int,
        val lateNightCommits: Int,
        val roastablePoints: List<String>,
        val recentCommits: List<String>,
        val lazyMessages: List<String>
    )

    data class ActivityInsights(
        val totalEvents: Int,
        val roastablePoints: List<String>,
        val eventTypes: Map<String, Int>
    )
}