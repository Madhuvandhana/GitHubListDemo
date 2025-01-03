package com.example.githubtoprepos.presentation

import com.example.githubtoprepos.domain.model.GithubRepos

interface GithubRepoViewAction {
    object FetchRepositories : GithubRepoViewAction
    data class UpdateVisibleKeys(val visibleItems: List<GithubRepos>): GithubRepoViewAction
}