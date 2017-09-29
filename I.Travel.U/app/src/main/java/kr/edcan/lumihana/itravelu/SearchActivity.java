package kr.edcan.lumihana.itravelu;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

public class SearchActivity extends AppCompatActivity implements OnMapReadyCallback {
    private EditText edit_search;
    private MapView mapView;
    private GoogleMap map;
    private RecyclerView recycler;
    private GridLayoutManager gridLayoutManager;
    private InfoAdapter infoAdapter;
    private Toolbar toolbar;
    private TextView text_date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        toolbar = (Toolbar) findViewById(R.id.search_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mapView = (MapView) findViewById(R.id.maps_mapview);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);

        text_date = (TextView) findViewById(R.id.search_text_date);
        edit_search = (EditText) findViewById(R.id.search_edit);
        edit_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String temp = editable.toString().trim();

                if (temp != null)
                    onSearchAction(temp);
            }
        });
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
                final String temp = edit_search.getText().toString().trim();
                if (!temp.equals("")) onSearchAction(temp);
                break;
            }

            case android.R.id.home: {
                finish();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void onSearchAction(String data) {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<RealmInfoModel> results = realm.where(RealmInfoModel.class).contains("name", data).findAllSorted("name");
        if (results.size() <= 0)
            Toast.makeText(getApplicationContext(), "no data found", Toast.LENGTH_SHORT).show();
        else {
            ArrayList<RealmInfoModel> arrayList = new ArrayList<>();
            for (RealmInfoModel realmInfoModel : results) arrayList.add(realmInfoModel);

            InfoAdapter infoAdapter = new InfoAdapter(SearchActivity.this, arrayList);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
            gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            RecyclerView recyclerView = (RecyclerView) findViewById(R.id.search_recycler);
            recyclerView.setLayoutManager(gridLayoutManager);
            recyclerView.setAdapter(infoAdapter);

            if (map != null) {
                final RealmInfoModel first = arrayList.get(0);
                final MarkerOptions marker = new MarkerOptions();
                marker.position(new LatLng(first.getLat(), first.getLong()));
                marker.title(first.getName());

                map.addMarker(marker);
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(first.getLat(), first.getLong()), 16));
                map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        final DetailDialog detailDialog = new DetailDialog(SearchActivity.this, first);
                        detailDialog.show();
                        return true;
                    }
                });

                text_date.setText(first.getHours() + "");
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
    }
}
