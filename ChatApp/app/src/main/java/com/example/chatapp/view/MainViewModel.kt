package com.example.chatapp.view

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.chatapp.common.Event
import com.example.chatapp.common.util.DateUtil
import com.example.chatapp.model.Chat
import com.example.chatapp.model.ChatDatabase
import com.example.chatapp.model.ChatRepository
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import java.net.URISyntaxException

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val _messageSendClickEvent = MutableLiveData<Event<Unit>>()
    val messageSendClickEvent: LiveData<Event<Unit>> = _messageSendClickEvent

    private val _enterRoomClickEvent = MutableLiveData<Event<Unit>>()
    val enterRoomClickEvent: LiveData<Event<Unit>> = _enterRoomClickEvent

    val _roomId = MutableLiveData(0)
    val roomId: LiveData<Int> = _roomId

    val _name = MutableLiveData("unknown")

    var _userId = 0
    //val _userId = MutableLiveData(0)
    //val userId: LiveData<Int> = _userId

    private val repository: ChatRepository

    private val _allChats = MutableLiveData<List<Chat>>(listOf())
    val allChats: LiveData<List<Chat>> = _allChats


    //private val _allChatsByRoomId = MutableLiveData<List<Chat>>(listOf())
    //var allChatsByRoomId: LiveData<List<Chat>> = _allChatsByRoomId

    lateinit var mSocket: Socket

    var chatAdapter = ChatAdapter(0)


    init {

        try {
            mSocket = IO.socket("http://13.209.67.51:3000/chat")
            mSocket.connect()
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }

        val chatDao = ChatDatabase.getDatabase(application).chatDao()
        repository = ChatRepository(chatDao)

        //allChats = repository.allChats
    }


    fun onEnterRoomClick() {
        _enterRoomClickEvent.value = Event(Unit)
    }

    fun onMessageSendClick() {
        _messageSendClickEvent.value = Event(Unit)
    }

    fun insert(chat: Chat) = viewModelScope.launch {
        repository.insert(chat)

        addChat(chat)
    }

    fun addChat(chat : Chat) {
        val list = _allChats.value
        val arrayList = arrayListOf<Chat>()

        list?.let {
            for (i in list.withIndex()) {
                arrayList.add(list[i.index])
            }
        }

        arrayList.add(chat)

        _allChats.postValue(arrayList)
    }

    fun sendMessage(name: String, userId: Int, msg: String) {
        val data = JSONObject()
        try {
            data.put("name", name)
            data.put("roomId", _roomId.value)
            data.put("userId", userId)
            data.put("message", msg)
            mSocket.emit("chat message", data)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    fun enterRoom(name: String) {
        val data = JSONObject()
        try {
            data.put("name", name)
            data.put("roomId", _roomId.value)
            mSocket.emit("enter room", data)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    fun initAdapter() {
        chatAdapter =  ChatAdapter(_userId)
    }

    fun clearAdapter() {
        chatAdapter.submitList(listOf())
    }

    fun makeChat(message: String): Chat {
        return Chat(0, "", _roomId.value ?: 0, 0, message, DateUtil.curDate())
    }

    fun updateAdapterList() {
        Log.d("checkMessage", "arrived!!!!")


        val list = arrayListOf<Chat>()

        Log.d("checkMessage", "roomId : " + _roomId.value.toString())

        viewModelScope.launch {
            list.addAll(repository.allChatsByRoomId(_roomId.value ?: 0))
            Log.d("checkMessage", "list size : " + list.size.toString())
            chatAdapter.userId = _userId
            _allChats.postValue(list)
        }
    }
}