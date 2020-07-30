package com.example.tripwithyou;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface Retrofit_home {

    //PHP에 전송하는 부분
    @GET("home.php")
    //id라는 키로 id값을 보낸다, pw키로 pw밸류를 보낸다
    Call<Retrofit_result_home> getlist(@Query("home") String home);

}
