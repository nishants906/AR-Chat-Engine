
package com.example.nishant.ar_chat_engine;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Usermessages {

    @SerializedName("profile")
    @Expose
    private Profile profile;

    @SerializedName("blocked")
    @Expose
    private Boolean block;

    @SerializedName("can_unblock")
    @Expose
    private Boolean can_block;

    @SerializedName("messages")
    @Expose
    private List<Messages> messages;

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public List<Messages> getMessages() {
        return messages;
    }

    public void setMessages(List<Messages> messages) {
        this.messages = messages;
    }

    public Boolean getCan_block() {
        return can_block;
    }

    public void setCan_block(Boolean can_block) {
        this.can_block = can_block;
    }

    public Boolean getBlock() {
        return block;
    }

    public void setBlock(Boolean block) {
        this.block = block;
    }
}
