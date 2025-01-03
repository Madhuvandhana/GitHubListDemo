package com.example.githubtoprepos.presentation.util

private const val MILLION = 1_000_000
private const val THOUSAND = 1_000
fun formatStarCount(stars: Int): String {
    return when {
        stars >= MILLION -> "${(stars / MILLION)}M"
        stars >= THOUSAND -> "${(stars / THOUSAND)}K"
        else -> "$stars"
    }
}