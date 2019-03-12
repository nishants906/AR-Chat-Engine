
package com.example.nishant.ar_chat_engine;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MessageCreate {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("sender")
    @Expose
    private Integer sender;
    @SerializedName("receiver")
    @Expose
    private Integer receiver;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("timestamp")
    @Expose
    private String timestamp;

    public MessageCreate(Integer id, String message) {
        this.receiver = id;
        this.message = message;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSender() {
        return sender;
    }

    public void setSender(Integer sender) {
        this.sender = sender;
    }

    public Integer getReceiver() {
        return receiver;
    }

    public void setReceiver(Integer receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

}
