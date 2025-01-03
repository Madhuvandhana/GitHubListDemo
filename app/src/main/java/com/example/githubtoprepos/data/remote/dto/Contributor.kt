package com.example.githubtoprepos.data.remote.dto

import androidx.compose.runtime.Immutable

@Immutable
data class Contributor(
    val login: String,
    val avatar_url: String,
    val contributions: Int
)