package com.example.githubtoprepos.domain.repository

import com.example.githubtoprepos.data.remote.dto.Contributor
import com.example.githubtoprepos.domain.model.util.Resource
import com.example.githubtoprepos.domain.model.GithubRepos
import kotlinx.coroutines.flow.Flow


interface GitHubContributorsRepository {
    suspend fun getTopStarredRepositories(): Flow<Resource<List<GithubRepos>>>
    suspend fun getTopContributor(owner: String, repo: String): Flow<Contributor?>
}