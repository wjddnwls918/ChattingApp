package com.example.chatapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat")
data class Chat(
    @PrimaryKey(autoGenerate = true) val id: Int, val name: String,
    val roomId: Int,
    val userId: Int,
    val message: String,
    val inputTime: String
)