package com.example.tripwithyou;

import java.util.List;

public class Retrofit_result_home {
    //php에서 받는 부분
    //난 php에서 result만 받겠다.
    private final String result;
    private final List<TestDto> array;
    public Retrofit_result_home(String result, List<TestDto> array) {
        this.result = result;
        this.array = array;
    }
    public String getResult() {
        return result;
    }
    public List<TestDto> getArray() {return array;}
}
