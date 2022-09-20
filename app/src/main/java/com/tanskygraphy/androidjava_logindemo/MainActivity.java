package com.tanskygraphy.androidjava_logindemo;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btnLogin = (Button) findViewById(R.id.btnLogin);
        EditText txtEmail = (EditText) findViewById(R.id.txtEmail);
        EditText txtPassword = (EditText) findViewById(R.id.txtPassword);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = txtEmail.getText().toString().trim();
                String password = txtPassword.getText().toString().trim();
                String Error= "";
                if(email.matches(".+@.+\\.[a-z]+")==true && email.contains(" ")==false){
                    if(password.length()>=8){
                        try{
                            dbWorker dbw = new dbWorker(MainActivity.this);
                            dbw.execute(email,password);
                        }catch(Exception e){
                            // showMessage(e.getMessage());
                        }
                    }else{
                        Error="Please enter a valid password";
                        showMessage(Error);
                    }
                }else{
                    Error="Enter a valid email please!";
                    showMessage(Error);
                }
            }

        });
    }
    public  void showMessage(String Message){
        AlertDialog.Builder MessageStatus = new AlertDialog.Builder(MainActivity.this);
        MessageStatus.setTitle("Something wrong");
        MessageStatus.setIcon(R.drawable.icons8error);
        MessageStatus.setMessage(Message);
        MessageStatus.create();
        MessageStatus.show();
    }

    public class dbWorker extends AsyncTask {
        private Context c;
        private AlertDialog MessageStatus;
        public dbWorker(Context c){
            this.c= c;
        }
        protected void onPreExecute(){
            this.MessageStatus= new AlertDialog.Builder(this.c).create();
            this.MessageStatus.setTitle("Login Status");
        }
        @Override
        protected String doInBackground(Object[] param) {
            String cible = "http://192.168.1.8:8080/android-backend/AndroidJava-LoginDemo/login.php";
            String m="";
            try{
                URL url = new URL(cible);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setDoInput(true);
                con.setDoOutput(true);
                con.setRequestMethod("POST");
                OutputStream outs = con.getOutputStream();
                BufferedWriter bufw = new BufferedWriter(new OutputStreamWriter(outs,"utf-8"));

                String msg = URLEncoder.encode("mail","utf-8")+"="+
                        URLEncoder.encode((String) param[0],"utf-8")+
                        "&"+URLEncoder.encode("password","utf-8")+"="+
                        URLEncoder.encode((String) param[1],"utf-8");
                bufw.write(msg);
                bufw.flush();
                bufw.close();
                outs.close();

                InputStream ins = con.getInputStream();
                BufferedReader bufr = new BufferedReader(new InputStreamReader(ins,"iso-8859-1"));
                String line;
                StringBuffer sbuff = new StringBuffer();

                while((line=bufr.readLine())!=null){
                    sbuff.append(line);
                }
                return sbuff.toString();
            } catch (Exception ex) {
                return  ex.getMessage();
            }
        }
        protected void onPostExecute(Object o){

            try{
                // Input string to be convert to string array
                String data = o.toString();
                String ArrayOfData[] = data.split("\\.");
                String status = ArrayOfData[0].toString().trim();
                String message = ArrayOfData[1].toString().trim();

                // showMessage(status);
                if (status.contains("danger")){
                    showMessage(message);
                }else if(status.contains("success")){
                    // showMessage("Welcome");
                    Intent GoToDashboard = new Intent(MainActivity.this, DashboardActivity.class);
                    startActivity(GoToDashboard);
                }else{
                    showMessage("Error");
                }
            }catch(Exception e){
                System.out.print(e.getMessage());
                showMessage(e.getMessage());
            }

        }
    }


}