package com.example.tripwithyou;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface Retrofit_login {

    //로그인 구현
    @GET("login.php")
    Call<Retrofit_result> getlist(@Query("id") String id, @Query("pw") String pw);

}
