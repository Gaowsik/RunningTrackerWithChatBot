package com.androiddevs.runningexpert.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.androiddevs.runningexpert.R
import com.androiddevs.runningexpert.db.Message


class ChatAdapter(private var messageList1: List<Message>) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

  class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var messageReceive: TextView = itemView.findViewById(R.id.message_receive)
    var messageSend: TextView = itemView.findViewById(R.id.message_send)
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
    return ChatViewHolder (LayoutInflater.from(parent.context).inflate(R.layout.adapter_message_one, parent, false))

  }

  override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
    val message: String = messageList1[position].message
    val isReceived: Boolean = messageList1[position].isReceived
    if (isReceived) {
      holder.messageReceive.visibility = View.VISIBLE
      holder.messageSend.visibility = View.GONE
      holder.messageReceive.text = message
    } else {
      holder.messageSend.visibility = View.VISIBLE
      holder.messageReceive.visibility = View.GONE
      holder.messageSend.text = message
    }
  }

  override fun getItemCount(): Int {
    return messageList1.count()
  }
}