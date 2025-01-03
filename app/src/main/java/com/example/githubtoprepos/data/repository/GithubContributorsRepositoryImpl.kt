package com.example.githubtoprepos.data.repository

import com.example.githubtoprepos.domain.model.util.Resource
import com.example.githubtoprepos.data.mapper.toDomain
import com.example.githubtoprepos.data.remote.GitHubApiService
import com.example.githubtoprepos.data.remote.dto.Contributor
import com.example.githubtoprepos.domain.model.GithubRepos
import com.example.githubtoprepos.domain.repository.GitHubContributorsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GithubContributorsRepositoryImpl @Inject constructor(
    private val apiService: GitHubApiService,
) : GitHubContributorsRepository {
    override suspend fun getTopStarredRepositories(): Flow<Resource<List<GithubRepos>>> = flow {
        try {
            val response = apiService.getTopStarredRepositories()
            emit(
                Resource.Success(
                    data = response.items.map { it.toDomain() },
                ),
            )
        } catch (e: Exception) {
            emit(Resource.Error(
                message = "Couldn't load github starred repository data",
                data = null,
            ))
        }
    }

    override suspend fun getTopContributor(owner: String, repo: String): Flow<Contributor?> = flow{
        try {
            val contributors = apiService.getTopContributor(owner, repo)
            val contributor = contributors.maxByOrNull { it.contributions }
            emit(contributor)
        } catch (e: Exception) {
            throw e
        }
    }
}