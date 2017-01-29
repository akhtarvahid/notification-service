package com.example.vaheed.nikitask;
import android.app.ProgressDialog;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * Created by Belal on 2/3/2016.
 */
//Our class extending fragment
public class Tab1 extends Fragment implements LocationListener {
    private String TAG = MainActivity.class.getSimpleName();
    private String name;
    private String email ;
    private String phone ;
    private String latitude ;
    private String longitude ;
    private ProgressDialog pDialog;
    // Google Map
    private GoogleMap googleMap;
    private static String url="http://akhtarvahid543.16mb.com/contacts.json";
    ArrayList<ArrayList<String>> contactList=new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab1, container, false);
        try {
            // Loading map
            initilizeMap();

        } catch (Exception e) {
            e.printStackTrace();
        }
        new Tab1.GetContacts().execute();
        return v;
    }

    private void initilizeMap() {
        if (googleMap == null) {
            googleMap = ((MapFragment) getActivity().getFragmentManager().findFragmentById(
                    R.id.map)).getMap();
            // check if map is created successfully or not
            if (googleMap == null) {
                Toast.makeText(getContext(),
                        "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        initilizeMap();
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    private class GetContacts extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(getContext());
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(true);
            pDialog.show();
        }
        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url);

            // Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONArray contacts = jsonObj.getJSONArray("contacts");

                    // looping through All Contacts
                    for (int i = 0; i < contacts.length(); i++) {
                        JSONObject c = contacts.getJSONObject(i);

                        name = c.getString("name");
                        email = c.getString("email");
                        phone = c.getString("phone");
                        latitude = c.getString("latitude");
                        longitude = c.getString("longitude");

                        ArrayList<String> arrayList=new ArrayList<>();
                        arrayList.add(name);
                        arrayList.add(email);
                        arrayList.add(phone);
                        arrayList.add(latitude);
                        arrayList.add(longitude);
                        contactList.add(arrayList);
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());


                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();

            for (int i=0;i<contactList.size();i++){

                String name=contactList.get(i).get(0);
                String email=contactList.get(i).get(1);
                String phone=contactList.get(i).get(2);
                Double latitude= Double.parseDouble(contactList.get(i).get(3));
                Double longitude= Double.parseDouble(contactList.get(i).get(4));

                LatLng Banglore4 = new LatLng(latitude, longitude);
                googleMap.addMarker(new MarkerOptions().position(Banglore4).title(name+", "+phone+", "+email));
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(Banglore4));

                Log.e("All data",name+" ,"+email+" ,"+phone+" ,"+latitude+" ,"+longitude);
            }
        }
    }
}