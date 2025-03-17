package ru.profw.favoriterepomanager

import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import ru.profw.favoriterepomanager.adapter.RepoAdapter
import ru.profw.favoriterepomanager.databinding.ActivityMainBinding
import ru.profw.favoriterepomanager.model.LikedRepository

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: RepoAdapter
    companion object {
        const val AUTHORITY = "ru.profw.repofinder.provider"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        adapter = RepoAdapter(
            onOpenClick = { repo ->
                val intent = Intent(Intent.ACTION_VIEW, repo.htmlUrl.toUri())
                startActivity(intent)
            },
            onDeleteClick = { repo ->
                deleteRepository(repo)
            }
        )

        binding.reposRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.reposRecyclerView.adapter = adapter

        binding.searchButton.setOnClickListener {
            loadRepositories()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun loadRepositories() {
        val cursor = contentResolver.query(
            "content://$AUTHORITY/liked_repositories".toUri(),
            null, null, null, null
        )

        val repositories = mutableListOf<LikedRepository>()
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

                repositories.add(
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
        if (repositories.isEmpty()) {
            Toast.makeText(this, R.string.no_liked_repos, Toast.LENGTH_SHORT).show()
        } else {
            adapter.repositories = repositories
        }
        adapter.notifyDataSetChanged()
    }


    private fun deleteRepository(repo: LikedRepository) {
        val uri =
            "content://$AUTHORITY/liked_repositories/${repo.id}"
                .toUri()
        contentResolver.delete(uri, null, null)

        // Обновляем список после удаления
        loadRepositories()
    }

    private fun Cursor.getStringOrEmpty(index: Int): String =
        if (index >= 0) getString(index) else ""
}