package com.example.tripwithyou;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface Retrofit_sign_up {
    //회원가입
    //PHP에 전송하는 부분
    @GET("sign_up.php")
    //id라는 키로 id값을 보낸다, pw키로 pw밸류를 보낸다
    Call<Retrofit_result> getlist(@Query("id") String id, @Query("pw") String pw,
                                  @Query("nick") String nick, @Query("grade")String grade, @Query("name") String name);

}
