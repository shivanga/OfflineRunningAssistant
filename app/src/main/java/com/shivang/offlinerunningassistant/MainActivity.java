package com.shivang.offlinerunningassistant;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity implements Runnable{

    private static final long MINIMUM_DISTANCE_CHANGE_FOR_UPDATES = 1; // in Meters
    private static final long MINIMUM_TIME_BETWEEN_UPDATES = 30000;

    protected LocationManager locationManager;
    static double n=0;
    Long s1,r1;
    double plat,plon,clat,clon,dis;
    MyCount counter;
    Thread t1;
    EditText e1;
    boolean bool=true;
    TextView t,t2,t3,t4,t5;
    Button b1;
    ImageButton b2,b3,b4,b5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        b1=(Button)findViewById(R.id.button1);//current position
                b2=(ImageButton)findViewById(R.id.ibplay);//start moving.. calculates distance on clicking this
        b3=(ImageButton)findViewById(R.id.ibpause); //pause
        b4=(ImageButton)findViewById(R.id.ibresume);  //resume
        b5=(ImageButton)findViewById(R.id.ibstop);  // get distance
        e1=(EditText)findViewById(R.id.editText1);

        t=(TextView)findViewById(R.id.textView3);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    MINIMUM_TIME_BETWEEN_UPDATES,
                    MINIMUM_DISTANCE_CHANGE_FOR_UPDATES,
                    new MyLocationListener()
            );
        }
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCurrentLocation();
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    Location location;

    protected void showCurrentLocation() {

             location= locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (location != null) {
            String message = String.format(
                    "Current Location \n Longitude: %1$s \n Latitude: %2$s",
                    location.getLongitude(), location.getLatitude()
            );
            clat=location.getLatitude();
            clon=location.getLongitude();
            Toast.makeText(MainActivity.this, message,
                    Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(MainActivity.this, "null location",
                    Toast.LENGTH_LONG).show();
        }

    }
    public void start (View v){

        switch(v.getId()){

            case R.id.ibplay:
                t1=new Thread();
                t1.start();
                counter= new MyCount(30000,1000);
                counter.start();
                break;
            case R.id.ibpause:
                counter.cancel();
                bool=false;
                break;
            case R.id.ibresume:
                counter= new MyCount(s1,1000);
                counter.start();
                bool=true;
                break;
            case R.id.ibstop:

                double time=n*30+r1;
                Toast.makeText(MainActivity.this,"distance in metres:"+String.valueOf(dis)+"Velocity in m/sec :"+String.valueOf(dis/time)+"Time :"+String.valueOf(time),Toast.LENGTH_LONG).show();

        }


    }

    @Override
    public void run() {
        while(bool){
            clat=location.getLatitude();
            clon=location.getLongitude();
            if(clat!=plat || clon!=plon){
                dis+=getDistance(plat,plon,clat,clon);
                plat=clat;
                plon=clon;
                t.setText(dis+"");
            }

        }

    }
    public double getDistance(double lat1, double lon1, double lat2, double lon2) {
        double latA = Math.toRadians(lat1);
        double lonA = Math.toRadians(lon1);
        double latB = Math.toRadians(lat2);
        double lonB = Math.toRadians(lon2);
        double cosAng = (Math.cos(latA) * Math.cos(latB) * Math.cos(lonB-lonA)) +
                (Math.sin(latA) * Math.sin(latB));
        double ang = Math.acos(cosAng);
        double dist = ang *6371;
        return dist;
    }

    public class MyLocationListener implements LocationListener {

        public void onLocationChanged(Location location) {
            String message = String.format(
                    "New Location \n Longitude: %1$s \n Latitude: %2$s",
                    location.getLongitude(), location.getLatitude()
            );

            Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
        }

        public void onStatusChanged(String s, int i, Bundle b) {
            Toast.makeText(MainActivity.this, "Provider status changed",
                    Toast.LENGTH_LONG).show();
        }

        public void onProviderDisabled(String s) {
            Toast.makeText(MainActivity.this,
                    "Provider disabled by the user. GPS turned off",
                    Toast.LENGTH_LONG).show();
        }

        public void onProviderEnabled(String s) {
            Toast.makeText(MainActivity.this,
                    "Provider enabled by the user. GPS turned on",
                    Toast.LENGTH_LONG).show();
        }

    }
    public class MyCount extends CountDownTimer {
        public MyCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }
        @Override
        public void onFinish() {
            counter= new MyCount(30000,1000);
            counter.start();
            n=n+1;
        }
        @Override
        public void onTick(long millisUntilFinished) {
            s1=millisUntilFinished;
            r1=(30000-s1)/1000;
            e1.setText(String.valueOf(r1));


        }
    }

}




