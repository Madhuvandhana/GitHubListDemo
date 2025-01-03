package com.example.githubtoprepos.data.remote

import com.example.githubtoprepos.data.remote.dto.Contributor
import com.example.githubtoprepos.data.remote.dto.GithubRepoDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GitHubApiService {
    @GET("search/repositories")
    suspend fun getTopStarredRepositories(
        @Query("q") query: String = "stars:>0",
        @Query("per_page") perPage: Int = 100
    ): GithubRepoDto

    @GET("repos/{owner}/{repo}/contributors")
    suspend fun getTopContributor(
        @Path("owner") owner: String,
        @Path("repo") repo: String
    ): List<Contributor>
}