package ru.profw.favoriterepomanager

import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.request.crossfade
import ru.profw.favoriterepomanager.adapter.RepoAdapter
import ru.profw.favoriterepomanager.databinding.ActivityMainBinding
import ru.profw.favoriterepomanager.model.LikedRepository
import ru.profw.favoriterepomanager.viewmodel.FavoriteViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: RepoAdapter
    private val viewModel: FavoriteViewModel by viewModels()

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
                viewModel.deleteRepository(repo)
            }
        )

        binding.reposRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.reposRecyclerView.adapter = adapter

        viewModel.repositories.observe(this) { repositories ->
            adapter.repositories = repositories
        }
        viewModel.isEmpty.observe(this) { isEmpty ->
            if (isEmpty) {
                Toast.makeText(this, R.string.no_liked_repos, Toast.LENGTH_SHORT).show()
            }
        }

        binding.refreshButton.setOnClickListener {
            viewModel.loadRepositories()
        }

        SingletonImageLoader.setSafe { context ->
            ImageLoader.Builder(context)
                .crossfade(true)
                .build()
        }

        viewModel.loadRepositories()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}