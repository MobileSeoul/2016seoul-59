package kr.edcan.lumihana.itravelu;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import io.realm.Realm;
import io.realm.RealmResults;
import android.content.Context;

/**
 * Created by kimok_000 on 2016-10-30.
 */
public class DistanceService extends Service implements LocationListener {
    private double myLat, myLon;
    private static OnDistanceInfoUpdateListener listener;
    private LocationManager locationManager;
    private Location myLocation;
    private static Context context;

    public DistanceService(){}

    public DistanceService(Context context) {
        this.context = context;
    }

    @Override
    public void onLocationChanged(Location location) {
        this.myLocation = location;
        myLat = location.getLatitude();
        myLon = location.getLongitude();
        updateDistance();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    public interface OnDistanceInfoUpdateListener {
        void OnDistanceUpdateListener();
    }

    public void setOnDistanceUpdateListener(OnDistanceInfoUpdateListener onDistanceUpdateListener) {
        this.listener = onDistanceUpdateListener;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 60 * 1000 * 15, 100.0f, this);
        } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60 * 1000 * 15, 100.0f, this);
        }
    }

    private void updateDistance(){
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("Setting", MODE_PRIVATE);
        final String userId = sharedPreferences.getString("userId", "");
        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference root = firebaseDatabase.getReference().getRoot();
        final DatabaseReference user = root.getRef().child("user");
        final DatabaseReference myUserReference = user.child(userId);
        final DatabaseReference favorite = myUserReference.child("pointed");

        Realm realm = Realm.getDefaultInstance();
        RealmResults<RealmInfoModel> models = realm.where(RealmInfoModel.class).findAll();
        if (models.size() <= 0){
            Log.e("distanc", "no base data");
        }
        else {
            for (final RealmInfoModel model : models) {
                final Location location = new Location(model.getName() + "");
                location.setLatitude(model.getLat());
                location.setLongitude(model.getLong());

                final double distance = myLocation.distanceTo(location);
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        model.setDistanceFromMe(distance);
                    }
                });
                Log.e(model.getName() + "", distance + "m;");

                if (distance <= 300.0) {
                    favorite.child(model.getName()).setValue(System.currentTimeMillis(), new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError == null) {
                                Log.e("text", "pointed : " + model.getName());
                            } else {
                                Log.e("text", "error");
                            }
                        }
                    });
                }
            }
        }

        if (listener != null)
            listener.OnDistanceUpdateListener();
    }
}
