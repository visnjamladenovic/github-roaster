package com.snjavi.githubroaster.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class GitHubCommit(
    val sha: String,
    val commit: CommitDetails,
)

data class CommitDetails(
    val message: String,
    val author: CommitAuthorDetails
)

data class CommitAuthorDetails(
    val name: String,
    val email: String,
    val date: String
)