package com.example.testing_gps_services;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.zxing.Result;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import me.dm7.barcodescanner.zxing.ZXingScannerView;
import testing.gps_service.R;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;

import static android.Manifest.permission.CAMERA;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private TextView fullnameText;
    private BroadcastReceiver broadcastReceiver;


    public static List<DataInfoCommande> commandesList = new ArrayList<>();
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    androidx.appcompat.widget.Toolbar toolbar;
    NavigationView navigationView;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    private static final int REQUEST_CAMERA = 1;
    private ZXingScannerView mScannerView;
    public int currentapiVersion = android.os.Build.VERSION.SDK_INT;
    StompClient mStompClient;
    String TAG = "LongOperation";

    private Gson gson;

    private SharedPreferences pref = null; // 0 - for private mode
    private SharedPreferences.Editor editor = null;


    @Override
    protected void onResume() {
        super.onResume();


        if (broadcastReceiver == null) {
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, final Intent intent) {


                    final Double value1 = (Double) intent.getExtras().get("longtitude");
                    final Double value2 = (Double) intent.getExtras().get("Latitude");

                    RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
                    String URL = "http://192.168.4.99:8989/positionsgps";
                    final StringRequest objectRequest = new StringRequest(
                            Request.Method.POST,
                            URL,

                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {

                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {

                                }
                            }

                    ) {
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<String, String>();
                            String id = pref.getString("id", "");
                            params.put("id", id);

                            params.put("longtitude", value1.toString());
                            params.put("Latitude", value2.toString());

                            return params;
                        }

                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("content-type", "application/x-www-form-urlencoded;charset=utf-8");
                            return params;
                        }
                    };


                    requestQueue.add(objectRequest);

                }

            };
        }
        registerReceiver(broadcastReceiver, new IntentFilter("location_update"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
        }
    }

    private boolean checkPermission() {
        return (ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{CAMERA}, REQUEST_CAMERA);
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.pref = getApplicationContext().getSharedPreferences("userdata", 0);
        this.editor = this.pref.edit();
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        if (!runtime_permissions()) {
            this.loadRefrechData();

//            mStompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, "ws://192.168.4.109:8989/gs-guide-websocket/websocket");
//            Disposable lifecycle = mStompClient.lifecycle().subscribe(lifecycleEvent -> {
//                switch (lifecycleEvent.getType()) {
//                    case OPENED:
//                        Log.i(TAG, "Stomp Connection Opened");
//                        break;
//                    case ERROR:
//                        Log.d(TAG, "Error ", lifecycleEvent.getException());
//                        break;
//                    case CLOSED:
//                        Log.w(TAG, "Stomp Connection Closed");
//                        break;
//                    case FAILED_SERVER_HEARTBEAT:
//                        Log.d(TAG, "Failed Server Heartbeat ");
//                        break;
//                }
//            });
//            if (!mStompClient.isConnected())
//                mStompClient.reconnect();
//
//
//            Disposable topic = mStompClient.topic("/topic/notifications").subscribe(stompMessage -> {
//                Log.d(TAG, stompMessage.getPayload());
//                // Do your code here when ever you receive data from server.
//            }, throwable -> Log.d(TAG, throwable.getMessage().toString() + ""));
//            Disposable send = mStompClient.send("/app/2", "{\"name\" : \"name\"}").subscribe(() -> {
//            }, throwable -> {
//                Toast.makeText(getApplicationContext(), "errot in notif subscribe", Toast.LENGTH_LONG).show();
//                Log.i("error in notif subce", throwable.getMessage());
//            });
//            compositeDisposable.add(lifecycle);
//            compositeDisposable.add(topic);
//            compositeDisposable.add(send);
            //Mapbox declaration

            Intent i = new Intent(getApplicationContext(), GPS_Service.class);
            startService(i);


            drawerLayout = findViewById(R.id.drawer);


            //kacem bar
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
            setSupportActionBar(toolbar);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (drawerLayout.isDrawerOpen(Gravity.END)) {

                        drawerLayout.closeDrawer(Gravity.START);
                    } else {
                        drawerLayout.openDrawer(Gravity.LEFT);

                    }
                }
            });


            navigationView = findViewById(R.id.navigationView);
            navigationView.setNavigationItemSelectedListener(this);
            drawerLayout.addDrawerListener(actionBarDrawerToggle);
            actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            actionBarDrawerToggle.syncState();


        } else {

            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }


    }

    private boolean runtime_permissions() {
        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);

            return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            } else {
                runtime_permissions();
            }
        } else if (grantResults.length > 0) {

            boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
            if (cameraAccepted) {
                Toast.makeText(getApplicationContext(), "Permission Granted, Now you can access camera", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "Permission Denied, You cannot access and camera", Toast.LENGTH_LONG).show();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (shouldShowRequestPermissionRationale(CAMERA)) {
                        showMessageOKCancel("You need to allow access to both the permissions",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                            requestPermissions(new String[]{CAMERA},
                                                    REQUEST_CAMERA);
                                        }
                                    }
                                });
                        return;
                    }
                }
            }
        }


    }


    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    //Yes button clicked
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    Toast.makeText(getApplicationContext(), "Répéter le scan", Toast.LENGTH_LONG).show();

                    break;
            }
        }
    };

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        drawerLayout.closeDrawer(GravityCompat.START);
        if (menuItem.getItemId() == R.id.map) {
            getSupportActionBar().setTitle("Voir sur Map");
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            Bundle bundle = new Bundle();

            MapsFragment mapsFragment = new MapsFragment();


            mapsFragment.setArguments(bundle);
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_fragment, mapsFragment);
            fragmentTransaction.commit();
        }


        if (menuItem.getItemId() == R.id.commandes) {
            //GetAllCommande();
            getSupportActionBar().setTitle("Mes commandes");
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            Bundle bundle = new Bundle();
            bundle.putString("id", getIntent().getStringExtra("id"));
            bundle.putString("Token", getIntent().getStringExtra("Token"));
            FragmentCommandes fragmentCommandes = new FragmentCommandes();
            fragmentCommandes.setArguments(bundle);
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_fragment, fragmentCommandes);
            fragmentTransaction.commit();
        }
        if (menuItem.getItemId() == R.id.scan) {
//Toast.makeText(getApplicationContext(),"Access Scanner",Toast.LENGTH_LONG).show();


            if (checkPermission()) {
                if (mScannerView == null) {
                    mScannerView = new ZXingScannerView(this);
                    setContentView(mScannerView);
                }
                mScannerView.setResultHandler(new ZXingScannerView.ResultHandler() {
                    @Override
                    public void handleResult(Result rawResult) {
                        final String result = rawResult.getText();
                        Log.d("QRCodeScanner", rawResult.getText());
                        Log.d("QRCodeScanner", rawResult.getBarcodeFormat().toString());

                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setMessage("Confirmer code : " + result).setPositiveButton("Oui", dialogClickListener)
                                .setNegativeButton("Non", dialogClickListener).show();


                    }
                });
                mScannerView.startCamera();
            } else {
                requestPermission();
            }
        }
        if (menuItem.getItemId() == R.id.logout) {
            editor.clear();
            editor.commit();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }

        return true;
    }

    private void GetAllCommande() {

        Thread th = new Thread() {
            @Override
            public void run() {
                super.run();

                Log.e("data", "access to Get all commande");
                RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
                String URL = "http://192.168.4.109:8989/livreurs/commandes/" + getIntent().getStringExtra("id");
                CustomStringRequest stringRequest = new CustomStringRequest(
                        Request.Method.GET,
                        URL,

                        new Response.Listener<CustomStringRequest.ResponseM>() {

                            @Override
                            public void onResponse(CustomStringRequest.ResponseM response) {

                                //From here you will get headers
                                ArrayList<DataInfoCommande> valuesCommande = new ArrayList<>();
                                String responseString = response.response;
                                JSONArray jArray = null;
                                try {
                                    jArray = new JSONArray(responseString);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                for (int i = 0; i < jArray.length(); i++) {

                                    JSONObject json_data = null;
                                    JSONObject json_client = null;
                                    JSONObject json_societe = null;

                                    try {
                                        json_data = jArray.getJSONObject(i);
                                        json_client = json_data.getJSONObject("client");
                                        json_societe = json_client.getJSONObject("societe");
                                        json_data = jArray.getJSONObject(i);
                                        commandesList.add(new DataInfoCommande(json_societe.getString("latitude"), json_societe.getString("longtitude"), json_client.getString("id"), json_client.getString("telephone")));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }


                                }


                                for (int j = 0; j < commandesList.size(); j++) {

                                    Log.d("Aff", commandesList.get(j).toString());


                                }
                                Log.d("Aff", commandesList.size() + "    ----    " + commandesList.size());


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

                        Log.e("token", pref.getString("Token",""));
                        params.put("Authorization", "Bearer " + pref.getString("Token", ""));


                        return params;
                    }
                };


                requestQueue.add(stringRequest);
            }


        };
        th.start();
    }

   /* private void connectStomp() {
        // replace your websocket url
        mStompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, "ws://192.168.4.109:8988/gs-guide-websocket/websocket");
        //Stomp.over(Stomp.ConnectionProvider.OKHTTP, "http://192.168.4.109:8988/gs-guide-websocket");
        mStompClient.connect();

        //  mStompClient.topic("/topic/notifications.2").subscribe(topicMessage -> {
        //      Log.d(TAG, topicMessage.getPayload());
        //  });

        mStompClient.send("/app/2", "My first STOMP message!").subscribe();


        mStompClient.lifecycle().subscribe(lifecycleEvent -> {
            switch (lifecycleEvent.getType()) {

                case OPENED:
                    Log.d(TAG, "Stomp connection opened");
                    break;

                case ERROR:
                    Log.e(TAG, "Error", lifecycleEvent.getException());
                    break;

                case CLOSED:
                    Log.d(TAG, "Stomp connection closed");
                    break;
            }
        });


    }*/

    private void loadRefrechData() {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        String URL = "http://192.168.4.99:8989/clients/getmyinfo";
        CustomStringRequest stringRequest = new CustomStringRequest(
                Request.Method.GET,
                URL,

                new Response.Listener<CustomStringRequest.ResponseM>() {

                    @Override
                    public void onResponse(CustomStringRequest.ResponseM response) {


                        String responseString = response.response;
                        try {
                            JSONObject obj = new JSONObject(responseString);
                            JSONObject compte = obj.getJSONObject("compte");

                            Toast.makeText(getApplicationContext(), "welcome " + obj.get("nom").toString(), Toast.LENGTH_LONG).show();
                            editor.putString("role", compte.getJSONObject("role").getString("designation"));
                            editor.putString("id", obj.getString("id"));
                            editor.putString("No","PORTAIL");
                            editor.commit();
                            Log.d("ff","ddddddd");
                            fragmentManager = getSupportFragmentManager();
                            fragmentTransaction = fragmentManager.beginTransaction();
                            if (pref.getString("role", "").equals("Livreur")) {
                                fragmentTransaction.replace(R.id.container_fragment, new FragmentCommandes());
                                fragmentTransaction.commit();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "error in load dara from server", Toast.LENGTH_LONG).show();

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
                String token = pref.getString("token", "");
                Log.i("token Main : ", token);
                params.put("Content-Type", "application/json; charset=UTF-8");
                params.put("Authorization", "Bearer " + token);


                return params;
            }
        };


        requestQueue.add(stringRequest);


    }

}

