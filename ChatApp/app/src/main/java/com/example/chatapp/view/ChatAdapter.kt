package com.example.chatapp.view


import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import com.example.chatapp.databinding.CenterContentBinding
import com.example.chatapp.databinding.MyContentBinding
import com.example.chatapp.databinding.OthersContentBinding
import com.example.chatapp.model.Chat

class ChatAdapter(var userId: Int) : ListAdapter<Chat, RecyclerView.ViewHolder>(ChatDiffCallback) {

    val TYPE_CENTER = 0
    val TYPE_OTHERS = 1
    val TYPE_MY = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            TYPE_CENTER -> return CenterContentHolder(
                CenterContentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )

            TYPE_OTHERS -> return OthersContentHolder(
                OthersContentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
            else -> return MyContentHolder(
                MyContentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )

        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val content = getItem(position)

        when (holder) {
            is CenterContentHolder -> {
                holder.bind(content)
            }
            is OthersContentHolder -> {
                holder.bind(content)
            }
            else -> {
                (holder as MyContentHolder).bind(content)
            }
        }

    }

    override fun getItemViewType(position: Int): Int {

        when {
            getItem(position).userId == 0 -> {
                //Log.d("checkUser : center", userId.toString())
                //Log.d("checkUser", "center")
                return TYPE_CENTER
            }
            getItem(position).userId != userId -> {
                //Log.d("checkUser : others", userId.toString())
                return TYPE_OTHERS
            }
            else -> {
                //Log.d("checkUser : my", userId.toString())
                //Log.d("checkUser", "my")
                return TYPE_MY
            }
        }
    }

    inner class CenterContentHolder(val binding: CenterContentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(chat: Chat) {
            binding.chat = chat
        }
    }

    inner class OthersContentHolder(val binding: OthersContentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(chat: Chat) {
            binding.chat = chat
        }
    }

    inner class MyContentHolder(val binding: MyContentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(chat: Chat) {
            binding.chat = chat
        }
    }

}


object ChatDiffCallback : DiffUtil.ItemCallback<Chat>() {
    override fun areContentsTheSame(oldItem: Chat, newItem: Chat): Boolean {
        return oldItem == newItem
    }

    override fun areItemsTheSame(oldItem: Chat, newItem: Chat): Boolean {
        return oldItem.id == newItem.id
    }
}