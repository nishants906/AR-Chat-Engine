package com.example.nishant.ar_chat_engine;


import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by samarthgupta on 05/04/17.
 */

public interface DataInterface {

    // Get chat user
    @GET("chat/users/?")
    Call<List<ChatUser>> getchatuser(@Header("Authorization") String token, @Query("page") String pagenumber);


    // Get users message
    @GET("chat/messages/{userID}/list/?")
    Call<Usermessages> getusermessage(@Header("Authorization")String token, @Path("userID") String storeID,@Query("page") String pagenumber);


    // Post for create message in chat
    @POST("chat/messages/create/")
    Call<MessageCreate> createmessage(@Header("Authorization") String token, @Body MessageCreate messageCreate);


}