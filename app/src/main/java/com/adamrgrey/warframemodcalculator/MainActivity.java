package com.adamrgrey.warframemodcalculator;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.NumberPicker;


public class MainActivity extends ActionBarActivity {

    NumberPicker startingRankPicker;
    NumberPicker targetRankPicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupNumberPickers();
        //setupSpinner();
        //calculate();
    }

    private void setupNumberPickers() {
        startingRankPicker = (NumberPicker) findViewById(R.id.startingRank);
        targetRankPicker = (NumberPicker) findViewById(R.id.targetRank);
        startingRankPicker.setMinValue(0);
        startingRankPicker.setMaxValue(10);
        targetRankPicker.setMinValue(0); //fallback in case somehow the minimum doesn't get updated. I doubt it's possible, but who knows
        targetRankPicker.setMaxValue(10);
        //TODO: add listeners for events
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
