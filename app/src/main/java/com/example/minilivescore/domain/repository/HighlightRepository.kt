package com.example.minilivescore.domain.repository

import com.example.minilivescore.utils.Resource

interface HighlightRepository {
    suspend fun findVideoInList(videoTitle: String): Resource<String?>
}