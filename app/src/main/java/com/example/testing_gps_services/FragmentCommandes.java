package com.example.testing_gps_services;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import testing.gps_service.R;

public class FragmentCommandes extends Fragment {
    public List<DataInfoCommande> commandesList = new ArrayList<>();
    public RecyclerView recyclerView;
    public View view;

    private SharedPreferences pref = null; // 0 - for private mode
    private SharedPreferences.Editor editor = null;
    String URL = "http://192.168.4.99:8989/";

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.pref = getActivity().getApplicationContext().getSharedPreferences("userdata", 0);
        this.editor = this.pref.edit();

    }


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_commandes, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        GetAllCommande();
        Thread thr = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(2000);
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getActivity().getApplicationContext(), "Sman" + commandesList.size(), Toast.LENGTH_LONG).show();
                            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                            DataInfoCommandeAdapter adapter = new DataInfoCommandeAdapter(getActivity(), commandesList);

                            //setting adapter to recyclerview

                            recyclerView.setAdapter(adapter);


                        }

                    });


                    // manage other components that need to respond
                    // to the activity lifecycle

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                super.run();
            }
        };
        thr.start();


        //creating recyclerview adapter
        return view;
    }


    private void GetAllCommande() {

        Thread th = new Thread() {
            @Override
            public void run() {
                super.run();

                Log.e("data", "access to Get all commande");
                RequestQueue requestQueue = Volley.newRequestQueue(getContext());
                if (pref.getString("role", "").equals("Livreur"))
                    URL += "livreurs/commandes/" + pref
                            .getString("No", "PORTAIL");
                else
                    URL += "commandes/my/" + pref.getString("id", "-1");
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
                                    jArray =new JSONObject(responseString).getJSONArray("value");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                Log.d("TAG", "onResponse: "+jArray);
                                for (int i = 0; i < jArray.length(); i++) {

                                    JSONObject json_data = null;
                                    JSONObject json_client = null;
                                    JSONObject json_societe = null;
                                    // int idCommande;
                                    // String type;
                                    try {
                                        json_data = jArray.getJSONObject(i);

                                          DataInfoCommande commande = new DataInfoCommande();
                                         commande.setIdCommande(json_data.getString("No"));
                                        commande.setNom(json_data.getString("Sell_to_Customer_Name"));
                                         commande.setType(json_data.getString("Order_Type"));
                                        commandesList.add(commande);
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
                                Toast.makeText(getActivity(), "Check your Information", Toast.LENGTH_LONG).show();

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
                        String token = pref.getString("token", "");
                        params.put("Authorization", "Bearer " + token);


                        return params;
                    }
                };


                requestQueue.add(stringRequest);


            }


        };
        th.start();
    }


}