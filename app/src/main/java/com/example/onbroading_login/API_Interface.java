package com.example.onbroading_login;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface API_Interface {
    @FormUrlEncoded
    @POST("token") // Replace "login" with your API endpoint
    Call<HelperClass> login(
            @Field("client_id") String client_id,
            @Field("username") String username,
            @Field("password") String password,
            @Field("grant_type") String grant_type
    );
}
