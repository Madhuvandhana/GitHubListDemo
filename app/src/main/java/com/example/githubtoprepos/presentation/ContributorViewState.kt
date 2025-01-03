package com.example.githubtoprepos.presentation

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import com.example.githubtoprepos.data.remote.dto.Contributor

@Immutable
data class ContributorViewState(
    val topContributors : SnapshotStateMap<String, Contributor?> = mutableStateMapOf()
)