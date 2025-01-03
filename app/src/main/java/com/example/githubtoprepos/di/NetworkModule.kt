package com.example.githubtoprepos.di

import com.example.githubtoprepos.data.network.NetworkStateProviderImpl
import com.example.githubtoprepos.presentation.util.NetworkStateProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkModule {
    @Binds
    abstract fun bindNetworkStateProvider(
        networkStateProviderImpl: NetworkStateProviderImpl
    ): NetworkStateProvider
}