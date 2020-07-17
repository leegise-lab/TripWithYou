package com.example.tripwithyou;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;


public class MainActivity extends AppCompatActivity {
//    final Fragment fragmentHome = new fragment_home();
    final Fragment fragmentBoard = new Fragment_board();
    final Fragment fragmentHome = new Sample();
    final Fragment fragmentMessage = new Fragment_message();
    final Fragment fragmentProfile = new Fragment_profile();
//    Fragment fragmentenjoy = new fragment_enjoy();

    private FragmentManager fragmentManager = getSupportFragmentManager();
    Fragment active = fragmentHome;



    //초기화면 설정(다른애들 다 숨기고 home화면만 띄우겠다)
    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.main_activity);



        BottomNavigationView navigationView = (BottomNavigationView) findViewById(R.id.bottom_bar);
        navigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        fragmentManager.beginTransaction().add(R.id.frame_layout, fragmentProfile, "4").hide(fragmentProfile).commit();
        fragmentManager.beginTransaction().add(R.id.frame_layout, fragmentMessage, "3").hide(fragmentMessage).commit();
        fragmentManager.beginTransaction().add(R.id.frame_layout, fragmentBoard, "2").hide(fragmentBoard).commit();
        fragmentManager.beginTransaction().add(R.id.frame_layout, fragmentHome, "1").commit();


    }

    //바텀 네비바 클릭 리스너
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.bottom_home:
                    Log.d("home", "home 버튼 클릭");
                    fragmentManager.beginTransaction().hide(active).show(fragmentHome).commit();
                    active = fragmentHome;
                    return true;

                case R.id.bottom_board:
                    Log.d("board", "board 버튼 클릭");
                    fragmentManager.beginTransaction().hide(active).show(fragmentBoard).commit();
                    active = fragmentBoard;
                    return true;

                case R.id.bottom_message:
                    Log.d("message", "message 버튼 클릭");
                    fragmentManager.beginTransaction().hide(active).show(fragmentMessage).commit();
                    active = fragmentMessage;
                    return true;

                case R.id.bottom_profile:
                    Log.d("profile", "profile 버튼 클릭");
                        fragmentManager.beginTransaction().hide(active).show(fragmentProfile).commit();
                        active = fragmentProfile;

                    return true;
            }
            return false;
        }
    };

    //상단 액션바 연결
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d("onCreateOptionsMenu","상단바 연결");
        getMenuInflater().inflate(R.menu.action_bar,menu);
        return super.onCreateOptionsMenu(menu);
    }

    //        //상단바 검색버튼 눌렀을 때
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("상단바","검색버튼 클릭");
        int id = item.getItemId();
        if (id == R.id.action_bar_search) {
            startActivity(new Intent(MainActivity.this, Search_city.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    //첫 화면에서 뒤로가기 눌렀을때 토스트메시지 띄워주는 부분
    // 뒤로가기 버튼 입력시간이 담길 long 객체
    private long pressedTime = 0;
    // 리스너 생성
    public interface OnBackPressedListener {
        void onBack();

        void onBackPressed();
    }
    // 리스너 객체 생성
    private OnBackPressedListener mBackListener;
    // 리스너 설정 메소드
    public void setOnBackPressedListener(OnBackPressedListener listener) {
        mBackListener = listener;
    }
    // 뒤로가기 버튼을 눌렀을 때 메소드
    @Override
    public void onBackPressed() {
        // 다른 Fragment 에서 리스너를 설정했을 때 처리하는 부분
        if(mBackListener != null) {
            mBackListener.onBack();
            Log.e("!!!", "Listener is not null");
            // 리스너가 설정되지 않은 상태(예를들어 메인Fragment)라면
            // 뒤로가기 버튼을 연속적으로 두번 눌렀을 때 앱이 종료
        } else {
            Log.e("!!!", "리스너 null값 아님");
            if ( pressedTime == 0 ) {
                Toast.makeText(this, "한번 더 누르면 종료됩니다.",Toast.LENGTH_SHORT).show();
                pressedTime = System.currentTimeMillis();
            } else {
                int seconds = (int) (System.currentTimeMillis() - pressedTime);
                if ( seconds > 2000 ) {
                    Toast.makeText(this, "한번 더 누르면 종료됩니다.",Toast.LENGTH_SHORT).show();
                    pressedTime = 0 ;
                }
                else {
                    super.onBackPressed();
                    Log.e("!!!", "onBackPressed : finish, killProcess/종료");
                    //앱 완전꺼지는거
//                    finish();
//                    android.os.Process.killProcess(android.os.Process.myPid());
                    //꺼진것처럼 보이는거
                    Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                    homeIntent.addCategory(Intent.CATEGORY_HOME);
                    homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(homeIntent);
                }
            }
        }
    }

//    public void moveenjoy(){
//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction().addToBackStack(null);
//        transaction.add(R.id.container,fragmentenjoy);
//        transaction.commit();
//    }
}
