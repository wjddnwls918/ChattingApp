package com.example.chatapp.model

import androidx.lifecycle.LiveData

class ChatRepository(private val chatDao: ChatDao) {

    val allChats: LiveData<List<Chat>> = chatDao.getAllChats()

    suspend fun allChatsByRoomId(roomId: Int): List<Chat> {
        return chatDao.getAllChatsByRoomId(roomId)
    }

    suspend fun insert(chat: Chat) {
        chatDao.insert(chat)
    }

}