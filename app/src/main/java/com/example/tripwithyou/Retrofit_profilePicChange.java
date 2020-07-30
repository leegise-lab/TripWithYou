package com.example.tripwithyou;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface Retrofit_profilePicChange {
    //프사 구현

    // 프로필 이미지 보내기
    @GET("profile_change.php")
    Call<Retrofit_result> img(@Query("id") String id, @Query("img") String  img, @Query("nick") String nick, @Query("name")String name);
}
