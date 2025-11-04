package com.snjavi.githubroaster.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class GitHubEvent(
    val id: String,
    val type: String,

    @JsonProperty("created_at")
    val createdAt: String,

    val repo: EventRepo,
    val payload: Map<String, Any>?
)

data class EventRepo(
    val id: Long,
    val name: String,
    val url: String,
)
