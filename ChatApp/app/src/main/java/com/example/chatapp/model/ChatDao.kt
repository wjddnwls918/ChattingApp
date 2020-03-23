package com.example.chatapp.model

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ChatDao {

    @Query("SELECT * from chat ORDER BY id ASC")
    fun getAllChats(): LiveData<List<Chat>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(chat: Chat)

    @Query("DELETE FROM chat")
    suspend fun deleteAll()

    @Query("SELECT * from chat where roomId = :roomId")
    suspend fun getAllChatsByRoomId(roomId: Int): List<Chat>

}