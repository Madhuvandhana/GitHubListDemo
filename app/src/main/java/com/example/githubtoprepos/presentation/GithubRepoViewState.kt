package com.example.githubtoprepos.presentation

import androidx.compose.runtime.Immutable
import com.example.githubtoprepos.domain.model.util.Resource
import com.example.githubtoprepos.domain.model.GithubRepos

@Immutable
data class GithubRepoViewState(
    val resource: Resource<List<GithubRepos>> = Resource.Loading()
)