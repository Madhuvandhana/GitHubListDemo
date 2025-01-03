package com.example.githubtoprepos.domain.model

import androidx.compose.runtime.Immutable
import com.example.githubtoprepos.data.remote.dto.Contributor

@Immutable
data class GithubRepos(
    val id: Int,
    val name: String,
    val description: String?,
    val stargazersCount: Int,
    val owner: Owner
)