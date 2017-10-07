package com.siddharthkumar.restaurantdeals;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.ArrayMap;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity implements RadiusFragment.OnFragmentInteractionListener{
    RecyclerView recyclerView;
    SwipeRefreshLayout srl;
    ArrayList<Restaurant> restaurants= new ArrayList<Restaurant>();
    int radius=10;
    RVAdapter adapter;
    double currentlat;
    double currentlong;
    RadiusFragment radiusFragment;
    int results = 10;
    MenuItem reveal;
    final String TAG = "Main";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(savedInstanceState!=null){
            results = savedInstanceState.getInt("results");
            radius = savedInstanceState.getInt("radius");
            currentlat = savedInstanceState.getDouble("lat");
            currentlong = savedInstanceState.getDouble("long");

        }
        recyclerView = (RecyclerView)findViewById(R.id.list);
        srl = (SwipeRefreshLayout) findViewById(R.id.refresh);


        adapter=new RVAdapter(restaurants,getApplicationContext());

        recyclerView.setAdapter(adapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();

            }
        });
      //  restaurants.add(new Restaurant("Taco Bell", 34, "da", "sda", " sad00", "23", "rs"));

        refresh();


    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("radius",radius);
        outState.putInt("results",results);
        outState.putDouble("lat",currentlat);
        outState.putDouble("long",currentlong);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        reveal = menu.findItem(R.id.radius);
        return true;
    }

    public void setNewRadius(){
        if(radiusFragment==null){

            radiusFragment = RadiusFragment.newInstance();
            Bundle bundle = new Bundle();
            bundle.putInt("radius",radius);
            bundle.putInt("results",results);
            Log.e(TAG,radius+","+results);
            radiusFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter,R.anim.pop_enter).add(R.id.main,radiusFragment).commit();

            reveal.setIcon(R.mipmap.ic_keyboard_arrow_up_white_24dp);


        }
        else{
            radius=((SeekBar)findViewById(R.id.seekBar)).getProgress();
            results=((SeekBar)findViewById(R.id.seekBar2)).getProgress();
            Log.e(TAG,radius+","+results);
            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.exit,R.anim.pop_exit).remove(radiusFragment).commit();
            radiusFragment=null;

            reveal.setIcon(R.mipmap.ic_keyboard_arrow_down_white_24dp);
        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        switch (menuItem.getItemId()){

            case R.id.radius:{
                setNewRadius();


                return true;
            }

            default:{
                return super.onOptionsItemSelected(menuItem);
            }

        }


    }



    public void refresh()  {
        srl.setRefreshing(true);
        try{
            restaurants.clear();
            if (ContextCompat.checkSelfPermission(getApplicationContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {


                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        1);


            }
            SingleShotLocationProvider.requestSingleUpdate(getApplicationContext(),
                    new SingleShotLocationProvider.LocationCallback() {
                        @Override
                        public void onNewLocationAvailable(SingleShotLocationProvider.GPSCoordinates location) {
                            currentlat=location.latitude;
                            currentlong=location.longitude;

                        }


                    });

            for(int x=0;x<results;x++){
                if(currentlat!=0&&currentlong!=0){



                new FetchRestaurantsTask().execute("http://hackjskn.tk/rests/"+currentlat+"/"+currentlong+"/"+radius+"/"+(x+1));



                }
            }
        }
        catch(Exception e){Log.e("MAIN", e.toString());}
        srl.setRefreshing(false);
        adapter.notifyDataSetChanged();

    }

    @Override
    public void onFragmentInteraction() {
        SeekBar seekBar = (SeekBar)findViewById(R.id.seekBar);
        if(seekBar!=null)
          radius = seekBar.getProgress();
        TextView textView = (TextView)findViewById(R.id.radiusnumber);
        textView.setText("Radius = "+radius);

        SeekBar seekBar2 = (SeekBar)findViewById(R.id.seekBar2);
        if(seekBar!=null)
            results = seekBar2.getProgress();
        TextView textView2 = (TextView)findViewById(R.id.resultstext);
        String st = "Display "+results+" Results";
        SpannableString spannableString = new SpannableString(st);
        spannableString.setSpan(new UnderlineSpan(), 8,10,0);
        textView2.setText(spannableString);
    }

    public class FetchRestaurantsTask extends AsyncTask<String,Void, Restaurant>{
        @Override
        protected Restaurant doInBackground(String... params) {
                String str=params[0];
                URL url;
                String response="";
                HttpURLConnection urlConnection = null;
                try {
                    url = new URL(str);

                    urlConnection = (HttpURLConnection) url
                            .openConnection();

                    InputStream in = urlConnection.getInputStream();

                    InputStreamReader isw = new InputStreamReader(in);

                    int data = isw.read();
                    while (data != -1) {
                        char current = (char) data;
                        data = isw.read();
                        response+=current;

                    }


                    JSONObject everything = new JSONObject(response);


                        Restaurant restaurant = new Restaurant(
                                everything.getString("name"),
                                everything.getDouble("distance"),
                                everything.getString("short_title"),
                                everything.getString("title"),
                                everything.getString("fine_print"),
                                everything.getString("address")
                                ,
                                everything.getString("location")) ;


                   return restaurant;





                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                }





            return null;
        }

        @Override
        protected void onPostExecute(Restaurant restaurant) {
            super.onPostExecute(restaurant);

            restaurants.add(restaurant);
            adapter.notifyDataSetChanged();


        }
    }

    public class Restaurant{

        String name;

        double distance;

        String deal;

        String type;

        String hours;
        String address;


        String latlong;

        public Restaurant(String s1,  double d,String s2, String s3, String s4,String d3,String k){
            name=s1;
            distance=d;

            deal=s2;
            type=s3;
            hours=s4;

            address=d3;

            latlong=k;

        }


    }
}
