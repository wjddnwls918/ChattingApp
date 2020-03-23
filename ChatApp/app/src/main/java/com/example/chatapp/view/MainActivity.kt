package com.example.chatapp.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import com.example.chatapp.common.EventObserver
import com.example.chatapp.databinding.ActivityMainBinding
import com.example.chatapp.model.Chat
import com.google.gson.Gson
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONException
import org.json.JSONObject
import java.net.ContentHandlerFactory
import java.net.URISyntaxException

class MainActivity : AppCompatActivity() {

    //private lateinit var mSocket: Socket
    private lateinit var binding: ActivityMainBinding

    private lateinit var viewModelFactory: ViewModelProvider.AndroidViewModelFactory
    private lateinit var viewModel: MainViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this

        viewModelFactory = ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java).apply {
            binding.viewmodel = this
        }

      //  initRecyclerView()
        initSocket()
        setupNavigation()
        initObserve()
    }

    /*fun initRecyclerView() {
        viewModel.initAdapter()
    }*/

    fun initObserve() {

        val smoothScroller:RecyclerView.SmoothScroller by lazy {
            object: LinearSmoothScroller(this) {
                override fun getVerticalSnapPreference() = SNAP_TO_START
            }
        }

        viewModel._roomId.observe(this, Observer {
            //binding.tvMainChat.text = ""
            viewModel.clearAdapter()
        })

        viewModel.allChats.observe(this, Observer {
            if (it.size != 0) {
                Log.d("checkMessage", "name : " + it[0].name)
                Log.d("checkMessage", "size : " + it.size.toString())

                //binding.rcvChat.scrollToPosition(it.size-1)

                smoothScroller.targetPosition = it.size-1
                binding.rcvChat.layoutManager?.startSmoothScroll(smoothScroller)
            }
        })
/*
        viewModel.chatAdapter.registerAdapterDataObserver(object: RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                binding.rcvChat.scrollToPosition(itemCount-1)
            }
        })
 */








    }

    private fun isWhiteSpace(): Boolean {
        when {
            binding.etInputName.text.toString().equals("") || binding.etInputRoom.text.toString()
                .equals(
                    ""
                ) ||
                    binding.etInputUserId.text.toString().equals("") -> {
                makeToast("입력을 확인해주세요")
                return false
            }
            else -> return true
        }
    }

    private fun makeToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    private fun initSocket() {
        viewModel.mSocket.on("enter room", onEnterRoom)
        viewModel.mSocket.on("chat message", onMessageReceived)
    }

    private val onEnterRoom = Emitter.Listener {
        runOnUiThread {
            val receivedData = it[0]
            //binding.tvMainChat.append(receivedData.toString() + "\n")
            Log.d("checkMessage", receivedData.toString())

            viewModel.insert(viewModel.makeChat(receivedData.toString()))

        }
    }

    private val onMessageReceived = Emitter.Listener {

        runOnUiThread {

            Log.d("checkMessage", it[0].toString())
            val gson = Gson()

            val chat = gson.fromJson(it[0].toString(), Chat::class.java)

            Log.d("checkMessage", chat.toString())
            //binding.tvMainChat.append(chat.name + " : " +chat.message +"\n")

            viewModel.insert(chat)

        }
    }

    private fun setupNavigation() {

        //Send Message
        viewModel.messageSendClickEvent.observe(this, EventObserver {

            when {
                viewModel._roomId.value == 0 -> {
                    makeToast("방에 입장해주세요")
                }
                binding.etInputMsg.text.toString().equals("") -> {
                    makeToast("메시지를 입력하세요")
                }
                else -> {
                    viewModel.sendMessage(
                        binding.etInputName.text.toString(),
                        binding.etInputUserId.text.toString().toInt(),
                        binding.etInputMsg.text.toString()
                    )

                    binding.etInputMsg.setText("")
                }

            }

        })

        //Join Room
        viewModel.enterRoomClickEvent.observe(this, EventObserver {


            if (isWhiteSpace() && !viewModel._roomId.value.toString()
                    .equals(binding.etInputRoom.text.toString())
            ) {
                viewModel._name.value = binding.etInputName.text.toString()
                viewModel._roomId.value = binding.etInputRoom.text.toString().toInt()
                viewModel._userId = binding.etInputUserId.text.toString().toInt()

                viewModel.enterRoom(binding.etInputName.text.toString())

                viewModel.updateAdapterList()
            }

        })

    }
}
