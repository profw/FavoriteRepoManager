package ru.profw.favoriterepomanager.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import ru.profw.favoriterepomanager.R
import ru.profw.favoriterepomanager.model.LikedRepository

class RepoAdapter(
    private val onOpenClick: (LikedRepository) -> Unit,
    private val onDeleteClick: (LikedRepository) -> Unit
) : RecyclerView.Adapter<RepoAdapter.RepoViewHolder>() {
    var repositories: List<LikedRepository> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    inner class RepoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.repoName)
        val owner: TextView = itemView.findViewById(R.id.repoOwner)
        val avatar: ImageView = itemView.findViewById(R.id.repoAvatar)
        val openButton: ImageButton = itemView.findViewById(R.id.openButton)
        val deleteButton: ImageButton = itemView.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_repo, parent, false)
        return RepoViewHolder(view)
    }

    override fun onBindViewHolder(holder: RepoViewHolder, position: Int) {
        val repo = repositories[position]

        holder.name.text = repo.name
        holder.owner.text = repo.ownerLogin
        holder.avatar.load(repo.avatarUrl)

        holder.openButton.setOnClickListener {
            onOpenClick(repo)
        }

        holder.deleteButton.setOnClickListener {
            onDeleteClick(repo)
        }
    }

    override fun getItemCount() = repositories.size
}