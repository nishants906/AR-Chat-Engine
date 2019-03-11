
package com.example.nishant.ar_chat_engine;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ChatUser {

    @SerializedName("profile")
    @Expose
    private Profile profile;
    @SerializedName("messages")
    @Expose
    private Messages messages;

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public Messages getMessages() {
        return messages;
    }

    public void setMessages(Messages messages) {
        this.messages = messages;
    }

}
