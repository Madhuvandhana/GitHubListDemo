package com.example.githubtoprepos.data.mapper

import com.example.githubtoprepos.data.remote.dto.GithubRepoItemDto
import com.example.githubtoprepos.domain.model.GithubRepos
import com.example.githubtoprepos.domain.model.Owner

fun GithubRepoItemDto.toDomain(): GithubRepos {
    return GithubRepos(
        id = id,
        name = name,
        description = description,
        stargazersCount = stargazers_count,
        owner = Owner(
            login = owner.login,
            avatarUrl = owner.avatar_url
        )
    )
}