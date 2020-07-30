package com.example.tripwithyou.chatting;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tripwithyou.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

// 내부클래스   ( 접속용 )
public class TcpClient extends AppCompatActivity {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_choice);

    }

    public void chat() {
        EditText send_msg = (EditText) findViewById(R.id.send_msg);
        Button send_msg_btn = (Button) findViewById(R.id.send_msg_btn);
        Socket socket = null;            //Server와 통신하기 위한 Socket
        BufferedReader in = null;        //Server로부터 데이터를 읽어들이기 위한 입력스트림
        BufferedReader in2 = null;        //키보드로부터 읽어들이기 위한 입력스트림
        PrintWriter out = null;            //서버로 내보내기 위한 출력 스트림
        InetAddress ia = null;
        try {
            ia = InetAddress.getByName("192.168.241.1");    //서버로 접속
            socket = new Socket(ia,8080);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));

            System.out.println(socket.toString());
        } catch(IOException e) {
            e.printStackTrace();
        }
        try {
            System.out.print("서버로 보낼 메세지 : " + send_msg);

            //서버로 보낼 메세지 공백체크
            final String msg =  send_msg.getText().toString();
            if (msg != null || !msg.equals("")) {
                send_msg_btn.setEnabled(true);
            }
            //메세지 전송버튼 눌렀을 때 서버로 전송
            send_msg_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    System.out.println(msg);
                    System.out.flush();
                }
            });


            String str2 = in.readLine();            //서버로부터 되돌아오는 데이터 읽어들임
            System.out.println("서버로부터 되돌아온 메세지 : " + str2);
            socket.close();

        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
