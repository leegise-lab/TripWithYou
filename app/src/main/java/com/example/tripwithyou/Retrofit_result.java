package com.example.tripwithyou;

import java.util.ArrayList;

public class Retrofit_result {
    //php에서 받는 부분
    //난 php에서 result만 받겠다.
    private final String result;

    public Retrofit_result(String result) {
        this.result = result;

    }

    public String getResult() {
        return result;
    }

}
