package iotproject.com.smartbin;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hanks.htextview.base.HTextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import iotproject.com.smartbin.Utils.BaseActivity;
import iotproject.com.smartbin.Utils.MyTextView;
import iotproject.com.smartbin.Utils.MyTextViewBold;

public class BinStats extends BaseActivity {

    int delay = 2000;
    Handler handler;
    ArrayList<String> arrMessages = new ArrayList<>();
    int position=0;

    String binId, areaId, areaName;
    DatabaseReference binRef;

    MyTextView binLevel, binEmpty;
    MyTextViewBold binNameTV;
    HTextView messageTV;
    ProgressBar progressBar;
    Button emptyButton;

    HashMap<String,Object> binData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bin_stats);

        setupToolbar();
        setTitle("Bin Statistics");

        initUI();

        binId = getIntent().getStringExtra("binId");
        areaId = getIntent().getStringExtra("area");
        areaName = getIntent().getStringExtra("areaName");

        progressBar.setVisibility(View.VISIBLE);
        messageTV.setVisibility(View.INVISIBLE);
        binNameTV.setVisibility(View.INVISIBLE);
        binEmpty.setVisibility(View.INVISIBLE);
        binLevel.setVisibility(View.INVISIBLE);
        emptyButton.setVisibility(View.INVISIBLE);


        binRef = FirebaseDatabase.getInstance().getReference("/"+areaId+"/bins/"+binId);

        binRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                binData = (HashMap<String, Object>) dataSnapshot.getValue();

                double binLevelFilled = (double)binData.get("level");
                long time = (long)binData.get("last_emptied");

                DateFormat simple = new SimpleDateFormat("dd MMM yyy HH:mm:ss");
                Date finalDate = new Date(time);

                binNameTV.setText(binData.get("bin_name").toString()+" Bin,"+areaName);
                binLevel.setText("Level Filled "+binData.get("level").toString()+"%");
                binEmpty.setText("Last Emptied: "+simple.format(finalDate));

                messageTV.setVisibility(View.VISIBLE);


                long now = System.currentTimeMillis();
                long diff = now - time;
                long hours = ((diff / (1000*60*60)) % 24);

                if ( binLevelFilled > 75 ) {
                    emptyLevel(binLevelFilled);
                }
                else if ( hours > 48)
                    emptyTime();
                else
                    relax();

                emptyButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(BinStats.this, QRCode.class);
                        intent.putExtra("areaId", areaId);
                        intent.putExtra("binId", binId);
                        startActivity(intent);
                        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                    }
                });

                progressBar.setVisibility(View.GONE);
                binLevel.setVisibility(View.VISIBLE);
                binEmpty.setVisibility(View.VISIBLE);
                binNameTV.setVisibility(View.VISIBLE);
                emptyButton.setVisibility(View.VISIBLE);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void initUI() {
        binNameTV = findViewById(R.id.binNameTV);
        binLevel = findViewById(R.id.binLevel);
        binEmpty = findViewById(R.id.binLastEmpty);
        progressBar = findViewById(R.id.BinsProgBar);
        messageTV = findViewById(R.id.messageTV);
        emptyButton = findViewById(R.id.emptyButton);
    }

    public void relax() {
        arrMessages.add("No Action Needed");
        arrMessages.add("Required");

        messageTV.setTextColor(getResources().getColor(R.color.colorPrimaryDark));

        messageTV.animateText(arrMessages.get(position));

        handler = new Handler();
        handler.postDelayed(new Runnable(){
            public void run(){

                handler.postDelayed(this, delay);
                if(position>=arrMessages.size())
                    position=0;
                messageTV.animateText(arrMessages.get(position));
                position++;
            }
        }, delay);

    }

    public void emptyTime() {
        arrMessages.add("48 Hours elapsed");
        arrMessages.add("Since last Cleanup");
        arrMessages.add("Empty Bin ASAP");

        messageTV.setTextColor(getResources().getColor(R.color.red));

        messageTV.animateText(arrMessages.get(position));

        handler = new Handler();
        handler.postDelayed(new Runnable(){
            public void run(){

                handler.postDelayed(this, delay);
                if(position>=arrMessages.size())
                    position=0;
                messageTV.animateText(arrMessages.get(position));
                position++;
            }
        }, delay);

    }

    public void emptyLevel( double level ) {
        arrMessages.add("Garbage Level At "+level+"%");
        arrMessages.add("Empty Bin ASAP");

        messageTV.setTextColor(getResources().getColor(R.color.red));

        messageTV.animateText(arrMessages.get(position));

        handler = new Handler();
        handler.postDelayed(new Runnable(){
            public void run(){

                handler.postDelayed(this, delay);
                if(position>=arrMessages.size())
                    position=0;
                messageTV.animateText(arrMessages.get(position));
                position++;
            }
        }, delay);

    }
}
