package kr.edcan.lumihana.itravelu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity implements DistanceService.OnDistanceInfoUpdateListener {
    private static final int pgmax = 4;

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private MainPageAdapter mainPageAdapter;
    private FirebaseDatabase firebaseDatabase;
    private ArrayList<String> travelTypes = new ArrayList<>();
    private ArrayList<String> foodTypes = new ArrayList<>();
    private Realm realm;
    private DistanceService distanceService;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mainPageAdapter = new MainPageAdapter(getSupportFragmentManager());

        toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        tabLayout = (TabLayout) findViewById(R.id.main_tab);
        tabLayout.addTab(tabLayout.newTab().setText(R.string.main_tab_main));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.main_tab_travel));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.main_tab_food));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.main_tab_theme));

        viewPager = (ViewPager) findViewById(R.id.main_viewpager);
        viewPager.setAdapter(mainPageAdapter);
        viewPager.setSaveEnabled(false);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        distanceService = new DistanceService(MainActivity.this);
        distanceService.setOnDistanceUpdateListener(this);
        startService(new Intent(MainActivity.this, DistanceService.class));

        realm = Realm.getDefaultInstance();
        downloadData();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void downloadData() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference root = firebaseDatabase.getReference().getRoot();
        final DatabaseReference travel = root.getDatabase().getReference().child("travel");
        travel.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long value = dataSnapshot.getChildrenCount();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    travelTypes.add(snapshot.getKey());
                }

                for (final String travelType : travelTypes) {
                    final DatabaseReference reference = dataSnapshot.getRef().child(travelType);
                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                realm.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        final InfoModel infoModel = snapshot.getValue(InfoModel.class);
                                        final RealmInfoModel realmInfoModel = new RealmInfoModel(
                                                snapshot.getKey() + "", travelType, infoModel);

                                        realm.insertOrUpdate(realmInfoModel);
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            downloadfailed();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                downloadfailed();
            }
        });

        final DatabaseReference food = root.getDatabase().getReference().child("food");
        food.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long value = dataSnapshot.getChildrenCount();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    foodTypes.add(snapshot.getKey());
                }

                for (final String foodType : foodTypes) {
                    final DatabaseReference reference = dataSnapshot.getRef().child(foodType);
                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                realm.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        final InfoModel infoModel = snapshot.getValue(InfoModel.class);
                                        final RealmInfoModel realmInfoModel = new RealmInfoModel(
                                                snapshot.getKey() + "", foodType, infoModel);

                                        realm.insertOrUpdate(realmInfoModel);
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            downloadfailed();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                downloadfailed();
            }
        });

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                final RealmResults<RealmInfoModel> results = realm.where(RealmInfoModel.class).equalTo("name", "gyeongbokgung").findAll();
                for (RealmInfoModel result : results) {
                    Log.e("gyeongbokgung", result.getAbout());
                }
            }
        });
    }

    private void downloadfailed() {
        Toast.makeText(getApplicationContext(), "DB Update Error", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.main_search: {
                startActivity(new Intent(MainActivity.this, SearchActivity.class));
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://kr.edcan.lumihana.itravelu/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://kr.edcan.lumihana.itravelu/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    @Override
    public void OnDistanceUpdateListener() {

    }

    public final class MainPageAdapter extends FragmentStatePagerAdapter {

        public MainPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return MainFragment.getInstance(position);
        }

        @Override
        public int getCount() {
            return 4;
        }
    }

    public static final class MainFragment extends Fragment implements OnMapReadyCallback {
        private static final String BUNDLE_PAGE_NUMBER = "pgnum";
        private static final int PAGE_MAPS = 0;
        private static final int PAGE_TRAVEL = 1;
        private static final int PAGE_FOOD = 2;
        private static final int PAGE_THEME = 3;
        private static int currentPg = PAGE_MAPS;

        private GoogleMap map;
        private MapView mapView;
        private DistanceService distanceService;

        public final void MainFragment() {
        }

        public static final MainFragment getInstance(int pgnum) {
            MainFragment fragment = new MainFragment();
            Bundle bundle = new Bundle();
            bundle.putInt(BUNDLE_PAGE_NUMBER, pgnum);

            fragment.setArguments(bundle);
            return fragment;
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            int pgnum = getArguments().getInt(BUNDLE_PAGE_NUMBER);
            View view = null;
            if (mapView != null) {
                mapView.onDestroy();
                mapView = null;
            }

            switch (pgnum) {
                case PAGE_MAPS:
                    view = inflater.inflate(R.layout.main_maps, container, false);
                    currentPg = PAGE_MAPS;
                    break;
                case PAGE_TRAVEL:
                    view = inflater.inflate(R.layout.main_travel, container, false);
                    currentPg = PAGE_TRAVEL;
                    break;
                case PAGE_FOOD:
                    view = inflater.inflate(R.layout.main_food, container, false);
                    currentPg = PAGE_FOOD;
                    break;
                case PAGE_THEME:
                    view = inflater.inflate(R.layout.main_theme, container, false);
                    currentPg = PAGE_THEME;
                    break;
                default:
                    view = null;
            }

            onInitView(view, container, pgnum, inflater, savedInstanceState);
            return view;
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            if (mapView != null) {
                mapView.onSaveInstanceState(outState);
            }
        }

        @Override
        public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
            super.onViewStateRestored(savedInstanceState);
        }

        private void onInitView(View view, final ViewGroup container, int position, LayoutInflater inflater, Bundle savedInstanceSate) {
            switch (position) {
                case PAGE_MAPS: {
                    mapView = (MapView) view.findViewById(R.id.maps_mapview);
                    mapView.getMapAsync(this);
                    mapView.onCreate(savedInstanceSate);
                    mapView.setSaveEnabled(false);
                    mapView.onResume();

                    final FloatingActionButton button_locate = (FloatingActionButton) view.findViewById(R.id.fab_locate);
                    button_locate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (map != null) mainMapMarkerLoader();
                        }
                    });

                    break;
                }

                case PAGE_TRAVEL: {
                    final View here = view;
                    final ArrayList<String> arrayList = new ArrayList<>();
                    arrayList.add("highlights");
                    arrayList.add("history");
                    arrayList.add("park");

                    AppCompatSpinner spinner = (AppCompatSpinner) view.findViewById(R.id.travel_spinner_tag);
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, arrayList);
                    spinner.setAdapter(arrayAdapter);
                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            setList(arrayList.get(i), here, R.id.travel_recycler_list, R.id.travel_image_photo, R.id.travel_text_name, R.id.travel_text_desc);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {
                        }
                    });
                    break;
                }

                case PAGE_FOOD: {
                    final View here = view;
                    final ArrayList<String> arrayList = new ArrayList<>();
                    arrayList.add("highlights");
                    arrayList.add("history");
                    arrayList.add("park");

                    AppCompatSpinner spinner = (AppCompatSpinner) view.findViewById(R.id.food_spinner_tag);
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, arrayList);
                    spinner.setAdapter(arrayAdapter);
                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            setList(arrayList.get(i), here, R.id.food_recycler_list, R.id.food_image_photo, R.id.food_text_name, R.id.food_text_desc);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {
                        }
                    });
                    break;
                }

                case PAGE_THEME: {
                    mapView = (MapView) view.findViewById(R.id.maps_mapview);
                    mapView.getMapAsync(this);
                    mapView.onCreate(savedInstanceSate);
                    mapView.setSaveEnabled(false);
                    mapView.onResume();

                    final View here = view;
                    SharedPreferences sharedPreferences = getContext().getSharedPreferences("Setting", getContext().MODE_PRIVATE);
                    final String userId = sharedPreferences.getString("userId", "");
                    final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                    final FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                    final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                    final DatabaseReference root = firebaseDatabase.getReference().getRoot();
                    final DatabaseReference user = root.getRef().child("user");
                    final DatabaseReference myUserReference = user.child(userId + "");
                    final ArrayList<String> pointeds = new ArrayList<>();
                    myUserReference.child("pointed").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                pointeds.add(snapshot.getKey() + "");
                            }

                            setMap(here, pointeds);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                    final ArrayList<String> favorites = new ArrayList<>();
                    myUserReference.child("favorite").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot children : dataSnapshot.getChildren()) {
                                favorites.add(children.getKey() + "");
                            }

                            setList(here, favorites);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.e("error", databaseError.getMessage() + "");
                        }
                    });
                    break;
                }
            }
        }

        private void setMap(View here, ArrayList<String> pointeds) {
            if (map != null) {
                if (pointeds.size() <= 0) {
                    Log.e("fac", "null");
                } else {
                    Realm realm = Realm.getDefaultInstance();
                    final ArrayList<RealmInfoModel> arrayList = new ArrayList<>();

                    for (String favorite : pointeds) {
                        RealmInfoModel result = realm.where(RealmInfoModel.class).equalTo("name", favorite).findFirst();
                        if (result != null) {
                            arrayList.add(result);
                        }
                    }

                    int cnt = 0;

                    map.clear();

                    for (RealmInfoModel model : arrayList) {
                        final MarkerOptions marker = new MarkerOptions();
                        marker.position(new LatLng(model.getLat(), model.getLong()));
                        marker.title(model.getName());

                        map.addMarker(marker).setTag(cnt++);
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(model.getLat(), model.getLong()), 16));
                    }
                }
            }
        }

        private void setList(View view, ArrayList<String> favorites) {
            if (favorites.size() <= 0) {
                Log.e("fac", "null");
            } else {
                Realm realm = Realm.getDefaultInstance();
                ArrayList<RealmInfoModel> arrayList = new ArrayList<>();

                for (String favorite : favorites) {
                    RealmInfoModel result = realm.where(RealmInfoModel.class).equalTo("name", favorite).findFirst();
                    if (result != null) {
                        arrayList.add(result);
                        Log.e("hll", result.getLat() + "");
                    }
                }

                RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.theme_recycler);
                GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
                gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                InfoAdapter infoAdapter = new InfoAdapter(getContext(), arrayList);
                recyclerView.setLayoutManager(gridLayoutManager);
                recyclerView.setAdapter(infoAdapter);

                Log.e("view", "create");
            }
        }

        private void setList(String tag, View view, int recyclerRedId, int photoResId, int nameResId, int descResId) {
            Realm realm = Realm.getDefaultInstance();
            RealmResults<RealmInfoModel> results = realm.where(RealmInfoModel.class).equalTo("tag", tag).findAllSorted("name");
            if (results.size() <= 0)
                Toast.makeText(getContext(), "no data found.", Toast.LENGTH_SHORT).show();
            else {
                final RealmInfoModel mainModel = results.get(0);
                final String mainPhotoUrl = mainModel.getPhoto();
                final String name = mainModel.getName();
                final String desc = mainModel.getAbout();

                ImageView image_photo = (ImageView) view.findViewById(photoResId);
                Glide.with(getContext()).load(mainPhotoUrl).into(image_photo);
                TextView text_name = (TextView) view.findViewById(nameResId);
                text_name.setText(name + "");
                TextView textView_des = (TextView) view.findViewById(descResId);
                textView_des.setText(desc + "");

                ArrayList<RealmInfoModel> arrayList = new ArrayList<>();
                for (RealmInfoModel model : results) {
                    arrayList.add(model);
                }

                RecyclerView recycler_list = (RecyclerView) view.findViewById(recyclerRedId);
                InfoAdapter infoAdapter = new InfoAdapter(getContext(), arrayList);
                GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
                gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                recycler_list.setLayoutManager(gridLayoutManager);
                recycler_list.setAdapter(infoAdapter);
            }
        }

        @Override
        public void onMapReady(GoogleMap googleMap) {
            this.map = googleMap;

            if (currentPg == PAGE_MAPS) {
                mainMapMarkerLoader();
            }
        }

        private void mainMapMarkerLoader() {
            Realm realm = Realm.getDefaultInstance();
            RealmResults<RealmInfoModel> results = realm.where(RealmInfoModel.class).findAllSorted("name");
            if (results.size() <= 0) {
                Toast.makeText(getContext(), "no nearby places", Toast.LENGTH_SHORT).show();
            } else {
                map.clear();

                for (RealmInfoModel model : results) {
                    if (model.getDistanceFromMe() <= 300.0) {
                        final MarkerOptions marker = new MarkerOptions();
                        marker.title(model.getName());
                        marker.position(new LatLng(model.getLat(), model.getLong()));

                        map.addMarker(marker);
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(model.getLat(), model.getLong()), 16));

                    }
                }
                Toast.makeText(getContext(), "display nearby places", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
