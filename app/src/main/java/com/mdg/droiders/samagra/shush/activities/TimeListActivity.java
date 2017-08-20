package com.mdg.droiders.samagra.shush.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.mdg.droiders.samagra.shush.R;
import com.mdg.droiders.samagra.shush.adapters.TimeListAdapter;

public class TimeListActivity extends AppCompatActivity {

    private RecyclerView timeRecycler;
    private TimeListAdapter timeListAdapter;
    private boolean isActivityCreated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_list);
        isActivityCreated = true;
        timeRecycler = findViewById(R.id.time_list_recycler);
        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (timeListAdapter != null) {
                    timeListAdapter.addItem();
                }
            }
        });
        timeListAdapter = new TimeListAdapter(this);
        timeRecycler.setLayoutManager(new LinearLayoutManager(this));
        timeRecycler.setAdapter(timeListAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isActivityCreated) {
            timeListAdapter.refreshCursor();
        }
        isActivityCreated = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timeListAdapter.closeCursor();
    }

}
