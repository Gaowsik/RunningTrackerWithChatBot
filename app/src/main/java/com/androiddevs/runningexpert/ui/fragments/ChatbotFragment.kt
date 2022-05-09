package com.androiddevs.runningexpert.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.androiddevs.runningexpert.R
import com.androiddevs.runningexpert.adapters.ChatAdapter
import com.androiddevs.runningexpert.db.Message
import com.google.android.material.snackbar.Snackbar
import com.google.api.gax.core.FixedCredentialsProvider
import com.google.auth.oauth2.GoogleCredentials
import com.google.auth.oauth2.ServiceAccountCredentials
import com.google.cloud.dialogflow.v2.*
import kotlinx.android.synthetic.main.fragment_chatbot.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class ChatbotFragment: Fragment(R.layout.fragment_chatbot) {

   private var messageList: ArrayList<Message> = ArrayList()

    //dialogFlow
    private var sessionsClient: SessionsClient? = null
    private var sessionName: SessionName? = null
    private val uuid = UUID.randomUUID().toString()
    private val TAG = "mainactivity"
    private lateinit var chatAdapter: ChatAdapter


    @SuppressLint("LogNotTimber")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



               setupRecyclerView()


        //setting adapter to recyclerview
        chatAdapter = ChatAdapter(messageList)
        chatView.adapter = chatAdapter

        //onclick listener to update the list and call dialogflow
        btnSend.setOnClickListener {


            val message: String = editMessage.text.toString()
           if (message.isNotEmpty()) {
                addMessageToList(message, false)
                Log.d(TAG, "projectId : $messageList")
                sendMessageToBot(message)


            }
        else {
                Snackbar.make(view, "Please enter text!", Snackbar.LENGTH_SHORT).show()
            }
        }



        //initialize bot config
        setUpBot()


    }



    @SuppressLint("NotifyDataSetChanged")
    private fun addMessageToList(message: String, isReceived: Boolean) {
        messageList.add(Message(message, isReceived))
        editMessage.setText("")
        chatAdapter.notifyDataSetChanged()
        chatView.layoutManager?.scrollToPosition(messageList.size - 1)
    }

    private fun setUpBot() {
        try {
            val stream = this.resources.openRawResource(R.raw.credential)
            val credentials: GoogleCredentials = GoogleCredentials.fromStream(stream)
                .createScoped("https://www.googleapis.com/auth/cloud-platform")
            val projectId: String = (credentials as ServiceAccountCredentials).projectId
            val settingsBuilder: SessionsSettings.Builder = SessionsSettings.newBuilder()
            val sessionsSettings: SessionsSettings = settingsBuilder.setCredentialsProvider(
                FixedCredentialsProvider.create(credentials)
            ).build()
            sessionsClient = SessionsClient.create(sessionsSettings)
            sessionName = SessionName.of(projectId, uuid)
            Log.d(TAG, "projectId : $projectId")
        } catch (e: Exception) {
            Log.d(TAG, "setUpBot: " + e.message)
        }
    }

    private fun sendMessageToBot(message: String) {
        val input = QueryInput.newBuilder()
            .setText(TextInput.newBuilder().setText(message).setLanguageCode("en-US")).build()
        GlobalScope.launch {
            sendMessageInBg(input)
        }
    }

    private suspend fun sendMessageInBg(
        queryInput: QueryInput
    ) {
        withContext(Dispatchers.Default) {
            try {
                val detectIntentRequest = DetectIntentRequest.newBuilder()
                    .setSession(sessionName.toString())
                    .setQueryInput(queryInput)
                    .build()
                val result = sessionsClient?.detectIntent(detectIntentRequest)
                if (result != null) {
                    requireActivity()?.runOnUiThread {
                        updateUI(result)
                    }
                }
            } catch (e: java.lang.Exception) {
                Log.d(TAG, "doInBackground: " + e.message)
                e.printStackTrace()
            }
        }
    }

    private fun updateUI(response: DetectIntentResponse) {
        val botReply: String = response.queryResult.fulfillmentText
        if (botReply.isNotEmpty()) {
            addMessageToList(botReply, true)
        } else {
            Snackbar.make(requireView(), "something went wrong", Snackbar.LENGTH_SHORT).show()
        }
    }
    private fun setupRecyclerView() = chatView.apply {
        chatAdapter = ChatAdapter(messageList)
        adapter = chatAdapter

    }


}