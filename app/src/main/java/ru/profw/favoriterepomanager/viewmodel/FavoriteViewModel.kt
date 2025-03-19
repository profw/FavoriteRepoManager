package ru.profw.favoriterepomanager.viewmodel

import android.app.Application
import android.database.Cursor
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import ru.profw.favoriterepomanager.model.LikedRepository

class FavoriteViewModel(application: Application) : AndroidViewModel(application) {
    val repositories = MutableLiveData<List<LikedRepository>>()
    val isEmpty = MutableLiveData<Boolean>()

    private val contentResolver = getApplication<Application>().contentResolver

    fun loadRepositories() {
        val loadedRepos = mutableListOf<LikedRepository>()
        try {
            val contentResolver = getApplication<Application>().contentResolver
            val cursor = contentResolver.query(
                "content://$AUTHORITY/liked_repositories".toUri(),
                null, null, null, null
            )

            cursor?.use {
                val idIndex = it.getColumnIndex("id")
                val nameIndex = it.getColumnIndex("name")
                val ownerLoginIndex = it.getColumnIndex("ownerLogin")
                val avatarUrlIndex = it.getColumnIndex("avatarUrl")
                val htmlUrlIndex = it.getColumnIndex("htmlUrl")
                val descriptionIndex = it.getColumnIndex("description")

                while (it.moveToNext()) {

                    val id = it.getLong(idIndex)
                    val name = it.getStringOrEmpty(nameIndex)
                    val ownerLogin = it.getStringOrEmpty(ownerLoginIndex)
                    val avatarUrl = it.getStringOrEmpty(avatarUrlIndex)
                    val htmlUrl = it.getStringOrEmpty(htmlUrlIndex)
                    val description = it.getStringOrEmpty(descriptionIndex)

                    loadedRepos.add(
                        LikedRepository(
                            id,
                            name,
                            ownerLogin,
                            avatarUrl,
                            htmlUrl,
                            description
                        )
                    )
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load favorite repos", e)
        }
        repositories.value = loadedRepos
        isEmpty.value = loadedRepos.isEmpty()
    }

    fun deleteRepository(repo: LikedRepository) {
        val uri = "content://$AUTHORITY/liked_repositories/${repo.id}".toUri()
        contentResolver.delete(uri, null, null)
        loadRepositories()
    }

    private fun Cursor.getStringOrEmpty(index: Int): String =
        if (index >= 0) {
            getString(index)
        } else {
            Log.w(TAG, "One or more columns not found in cursor")
            ""
        }

    companion object {
        private const val AUTHORITY = "ru.profw.repofinder.provider"
        private const val TAG = "FavoriteViewModel"
    }
}