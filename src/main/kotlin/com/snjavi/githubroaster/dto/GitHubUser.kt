package com.snjavi.githubroaster.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class GitHubUser(
    val id: Long,
    val login: String,
    val name: String?,
    val bio: String?,
    val location: String?,
    val company: String?,
    val email: String?,
    val followers: Int,
    val following: Int,

    @JsonProperty("public_repos")
    val publicRepos: Int,

    @JsonProperty("public_gists")
    val publicGists: Int,

    @JsonProperty("created_at")
    val createdAt: String,

    @JsonProperty("updated_at")
    val updatedAt: String,

    @JsonProperty("avatar_url")
    val avatarUrl: String,

    @JsonProperty("html_url")
    val htmlUrl: String,
)
