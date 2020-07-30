package com.example.tripwithyou;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Edit_profile extends AppCompatActivity {
    EditText nick, name;
    ImageView img;
    final String BASE_URL = "http://13.125.246.30/";
    private Call<Retrofit_result> userList;
    private Retrofit mRetrofit;
    private Retrofit_profilePicChange retrofitInterface;
    private Gson mGson;
    long t = System.currentTimeMillis();
    String img_url, mCurrentPhotoPath, savedPhoto;
    private File tempFile;
    private static final int PICK_FROM_ALBUM = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.edit_profile);

        //디스플레이 창 크기 조절
        DisplayMetrics dm = getApplicationContext().getResources().getDisplayMetrics();
        int width = (int) (dm.widthPixels * 0.8); // Display 사이즈의 90%
        int height = (int) (dm.heightPixels * 0.8); // Display 사이즈의 90%
        getWindow().getAttributes().width = width;
        getWindow().getAttributes().height = height;

        set_profile();

        pick_photo();
        click_btn();
    }

    //프사, 이름, 별명 보여주는 부분
    public void set_profile() {
        Log.d("프로필 세팅", "set_profile 실행");
        //쉐어드에서 유저정보 가져옴.
        SharedPreferences sp = getSharedPreferences("user",MODE_PRIVATE);
        String setimg = sp.getString("img", null);
        String setnick = sp.getString("nick", null);
        String setname = sp.getString("name", null);
        nick = findViewById(R.id.edit_nick);
        name = findViewById(R.id.edit_name);
        img = findViewById(R.id.edit_profile_img);
        img.setBackground(new ShapeDrawable(new OvalShape()));
        img.setClipToOutline(true);
        Log.d("사진", ""+setimg);
        Log.d("닉", ""+setnick);
        Log.d("이름",""+setname);
        nick.setText(setnick);
        name.setText(setname);
        if (setimg == null) {
            img.setImageDrawable(getResources().getDrawable(R.drawable.ic_date_range_black_24dp, getApplicationContext().getTheme()));
        } else {
            Glide.with(Edit_profile.this).load(setimg).into(img);
        }

    }

    //사진 변경 눌렀을때 권한 체크부분
    public void pick_photo() {
        img = (ImageView)findViewById(R.id.edit_profile_img);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tedPermission();
            }
        });
    }

    //권한 체크
    private void tedPermission() {
        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                // 권한 요청 성공
                goToAlbum();
            }
            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                // 권한 요청 실패
                Toast.makeText(getApplicationContext(), "[설정] > [권한]에서 권한을 허용할 수 있습니다.", Toast.LENGTH_SHORT).show();
            }
        };
        TedPermission.with(this)
                .setPermissionListener(permissionListener)
                .setRationaleMessage("사진 및 파일을 저장하기 위하여 접근 권한이 필요합니다.")
                .setDeniedMessage("[설정] > [권한] 에서 권한을 허용할 수 있습니다.")
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .check();
    }
    //갤러리 이동
    private void goToAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }
    //갤러리에서 돌아왔을때
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("onActivityResult", "실행");
        //갤러리에서  돌아왔을때
        if (requestCode == PICK_FROM_ALBUM) {
            if (data == null) {
                Log.d("갤러리", "사진 선택 X");
            } else {
                File file = null;
                try {
                    file = createImageFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(file != null) {
//                    Uri albumURI = Uri.fromFile(file);
                }
                Uri photoUri = data.getData();
                Log.d("갤러리", "사진 선택");
                Cursor cursor = null;
                try {
                    /*
                     *  Uri 스키마를
                     *  content:/// 에서 file:/// 로  변경한다.
                     */
                    String[] proj = { MediaStore.Images.Media.DATA };

                    assert photoUri != null;
                    cursor = getContentResolver().query(photoUri, proj, null, null, null);

                    assert cursor != null;
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    cursor.moveToFirst();
                    tempFile = new File(cursor.getString(column_index));
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
            }
            setImage();
            uploadFile(""+tempFile);
        }
    }
    //갤러리에서 선택한 이미지 세팅부분
    private void setImage() {
        img = findViewById(R.id.edit_profile_img);
        img.setBackground(new ShapeDrawable(new OvalShape()));
        img.setClipToOutline(true);
        BitmapFactory.Options options = new BitmapFactory.Options();
        //이미지 선택 안했을때
        if (tempFile == null) {
            Log.d("이미지", "tempFile == null");
//            Drawable drawable = getResources().getDrawable(R.drawable.ic_people_outline_black_24dp);
//            img.setImageDrawable(drawable);

            //이미지 선택 했을때
        } else if (tempFile != null) {
            Bitmap originalBm = BitmapFactory.decodeFile(tempFile.getAbsolutePath(), options);
            Log.d("이미지 주소", ""+originalBm);
            img.setImageBitmap(originalBm);
            img_url = originalBm.toString();

        }
    }


    //수정 완료버튼 눌렀을때
    public void click_btn() {
        Button button = (Button)findViewById(R.id.edit_btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("버튼", "수정완료 버튼 클릭");
                setRetrofitInit();
                calluserlist();
            }
        });
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        String imageFileName = timeStamp + ".jpg";
        File storageDir = new File(Environment.getExternalStorageDirectory().getPath()+"/pathvalue/"+imageFileName);

        mCurrentPhotoPath = storageDir.getAbsolutePath();
        Log.i("mCurrentphotopath", mCurrentPhotoPath);
        return storageDir;
    }

    public class UploadFile extends AsyncTask<String, String, String> {
        Context context;
        ProgressDialog dialog;
        String filename;

        HttpURLConnection conn = null;//네트워크 연결 객체
        DataOutputStream dos = null; //서버 전송시 데이터 작성한 뒤 전송

        String lineEnd = "\r\n";//구분자
        String twoHyphens = "--";
        String boundary = "*****";

        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1024;
        File sourceFile;
        int serverResponseCode;
        String TAG = "FileUpload";

        public UploadFile(Context context) {
            this.context = context;
        }
        public void setPath(String uploadFilePath) {
            this.filename= uploadFilePath;
            this.sourceFile = new File(uploadFilePath);
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            dialog = new ProgressDialog(context);
            dialog.setMessage("업로드 중");
            dialog.setCanceledOnTouchOutside(false);
            dialog.setIndeterminate(false);
            dialog.show();
        }

        //이미지 업로드 완료되는 부분
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dialog.dismiss();
        }

        @Override
        protected String doInBackground(String... Strings) {
            if(!sourceFile.isFile()) {
                Log.e(TAG, "sourceFile(" + filename + ") is Not A file");
                return null;
            } else {
                String result = "";
                Log.i(TAG, "sourceFile(" + filename + ") is A file");
                try{
                    FileInputStream fileInputStream = new FileInputStream(sourceFile);
                    URL url = new URL(Strings[0]);
                    Log.i("Strings[0]", Strings[0]);

                    conn = (HttpURLConnection)url.openConnection();
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setUseCaches(false);
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Connection", "Keep-Alive");
                    conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                    conn.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);
                    conn.setRequestProperty("uploaded_file", filename);
                    Log.i(TAG, "fileName : " +filename);

                    dos = new DataOutputStream(conn.getOutputStream());

                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"data1\"" + lineEnd);

                    dos.writeBytes(lineEnd);
                    dos.writeBytes("newImage");
                    dos.writeBytes(lineEnd);

                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\"; filename=\"" + filename + "\"" +lineEnd);
                    dos.writeBytes(lineEnd);

                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    buffer = new byte[bufferSize];

                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                    while (bytesRead > 0) {
                        dos.write(buffer, 0, bufferSize);
                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                    }

                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                    serverResponseCode = conn.getResponseCode();
                    String serverResponseMessage = conn.getResponseMessage();
                    Log.i(TAG, "[서버 이미지 업로드] HTTP Response is : " + serverResponseMessage + ": " + serverResponseCode);

                    if (serverResponseCode == 200) {

                    }
                    BufferedReader rd = null;
                    rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                    String line = null;
                    while ((line = rd.readLine()) != null) {
                        Log.i("Upload State",line);
                        savedPhoto = "http://13.125.246.30/"+line;
                        SharedPreferences sp = getSharedPreferences("user",MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("img", savedPhoto);
                        editor.commit();
                        Log.d("savePhoto",savedPhoto);

                    }

                    fileInputStream.close();
                    dos.flush();
                    dos.close();


                } catch (Exception e) {
                    Log.e(TAG + " Error", e.toString());
                }
                return result;

            }
        }
    }
    //php에서 작업하는 부분
    public void uploadFile(String filePath) {
        String url = "http://13.125.246.30/upload_picture.php";
        try {
            UploadFile uploadFile = new UploadFile(Edit_profile.this);
            uploadFile.setPath(filePath);
            uploadFile.execute(url);
        } catch (Exception e) {
        }
    }


    //레트로핏 객체생성
    protected void setRetrofitInit() {
        mGson = new GsonBuilder().setLenient().create();
        mRetrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(mGson))
                .build();
        retrofitInterface = mRetrofit.create(Retrofit_profilePicChange.class);
    }
    protected void calluserlist()  {
        SharedPreferences sp = getSharedPreferences("user",MODE_PRIVATE);
        String setid = sp.getString("id", null);
        String setimg = sp.getString("img", null);
        nick = (EditText)findViewById(R.id.edit_nick);
        name = (EditText)findViewById(R.id.edit_name);
        String setnick = nick.getText().toString();
        String setname = name.getText().toString();

        SharedPreferences.Editor editor = sp.edit();
        editor.putString("name", setname);
        editor.putString("nick", setnick);
        editor.commit();


        userList = retrofitInterface.img(setid, setimg, setnick, setname);
        userList.enqueue(mRetrofitCallback);
    }
    protected Callback<Retrofit_result> mRetrofitCallback = new Callback<Retrofit_result>() {
        @Override
        public void onResponse(Call<Retrofit_result> call, Response<Retrofit_result> response) {
            Retrofit_result retrofit_result = response.body();
            String result = retrofit_result.getResult();
            Log.d("결과", result);
            if (result.equals("success")) {
                finish();
            } else if (result.contains("fail")) {
                Toast.makeText(Edit_profile.this,"프로필 변경을 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        }
        @Override
        public void onFailure(Call<Retrofit_result> call, Throwable t) {
            t.printStackTrace();
        }
    };

}
