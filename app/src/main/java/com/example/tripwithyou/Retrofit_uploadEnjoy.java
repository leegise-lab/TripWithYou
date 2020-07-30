package com.example.tripwithyou;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface Retrofit_uploadEnjoy {

    //즐길거리 정보 등록
    @GET("sample.php")
    //id라는 키로 id값을 보낸다, pw키로 pw밸류를 보낸다
    Call<Retrofit_result> getlist(@Query("country") String country, @Query("location") String location,
                                  @Query("website") String website, @Query("open") String open, @Query("name") String name,
                                  @Query("photo") String photo);

}
