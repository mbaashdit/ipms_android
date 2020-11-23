package com.aashdit.ipms.network;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by Manabendu on 29/05/20
 */
public interface Api {
    //server :: 167.172.243.181
    //local ::  192.168.3.139

    public static String PATH = "ipms/api/";

    String IP = "http://192.168.3.139:4848/";

    @GET("https://itunes.apple.com/search?")
    Call<String> getSong(@Query("term") String term, @Query("country") String country);

    @GET("http://test.in/api/tree.json")
    Call<String> getData();

    @FormUrlEncoded
    @POST(PATH+"moblogin")
    Call<String> agencyUserLogin(@Field("userName") String userName,
                                 @Field("password") String password,
                                 @Field("ip") String ip);

    @GET(PATH+"getProjectDetailsByUserId")
    Call<String> getProjectDetailsByUserId(@Query("userId")String userId,
                                           @Query("token")String token);
}
