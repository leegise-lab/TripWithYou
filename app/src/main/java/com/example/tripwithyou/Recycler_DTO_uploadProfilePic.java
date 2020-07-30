package com.example.tripwithyou;


public class Recycler_DTO_uploadProfilePic {

    private String img;

    public Recycler_DTO_uploadProfilePic(String img) {
        this.img = img;
    }

    //get 외부에서 get해옴
    public String getImg() {
        return img;
    }

    //외부에서 받은 텍스트에 넣음?
    public void setImg(String img) {
        this.img = img;
    }


}
