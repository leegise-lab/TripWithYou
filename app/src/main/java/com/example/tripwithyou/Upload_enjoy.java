package com.example.tripwithyou;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class Upload_enjoy extends AppCompatActivity {
    EditText ename, elocation, eopen, ecountry, ewebsite;
    RecyclerView rv;
    String check = "x";
    private File tempFile;
    Adapter_upload_pic adapter;
    ArrayList<Recycler_DTO_uploadProfilePic> datalist = new ArrayList<>();

    private static final int PICK_FROM_ALBUM = 1;

    final String BASE_URL = "http://13.125.246.30/";
    Retrofit_uploadEnjoy retrofitInterface;
    private Call<Retrofit_result> userList;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("datalist", ""+datalist);
        setContentView(R.layout.upload_enjoy);
        rv = (RecyclerView)findViewById(R.id.recycler);
        //아이템 보여주는거 크기 일정하게
        rv.setHasFixedSize(true);
        //리사이클러뷰에 리사이클러뷰 매니저붙임 // 가로로 붙임
        rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        adapter = new Adapter_upload_pic(Upload_enjoy.this, datalist);
        rv.setAdapter(adapter);

        //카테고리 스피너로 구현하는 부분
        ArrayList<String> items = new ArrayList();
        items.add("랜드마크");
        items.add("쇼핑");
        items.add("야외 활동");
        items.add("전시관");
        items.add("기타");
        Spinner sp = (Spinner) findViewById(R.id.enjoy_category);
        //스피너에 제목 달아주는 부분
        ArrayAdapter adapter = new ArrayAdapter<String>(this,
                R.layout.support_simple_spinner_dropdown_item, items) {
            //getView 오버라이딩
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                if (position == getCount()) {
                    ((TextView)v.findViewById(android.R.id.text1)).setText("아왜요ㅡㅡ");
                    ((TextView)v.findViewById(android.R.id.text1)).setHint(getCount());
                }
                return v;
            }
//            //getCount 오버라이딩
            public int getCount() {
                return super.getCount() ;
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(adapter);


        //사진 추가, 등록버튼 클릭
        addPic();
        upload();
    }

    //리사이클러뷰 사진 없을때 버튼 gone처리 하는 부분
    @Override
    public void onResume(){
        super.onResume();
        Log.d("onResume", "check : "+check);
        check();
    }
    public void check() {
        if (check == "x") {
            Log.d("onResume", "check : "+check);
            rv.setVisibility(View.GONE);
            TextView textView = (TextView)findViewById(R.id.text);
            textView.setVisibility(View.GONE);
        } else if (check == "ok") {
            Log.d("onResume", "check : "+check);
            rv.setVisibility(View.VISIBLE);
            TextView textView = (TextView)findViewById(R.id.text);
            textView.setVisibility(View.VISIBLE);
        }
    }

    //사진 추가 눌렀을때 권한 체크 부분
    public void addPic() {
        Button button = (Button) findViewById(R.id.addPic);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("버튼", "사진 추가 버튼 클릭");
                tedPermission();
            }
        });
    }
    //업로드(등록하기) 버튼 클릭
    public void upload() {
        Button button = (Button)findViewById(R.id.upload);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setRetrofitInit();
                calluserlist();
                //아까 서버에 올린 사진 경로 쉐어드에서 삭제하기
                SharedPreferences sp = getSharedPreferences("upload", MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.clear();
                editor.commit();
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
        //사진을 여러개 선택할수 있도록 한다
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }
    //갤러리에서 돌아왔을때
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        ArrayList<File> files = new ArrayList();
        ArrayList<String> filesUri = new ArrayList();

        Log.d("onActivityResult", "실행");
        if (requestCode == PICK_FROM_ALBUM) {
            if (data == null) {
                Log.d("갤러리", "사진 선택 X");
            } else {
//                File file = null;
                ClipData clipData = data.getClipData();
                if (data.getClipData() == null) {
                    Toast.makeText(getApplicationContext(), "다중선택이 불가능한 기기입니다.", Toast.LENGTH_SHORT).show();
                } else if (clipData != null) {
                    Cursor cursor = null;
                    //파일 어레이리스트 초기화. 안해주면 이전에 선택했던 파일명이 다 담김
                    files.clear();
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        ClipData.Item item = clipData.getItemAt(i);
                        Uri uri = item.getUri();
                        Log.e("갤러리 다중선택", " : " + uri.toString());
                        String path = String.valueOf(uri);
                        Log.d("path", path);
                        //리사이클러뷰에 선택한 이미지 넣음
                        Recycler_DTO_uploadProfilePic dto = new Recycler_DTO_uploadProfilePic(path);
                        Log.d("dto", dto+"");
                        datalist.add(new Recycler_DTO_uploadProfilePic(path));
                        adapter.notifyDataSetChanged();
                        check = "ok";
//                        Cursor cursor = null;
                        try {
                            /*
                             *  Uri 스키마를
                             *  content:/// 에서 file:/// 로  변경한다.
                             */
                            String[] proj = { MediaStore.Images.Media.DATA };
//                    assert photoUri != null;
                            Log.d("proj", ""+proj);
                            cursor = getContentResolver().query(uri, proj, null, null, null);
                            assert cursor != null;
                            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                            cursor.moveToFirst();

                            tempFile = new File(cursor.getString(column_index));
                            files.add(tempFile);
                            filesUri.add(""+tempFile);
                        } finally {
                            if (cursor != null) {
                                cursor.close();

                            }
                        }
                    }
                    uploadFile(filesUri);
                    Log.d("files", ""+filesUri);
                }
            }
        }
    }

    public class UploadFile extends AsyncTask<String, String, String> {
        Context context;
        ProgressDialog dialog;
        ArrayList<String> filename;

        HttpURLConnection conn = null;//네트워크 연결 객체
        DataOutputStream dos = null; //서버 전송시 데이터 작성한 뒤 전송

        String lineEnd = "\r\n";//구분자
        String twoHyphens = "--";
        String boundary = "*****";

        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1024 * 1024;
        ArrayList<File> sourceFile = new ArrayList();
//        File sourceFile;
        int serverResponseCode;
        String TAG = "FileUpload";

        public UploadFile(Context context) {
            this.context = context;
        }
        public void setPath(ArrayList<String> uploadFilePath) {
            Log.d("setPath","시작");
            this.filename = uploadFilePath;
            for (int i=0; i<uploadFilePath.size(); i++) {
                this.sourceFile.add(new File("" + uploadFilePath.get(i)));
            }
//            this.sourceFile = new File[]{new File(""+uploadFilePath)};
//            this.sourceFile = new File(uploadFilePath);
            Log.d("filename", ""+filename);

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
            Log.d("Doinbackgroung", "시작");

            //선택한 사진들의 경로를 쉐어드에 저장하는 부분
            //사진 선택만 해놓고 종료하는 경우를 대비해서 사진을 선택할때 그 전에 있던 사진 경로는 삭제함.
            SharedPreferences sp = getSharedPreferences("upload", MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.clear();
            editor.commit();
            for (int i = 0; i < sourceFile.size(); i++) {
                try {
                    FileInputStream fileInputStream = new FileInputStream(filename.get(i));
                    URL url = new URL(Strings[0]);
                    Log.i("Strings[0]", Strings[0]);

                    conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setUseCaches(false);
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Connection", "Keep-Alive");
                    conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                    conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                    conn.setRequestProperty("uploaded_file", filename.get(i));
                    Log.i(TAG, "fileName : " + filename.get(i));

                    dos = new DataOutputStream(conn.getOutputStream());

                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"data1\"" + lineEnd);

                    dos.writeBytes(lineEnd);
                    dos.writeBytes("newImage");
                    dos.writeBytes(lineEnd);

                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\"; filename=\"" + filename.get(i) + "\"" + lineEnd);
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
                        Log.i("Upload State", line);
                        String savedPhoto = "http://13.125.246.30/" + line;
                        long time = System.currentTimeMillis();
                        editor.putString(time + "img", savedPhoto);
                        editor.commit();
                        Log.d("savePhoto", savedPhoto);
                    }
                    dos.close();
                    rd.close();
                }catch (IOException e) {
                    e.printStackTrace();
                }

            }

            return null;
        }
    }
    //php에서 작업하는 부분
    public void uploadFile(ArrayList<String> filePath) {
        Log.d("uploadfile", "시작");
        String url = "http://13.125.246.30/upload_picture.php";
        ArrayList<String> path = new ArrayList();
        try {
            UploadFile uploadFile = new UploadFile(Upload_enjoy.this);
            for (int i=0; i<filePath.size(); i++) {
                path.add(filePath.get(i));
            }
            uploadFile.setPath(path);
            uploadFile.execute(url);
            Log.d("파일패스", ""+path);
        } catch (Exception e) {
        }
    }

    //레트로핏 부분
    protected void setRetrofitInit() {
        Gson mGson = new GsonBuilder().setLenient().create();
        Retrofit mRetrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(mGson))
                .build();

        retrofitInterface = mRetrofit.create(Retrofit_uploadEnjoy.class);
    }

    protected void calluserlist() {
        ename = (EditText)findViewById(R.id.enjoy_name);
        elocation = (EditText)findViewById(R.id.enjoy_location);
        eopen = (EditText)findViewById(R.id.enjoy_open);
        ecountry = (EditText)findViewById(R.id.enjoy_country);
        ewebsite = (EditText)findViewById(R.id.enjoy_web);

        String name = ename.getText().toString();
        String location = elocation.getText().toString();
        String open = eopen.getText().toString();
        String country = ecountry.getText().toString();
        String website = ewebsite.getText().toString();

        if (name == null ||location == null ||open == null ||country == null ||website == null) {
            Toast.makeText(Upload_enjoy.this, "빈 항목이 있습니다.", Toast.LENGTH_SHORT).show();
        } else {
            ArrayList list = new ArrayList();
            SharedPreferences sp = getSharedPreferences("upload", MODE_PRIVATE);
            Collection<?> col =  sp.getAll().values();
            Iterator<?> it = col.iterator();
            while(it.hasNext())
            {
                String msg = (String)it.next();
                Log.d("Result", msg);
                list.add(msg);
            }

            String getPhotoList = String.valueOf(list);
            Log.d("photo", getPhotoList);
            String sgetPhotoList = getPhotoList.replaceFirst("\\[", "");
            String photo = sgetPhotoList.replaceFirst("\\]", "");
            Log.d("photo", photo);

            if (photo.equals("") || photo == null) {
                Log.d("사진 선택 X", "업로드 완료");
                AlertDialog.Builder builder = new AlertDialog.Builder(Upload_enjoy.this);
                builder.setMessage("사진을 등록하지 않으셨습니다. 이대로 등록할까요?");
                //네 버튼 클릭
                builder.setPositiveButton("네", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String name = ename.getText().toString();
                        String location = elocation.getText().toString();
                        String open = eopen.getText().toString();
                        String country = ecountry.getText().toString();
                        String website = ewebsite.getText().toString();
                        String photo = "http://13.125.246.30/newImage/shdlalwldlqslekNOIMG.jpg";
                        userList = retrofitInterface.getlist(country, location, website, open, name, photo);
                        userList.enqueue(mRetrofitCallback);
                    }
                });
                //아니오 버튼 클릭
                builder.setNegativeButton("아니오", null);
                builder.create().show();
            } else if (!photo.equals("") || photo != null) {
                Log.d("사진 선택 O", "업로드 완료");
                userList = retrofitInterface.getlist(country, location, website, open, name, photo);
                userList.enqueue(mRetrofitCallback);
            }

//            }
            Log.d("Map", ""+photo);
        }
    }
    protected Callback<Retrofit_result> mRetrofitCallback = new Callback<Retrofit_result>() {
        @Override
        public void onResponse(Call<Retrofit_result> call, Response<Retrofit_result> response) {
            Retrofit_result retrofit_result = response.body();
            String result = retrofit_result.getResult();
            Log.d("결과", result);
            if (result.contains("null")) {
                Toast.makeText(Upload_enjoy.this, "빈 항목이 있습니다.", Toast.LENGTH_SHORT).show();
            } else if (result.equals("success")) {
                //리사이클러뷰 데이터 초기화
                datalist.clear();
                //초기화 알림(반영)
                adapter.notifyDataSetChanged();
                ename.setText("");
                elocation.setText("");
                ecountry.setText("");
                eopen.setText("");
                ewebsite.setText("");
                check = "x";
                check();
                Toast.makeText(Upload_enjoy.this, "등록하였습니다.", Toast.LENGTH_SHORT).show();
            } else if (result.contains("fail")) {
                Toast.makeText(Upload_enjoy.this, "등록을 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        }
        @Override
        public void onFailure(Call<Retrofit_result> call, Throwable t) {
            t.printStackTrace();
        }
    };
}

