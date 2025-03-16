package ru.profw.favoriterepomanager.model


data class LikedRepository(
    val id: Long,
    val name: String,
    val ownerLogin: String,
    val avatarUrl: String,
    val htmlUrl: String,
    val description: String?,
)