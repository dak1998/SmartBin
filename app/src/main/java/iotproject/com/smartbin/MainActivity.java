package iotproject.com.smartbin;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hanks.htextview.base.HTextView;

import java.util.ArrayList;

import iotproject.com.smartbin.Utils.BaseActivity;
import iotproject.com.smartbin.Utils.CustomAdapter1;
import iotproject.com.smartbin.Utils.MyTextViewBold;

public class MainActivity extends BaseActivity {

    int delay = 2000;
    Handler handler;
    ArrayList<String> arrMessages = new ArrayList<>();
    int position=0;

    DatabaseReference rootRef;

    HTextView hello;
    MyTextViewBold selectArea;
    ListView areaList;
    ProgressBar progressBar ;
    RelativeLayout loadingRL;

    ArrayList<String> areas, areaNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupToolbar();
        setTitle("Smart Bin");

        initUI();

        loadingRL.setVisibility(View.VISIBLE);
        areaList.setVisibility(View.GONE);

        rootRef = FirebaseDatabase.getInstance().getReference("");

        arrMessages.add("Hello User");
        arrMessages.add("Welcome To SmartBin");
        arrMessages.add("Select An Area");
        arrMessages.add("To View Bins");

        hello.animateText(arrMessages.get(position));

        handler = new Handler();
        handler.postDelayed(new Runnable(){
            public void run(){

                handler.postDelayed(this, delay);
                if(position>=arrMessages.size())
                    position=0;
                hello.animateText(arrMessages.get(position));
                position++;
            }
        }, delay);


        rootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                areas = new ArrayList<>();
                areaNames = new ArrayList<>();

                for ( DataSnapshot childDataSnapshot: dataSnapshot.getChildren() ) {
                    areas.add(childDataSnapshot.getKey());
                    areaNames.add(childDataSnapshot.child("group_name").getValue(String.class));
                }

                CustomAdapter1 ca1 = new CustomAdapter1(MainActivity.this,areas);
                areaList.setAdapter(ca1);

                areaList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Intent intent = new Intent(MainActivity.this, ViewAreas.class);
                        intent.putExtra("area", areas.get(i));
                        intent.putExtra("areaName", areaNames.get(i));
                        startActivity(intent);
                        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                    }
                });

                loadingRL.setVisibility(View.GONE);
                areaList.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    public void initUI() {

        hello = findViewById(R.id.helloTV);
        selectArea = findViewById(R.id.dateVC);
        areaList = findViewById(R.id.areasListView);
        progressBar = findViewById(R.id.listareasProgBar);
        loadingRL = findViewById(R.id.loadingRL);
    }
}
