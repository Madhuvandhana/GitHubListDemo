package com.example.githubtoprepos.di

import com.example.githubtoprepos.data.repository.GithubContributorsRepositoryImpl
import com.example.githubtoprepos.domain.repository.GitHubContributorsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindGithubContributorsRepository(
        githubContributorsRepositoryImpl: GithubContributorsRepositoryImpl,
    ): GitHubContributorsRepository
}
