package tasos.anesiadis.ptyxiakh;

        import android.location.Location;
        import android.os.Bundle;
        import android.support.v7.app.ActionBarActivity;
        import android.util.Log;
        import android.widget.Toast;

        import com.google.android.gms.common.ConnectionResult;
        import com.google.android.gms.common.api.GoogleApiClient;
        import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
        import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
        import com.google.android.gms.location.LocationListener;
        import com.google.android.gms.location.LocationRequest;
        import com.google.android.gms.location.LocationServices;
        import com.google.gson.JsonObject;
        import com.koushikdutta.async.future.FutureCallback;
        import com.koushikdutta.ion.Ion;

public class MainActivity extends ActionBarActivity implements
        ConnectionCallbacks, OnConnectionFailedListener, LocationListener {

    protected String latitude;
    protected String longitude;

    protected String globalAddress;
    protected String url;
    protected String userId;

    /**
     * Tracks the status of the location updates request. Value changes when the
     * user presses the Start Updates and Stop Updates buttons.
     */
    protected Boolean mRequestingLocationUpdates = true;

    /**
     * The desired interval for location updates. Inexact. Updates may be more
     * or less frequent.
     */
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 15000;

    /**
     * The fastest rate for active location updates. Exact. Updates will never
     * be more frequent than this value.
     */
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    /**
     * Provides the entry point to Google Play services.
     */
    protected GoogleApiClient client;

    /**
     * Represents a geographical location.
     */
    protected Location mLastLocation;

    protected LocationRequest mLocationRequest;

    public boolean userLoggedIn = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_layout);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, new LoginFragment()).commit();
        }


        globalAddress = GlobalDataSingleton.getInstance().GLOBAL_ADDRESS;

        buildGoogleApiClient();

        /*WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());


        Log.e("DEVICE IP ADDRESS: ", ip);*/

    }

    public void sendLocationToDb(String url) {

        FutureCallback<JsonObject> futureCallBack = new FutureCallback<JsonObject>() {

            @Override
            public void onCompleted(Exception e, JsonObject result) {
                if (e == null) {
                    // success on the request

                    String locationUpdateStatus = result.get("location_update")
                            .getAsString();
                    if (locationUpdateStatus != null
                            && locationUpdateStatus.equals("Success")) {

                        Log.d("LOCATION", "Location updated to database");

                    } else {
                        // message not sent
                        // handle the UI here

                        Log.d("LOCATION", "Location NOT updated");
                    }

                } else {
                    // u got network failures
                    Log.e("LOCATION", "NETWORK ERROR", e);

                }
            }
        };

        Ion.with(this).load(url)//
                .asJsonObject()//
                .setCallback(futureCallBack);

    }

    @Override
    public void onConnectionFailed(ConnectionResult arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onConnected(Bundle connectionHint) {

        Log.e("GOOGLE_CLIENT_STATUS:", "CONNECTED!");

        // Get the initial location of the user

        mLastLocation = LocationServices.FusedLocationApi
                .getLastLocation(client);
        if (mLastLocation != null) {

            latitude = String.valueOf(mLastLocation.getLatitude());
            longitude = String.valueOf(mLastLocation.getLongitude());

            Log.e("Initial Latitude", latitude);
            Log.e("Initial Longitude", longitude);

        } else {

            Toast.makeText(this, "GPS is disabled. Please enable Location", Toast.LENGTH_LONG)
                    .show();
        }

        if (mRequestingLocationUpdates) {

            startLocationUpdates();

        }

    }

    @Override
    public void onConnectionSuspended(int arg0) {
        // TODO Auto-generated method stub

        client.connect();

    }

    /**
     * Builds a GoogleApiClient. Uses the addApi() method to request the
     * LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        client = new GoogleApiClient.Builder(this).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        createLocationRequest();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval
        // is
        // inexact. You may not receive updates at all if no location sources
        // are available, or
        // you may receive them slower than requested. You may also receive
        // updates faster than
        // requested if other applications are requesting location at a faster
        // interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is
        // exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest
                .setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * Requests location updates from the FusedLocationApi.
     */
    protected void startLocationUpdates() {
        // The final argument to {@code requestLocationUpdates()} is a
        // LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        LocationServices.FusedLocationApi.requestLocationUpdates(client,
                mLocationRequest, this);
    }

    /**
     * Removes location updates from the FusedLocationApi.
     */
    protected void stopLocationUpdates() {
        // It is a good practice to remove location requests when the activity
        // is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.

        // The final argument to {@code requestLocationUpdates()} is a
        // LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        LocationServices.FusedLocationApi.removeLocationUpdates(client, this);
    }

    @Override
    protected void onStart() {

        client.connect();

        super.onStart();
    }

    @Override
    protected void onStop() {

        client.disconnect();

        super.onStop();
    }

    @Override
    public void onLocationChanged(Location location) {

        mLastLocation = location;

        latitude = String.valueOf(mLastLocation.getLatitude());

        longitude = String.valueOf(mLastLocation.getLongitude());

        Log.i("Location updated", "Latitude" + latitude + " -- " + "Longitude"
                + longitude);

        if (userLoggedIn) {

            userId = GlobalDataSingleton.getInstance().id;

            url = "http://" + globalAddress
                    + "/hatalink/UpdateLocation?UserID=" + userId
                    + "&LastLatitude=" + latitude + "&LastLongitude="
                    + longitude;

            sendLocationToDb(url);

            Log.i("Location status", "Sending to Database...");

        }
    }

    @Override
    protected void onResume() {

        if (client.isConnected() && mRequestingLocationUpdates) {

            startLocationUpdates();

        }
        super.onResume();
    }

    @Override
    protected void onPause() {

        stopLocationUpdates();

        super.onPause();
    }

}
