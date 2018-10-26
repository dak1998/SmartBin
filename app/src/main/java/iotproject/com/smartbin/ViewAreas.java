package iotproject.com.smartbin;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import iotproject.com.smartbin.Utils.BaseActivity;
import iotproject.com.smartbin.Utils.CustomAdapter1;
import iotproject.com.smartbin.Utils.MyTextViewBold;

public class ViewAreas extends BaseActivity {

    String area, areaName;
    DatabaseReference areaRef;

    MyTextViewBold selectBin;
    ListView listBins;
    ProgressBar progressBar;

    ArrayList<String> binNames, binIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_areas);

        setupToolbar();
        setTitle("Select A Bin");
        area = getIntent().getStringExtra("area");
        areaName = getIntent().getStringExtra("areaName");

        initUI();

        selectBin.setText("From Area: "+areaName);
        listBins.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        areaRef = FirebaseDatabase.getInstance().getReference(area+"/bins");

        areaRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                binNames = new ArrayList<>();
                binIds = new ArrayList<>();
                for(DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    binNames.add(childSnapshot.child("bin_name").getValue(String.class));
                    binIds.add(childSnapshot.getKey());
                }

                CustomAdapter1 customAdapter1 = new CustomAdapter1(ViewAreas.this,binNames);
                listBins.setAdapter(customAdapter1);

                listBins.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Intent intent = new Intent(ViewAreas.this, BinStats.class);
                        intent.putExtra("area", area);
                        intent.putExtra("areaName", areaName);
                        intent.putExtra("binId", binIds.get(i));
                        startActivity(intent);
                        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                    }
                });

                progressBar.setVisibility(View.GONE);
                listBins.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void initUI() {
        listBins = findViewById(R.id.BinsListView);
        selectBin = findViewById(R.id.binTV);
        progressBar = findViewById(R.id.BinsProgBar);
    }
}
