package com.example.testing_gps_services;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import testing.gps_service.R;


public class LoginActivity extends AppCompatActivity {

    private EditText edtUsername;
    private EditText edtPassword;
    private Button btnLogin;
    private BroadcastReceiver broadcastReceiver;

    SharedPreferences pref = null;
    SharedPreferences.Editor editor = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pref = getApplicationContext().getSharedPreferences("userdata", 0); // 0 - for private mode
        editor = pref.edit();


        setContentView(R.layout.activity_login);

        edtUsername = (EditText) findViewById(R.id.loginText);
        edtPassword = (EditText) findViewById(R.id.pwdText);
        btnLogin = (Button) findViewById(R.id.Btn_Login);


        //userService = ApiUtils.getUserService();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = edtUsername.getText().toString();
                String password = edtPassword.getText().toString();
                final LoadingDialog loadingDialog = new LoadingDialog(LoginActivity.this);
                loadingDialog.startLoadingDialog();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadingDialog.dismissDialof();
                    }
                }, 3000);
                //AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                // builder.setMessage( "att...").show() ;
                validateLogin(username, password);
                //  startActivity(new Intent(getApplicationContext(),MainActivity.class));
                //validate form
              /*  if(validateLogin(username, password)){
                    //do login
                    Log.e("Data","if condition");
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));

                }*/
            }
        });

    }

    private boolean validateLogin(final String username, final String password) {

        Thread th = new Thread() {
            @Override
            public void run() {
                super.run();


                RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);
                String URL = "http://192.168.4.99:8989/login";
                CustomStringRequest stringRequest = new CustomStringRequest(
                        Request.Method.POST,
                        URL,

                        new Response.Listener<CustomStringRequest.ResponseM>() {

                            @Override
                            public void onResponse(CustomStringRequest.ResponseM response) {

                                //From here you will get headers
                                String Token = response.headers.get("Authorization");

                                String responseString = response.response;
                                try {

                                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                   
                                    editor.putBoolean("isLoggedIn", true); // Storing boolean - true/false
                                    editor.putString("token", Token); // Storing string
                                     editor.commit();
                                    Log.i("token Login : ", Token);

                                    startActivity(i);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getApplicationContext(), "Check your Information", Toast.LENGTH_LONG).show();

                            }
                        }

                ) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();


                        return params;
                    }

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("content-type", "application/x-www-form-urlencoded;charset=utf-8");
                        params.put("username", username);
                        params.put("password", password);

                        return params;
                    }
                };


                requestQueue.add(stringRequest);


            }
        };

        th.start();

        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (pref.getString("token", null) != null) {
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);
        }
    }
}