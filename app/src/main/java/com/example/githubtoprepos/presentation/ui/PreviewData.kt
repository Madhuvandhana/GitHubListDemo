package com.example.githubtoprepos.presentation.ui

import com.example.githubtoprepos.data.remote.dto.Contributor
import com.example.githubtoprepos.domain.model.GithubRepos
import com.example.githubtoprepos.domain.model.Owner

val sampleGithubRepos =
    listOf(
        GithubRepos(
            id = 28457823,
            name = "freeCodeCamp",
            description = "freeCodeCamp.org's open-source codebase and curriculum. Learn to code for free.",
            stargazersCount = 408229,
            owner =
            Owner(
                login = "freeCodeCamp",
                avatarUrl = "https://avatars.githubusercontent.com/u/9892522?v=4",
            ),
        ),
        GithubRepos(
            id = 23758634,
            name = "flutter",
            description = "Flutter makes it easy and fast to build beautiful mobile apps.",
            stargazersCount = 158000,
            owner =
            Owner(
                login = "flutter",
                avatarUrl = "https://avatars.githubusercontent.com/u/14101776?v=4",
            ),
        ),
        GithubRepos(
            id = 27932032,
            name = "TensorFlow",
            description = "An open-source software library for dataflow and differentiable programming across a range of tasks.",
            stargazersCount = 168000,
            owner =
            Owner(
                login = "tensorflow",
                avatarUrl = "https://avatars.githubusercontent.com/u/15658614?v=4",
            ),
        ),
        GithubRepos(
            id = 16342391,
            name = "react",
            description = "A declarative, efficient, and flexible JavaScript library for building user interfaces.",
            stargazersCount = 184000,
            owner =
            Owner(
                login = "facebook",
                avatarUrl = "https://avatars.githubusercontent.com/u/69631?v=4",
            ),
        ),
        GithubRepos(
            id = 34205432,
            name = "Django",
            description = "A high-level Python Web framework that encourages rapid development and clean, pragmatic design.",
            stargazersCount = 62000,
            owner =
            Owner(
                login = "django",
                avatarUrl = "https://avatars.githubusercontent.com/u/1311173?v=4",
            ),
        ),
    )

val topContributor = Contributor(
    login = "krahets",
    avatar_url = "https://avatars.githubusercontent.com/u/26993056?v=4",
    contributions = 1039
)
