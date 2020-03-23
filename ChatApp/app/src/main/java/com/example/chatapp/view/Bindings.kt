package com.example.chatapp.view

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.model.Chat

@BindingAdapter("items")
fun setItems(listView: RecyclerView, items: List<Chat>?) {
    items?.let {
        (listView.adapter as ChatAdapter).submitList(items)
        //(listView.adapter as ChatAdapter).notifyDataSetChanged()
    }
}

@BindingAdapter("bind_adapter")
fun setBindAdapter(view: RecyclerView, adapter: ChatAdapter?) {
    adapter?.let {
        view.adapter = it
    }
}