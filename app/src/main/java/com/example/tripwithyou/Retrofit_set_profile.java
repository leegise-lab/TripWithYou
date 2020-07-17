package com.example.tripwithyou;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface Retrofit_set_profile {

    //프로필 세팅 구현
    @GET("set_profile.php")
    Call<Retrofit_result_profile> getlist (@Query("id") String id);

}
