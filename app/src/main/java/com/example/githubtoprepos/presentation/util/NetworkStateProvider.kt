package com.example.githubtoprepos.presentation.util

interface NetworkStateProvider {
    fun isInternetAvailable(): Boolean
}