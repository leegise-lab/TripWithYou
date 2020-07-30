package com.example.tripwithyou;

import java.util.List;

public class Retrofit_result_home {
    //php에서 받는 부분
    //난 php에서 result만 받겠다.
    private final String result;
    private final List<Recycler_DTO_home_Vertical> array;
    public Retrofit_result_home(String result, List<Recycler_DTO_home_Vertical> array) {
        this.result = result;
        this.array = array;
    }
    public String getResult() {
        return result;
    }
    public List<Recycler_DTO_home_Vertical> getArray() {return array;}
}
