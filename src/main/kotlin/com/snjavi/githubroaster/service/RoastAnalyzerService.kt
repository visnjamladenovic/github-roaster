package com.snjavi.githubroaster.service

import com.snjavi.githubroaster.dto.GitHubRepo
import com.snjavi.githubroaster.dto.GitHubUser
import org.springframework.stereotype.Service

@Service
class RoastAnalyzerService {
  /*  fun analyzeForRoast(data: RoastData): RoastInsights{
         return RoastInsights(
            profileInsights = analyzeProfile(data.profile)
        )
    } */
    private fun analyzeProfile(profile: GitHubUser?):ProfileInsights {
        if(profile == null) {
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

        if(profile.bio.isNullOrBlank()) {
            roastablePoints.add("No bio - too lazy to write about yourself?")
        }

        when{
            profile.followers == 0 -> roastablePoints.add("I am not even going to try to roast you")
            profile.followers < 10 -> roastablePoints.add("${profile.followers} followers - quality over quantity, right?")
            profile.followers > 1000 -> roastablePoints.add("What - are you trying to be Linus Torvalds?")

        }

        if(profile.following > profile.followers * 2) {
            roastablePoints.add("Following ${profile.following} but only ${profile.followers} follow back - desprate much?")
        }

        when{
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

   /* private fun analyzeRepositories(repos: List<GitHubRepo>): RepoInsights {
        val roastablePoints = mutableListOf<String>()

        if(repos.isEmpty()) {
            roastablePoints.add("No repos found - are you even a developer?")
            return RepoInsights(
                totalRepos = 0,
                languages = emptyMap(),
                avgStars = 0.0,
                genericNames = emptyList(),
                roastablePoints = roastablePoints
            )
        }

        val languages = repos.mapNotNull {it.language }.groupingBy { it }.eachCount()

        if(languages.containsKey("JavaScript")){
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

        return RepoInsights(
            totalRepos = repos.size,
            languages = languages,
            avgStars = avgStars,
            genericNames = genericRepos,
            roastablePoints = roastablePoints
        )
    }
    */

    data class RoastInsights(
        val profileInsights:ProfileInsights,
        val repoInsights: RepoInsights,
        val commitInsights:CommitInsights,
        val activityInsights: ActivityInsights
    )
    data class ProfileInsights(
        val username:String,
        val hasBio:Boolean,
        val followerCount:Int,
        val followingCount:Int,
        val repoCount:Int,
        val accountAge:String,
        val roastablePoints: List<String>
    )

    data class RepoInsights(
        val totalRepos:Int,
        val languages: Map<String,Int>,
        val avgStars:Double,
        val genericNames:List<String>,
        val roastablePoints: List<String>
    )

    data class CommitInsights(
        val totalCommits:Int,
        val lateNightCommits:Int,
        val roastablePoints: List<String>,
        val recentCommits:List<String>,
        val lazyMessages:List<String>
    )

    data class ActivityInsights(
        val totalEvents:Int,
        val roastablePoints: List<String>,
        val eventTypes: Map<String,Int>
    )
}