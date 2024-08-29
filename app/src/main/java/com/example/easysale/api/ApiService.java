package com.example.easysale.api;

import com.example.easysale.data.ManageUser;
import com.example.easysale.data.Result;
import com.example.easysale.data.User;
import com.example.easysale.data.UserResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @GET("users")
    Call<Result> getPageData(@Query("page") String pageNumber);

    @GET("users/{id}")
    Call<UserResponse> getUser(@Path("id") String id);

    @FormUrlEncoded
    @POST("register")
    Call<ManageUser> registerNewUser(@Field("email") String email, @Field("password") String password);

    @FormUrlEncoded
    @POST("login")
    Call<ManageUser> loginUser(@Field("email") String email, @Field("password") String password);
    @FormUrlEncoded
    @POST("users/{id}")
    Call<ManageUser> updateUser(@Path("id") String id,@Field("name") String name, @Field("job") String job);

    @FormUrlEncoded
    @POST("users")
    Call<ManageUser> createUser(@Field("name") String name, @Field("job") String job);
}
