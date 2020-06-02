package com.example.testing_gps_services;

import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import testing.gps_service.R;

public class MapsFragment extends Fragment {
    private BroadcastReceiver broadcastReceiver;
    public MapView mapView;
    String error;
    private static final String GEOJSON_SOURCE_ID = "GEOJSON_SOURCE_ID";
    private static final String MARKER_IMAGE_ID = "MARKER_IMAGE_ID";
    private static final String MARKER_LAYER_ID = "MARKER_LAYER_ID";
    private static final String CALLOUT_LAYER_ID = "CALLOUT_LAYER_ID";
    private static final String PROPERTY_SELECTED = "selected";

    private SharedPreferences pref = null; // 0 - for private mode
    private SharedPreferences.Editor editor = null;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_maps,container,false);
        Mapbox.getInstance(getContext(), getString(R.string.mapbox_access_token));

        GetAllCommande();
        mapView = (MapView) view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.pref = getActivity().getApplicationContext().getSharedPreferences("userdata", 0);
        this.editor = this.pref.edit();
        Mapbox.getInstance(getContext(), getString(R.string.mapbox_access_token));


    }

















    /***/
    public  void setInfoCommandMarker(final ArrayList<DataInfo> data){

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull MapboxMap mapboxMap) {

                mapView.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(@NonNull MapboxMap mapboxMap) {

                        for(DataInfo element:data){


                            MarkerOptions options=new MarkerOptions();

                            options.title(element.getTitre()+"  \n" +element.getDesc());

                            options.setPosition(new LatLng(element.getLat(),element.getLog()));
                            options.position(new LatLng(element.getLat(),element.getLog())).title(element.getTitre()+"  \n" +element.getDesc())
                            ;

                            mapboxMap.addMarker(options.position(new LatLng(element.getLat(),element.getLog())).title(element.getTitre()+"  \n" +element.getDesc()));
                        }




                    }
                });
                // CameraPosition.Builder position = new CameraPosition.Builder().target(new LatLng(33.4122898,15.3592439)).zoom(10);
                mapboxMap.setOnMarkerClickListener(new MapboxMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(@NonNull Marker marker) {

                        //  marker.getTitle()
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setMessage( marker.getTitle()).show() ;
                        //Toast.makeText(getActivity(), marker.getTitle(), Toast.LENGTH_LONG).show();
                        return true;
                    }
                });
                mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        // Map is set up and the style has loaded. Now you can add data or make other map adjustments


                        /* add a filter to show only when selected feature property is true */

                    }
                });

            }
        });


    }
    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(getActivity())
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    //Yes button clicked
                    startActivity(new Intent(getContext(),MainActivity.class));
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    Toast.makeText(getContext(),"Répéter le scan",Toast.LENGTH_LONG).show();

                    break;
            }
        }
    };


    private void GetAllCommande(){

        Thread th=new Thread(){
            @Override
            public void run() {
                super.run();




                Log.e("data","access to Get all commande");
                RequestQueue requestQueue = Volley.newRequestQueue(getContext());
                Log.i("id liv", pref.getString("id","-1")) ;
                String URL = "http://192.168.4.99:8989/livreurs/commandes/"+pref.getString("No","PORTAIL");
                CustomStringRequest stringRequest  = new CustomStringRequest (
                        Request.Method.GET,
                        URL,

                        new Response.Listener<CustomStringRequest.ResponseM> () {

                            @Override
                            public void onResponse(CustomStringRequest.ResponseM response) {

                                //From here you will get headers
                                ArrayList<DataInfo> valuesCommande=new ArrayList<>();
                                String responseString = response.response;
                                JSONArray jArray = null;
                                try {
                                    jArray =new JSONObject(responseString).getJSONArray("value");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                for(int i=0;i<jArray.length();i++){
                                    JSONObject json_data = null;
                                    JSONObject json_client=null;
                                    JSONObject json_societe=null;
                                    try {
                                        json_data = jArray.getJSONObject(i);
                                        json_client=json_data.getJSONObject("client");
                                        json_societe=json_client.getJSONObject("societe");
                                        // Log.i("Data",json_societe.getString("latitude")+"  "+json_societe.getString("longtitude"));
                                        valuesCommande.add(new DataInfo(json_societe.getDouble("latitude"),json_societe.getDouble("longtitude"),json_client.getString("nom")+" "+ json_client.getString("prenom"),json_client.getString("telephone"),json_client.getInt("id")));

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }




                                }
                                for(int j=0;j<valuesCommande.size();j++){

                                    Log.d("Aff1",valuesCommande.get(j).toString());


                                }
                                ArrayList<DataInfo>  values=removeDuplicates(valuesCommande);
                                for(int j=0;j<values.size();j++){

                                    Log.d("Aff",values.get(j).toString());


                                }
                                Log.d("Aff",valuesCommande.size()+"    ----    "+values.size());
                                setInfoCommandMarker(values);



                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getActivity(), "Check your Information"+error.getMessage(), Toast.LENGTH_LONG).show();

                            }
                        }

                ){
                    @Override
                    protected Map<String, String> getParams()
                    {
                        Map<String,String> params=new HashMap<String,String>();




                        return params;
                    }
                    @Override
                    public Map<String,String> getHeaders()throws AuthFailureError {
                        Map<String,String> params= new HashMap<String, String>();
                        params.put("content-type","application/x-www-form-urlencoded;charset=utf-8");

                         params.put("Authorization", "Bearer " +pref.getString("token",null));



                        return params;
                    }
                };


                requestQueue.add(stringRequest);



            }
        };

        th.start();



    }

    boolean ExitDat(ArrayList<DataInfo> info,DataInfo el1){


        for (DataInfo el2:info){
            if(el1.getIdclient()!=el2.getIdclient() && el1.getLog()!=el2.getLog() && el1.getLat()!=el2.getLat()){

                return true;
            }

        }

        return  false;
    }

    // Function to remove duplicates from an ArrayList
    public  ArrayList<DataInfo> removeDuplicates(ArrayList<DataInfo> list)
    {
        ArrayList<DataInfo> aux=new ArrayList<DataInfo>();

        if(list.size()>0) {
            aux.add(list.get(0));
            for (int i = 1; i < list.size(); i++) {
                DataInfo el = list.get(i);
                if (ExitDat(aux, el)) {

                    aux.add(el);
                }


            }


        }





        return aux;
    }





}



