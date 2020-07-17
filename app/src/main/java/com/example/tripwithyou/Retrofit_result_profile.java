package com.example.tripwithyou;

public class Retrofit_result_profile {
    //php에서 받는 부분
    //난 php에서 result만 받겠다.
    private final String nick;
    private final String name;
    private final String img;
    private final String grade;
    public Retrofit_result_profile(String nick, String name, String img, String grade) {
        this.nick = nick;
        this.name = name;
        this.img = img;
        this.grade = grade;
    }

    public String getNick() {
        return nick;
    }
    public String getName() {
        return name;
    }
    public String getImg() {
        return img;
    }
    public String getGrade() { return grade; }
}
