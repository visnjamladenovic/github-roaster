package com.snjavi.githubroaster.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class GitHubRepo(
    val id: Long,
    val name:String,
    val description:String? = null,

    @JsonProperty("html_url")
    val url:String,

    @JsonProperty("forks_count")
    val forksCount: Int,

    val language:String? = null,

    val owner: Owner

)
data class Owner(
    val id: Long,
    val login: String,

    @JsonProperty("avatar_url")
    val avatarUrl: String,

    @JsonProperty("html_url")
    val htmlUrl: String,

    val type: String
)

