package com.example.githubtoprepos.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class Owner(
    val login: String,
    val avatarUrl: String
)