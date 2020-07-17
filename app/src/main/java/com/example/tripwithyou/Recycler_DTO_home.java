package com.example.tripwithyou;

public class Recycler_DTO_home {

    private String city;
    private String randmark;
    private String[] img;

    public Recycler_DTO_home(String city, String randmark, String[] img) {
        this.city = city;
        this.randmark = randmark;
        this.img = img;
    }

    //get 외부에서 get해옴
    public String getCity() {
        return city;
    }
    public String getRandmark() {
        return randmark;
    }
    public String[] getImg() { return img; }

    //외부에서 받은 텍스트에 넣음?
    public void setCity(String city) {
        this.city = city;
    }
    public void setRandmark(String randmark) { this.randmark = randmark; }
    public void setImg(String[] img) {
        this.img = img;
    }

}
