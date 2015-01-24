package com.adamrgrey.warframemodcalculator;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {

    public static NumberPicker startingRankPicker;
    public static NumberPicker targetRankPicker;
    public static Spinner raritySpinner;
    private static final String LogTag = "MainActivity";
    private ListView outputListview;
    private ArrayAdapter outputArrayAdapter;
    private ArrayList<String> outputList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupNumberPickers();
        setupSpinner();
        calculate();
        renderOutput();
    }

    private void setupSpinner() {
        raritySpinner = (Spinner) findViewById(R.id.raritySpinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.rarities, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        raritySpinner.setAdapter(adapter);

        raritySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                calculate();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(getApplicationContext(), "WHAT. HOW. don't do that. select an actual rarity please.", Toast.LENGTH_SHORT);
                Log.e(LogTag, "rarity spinner: nothing selected. wtf.");
            }
        });
    }

    public void calculate() {
        //TODO: calculate
        outputList = new ArrayList<>();
        //TODO: populate output array
        renderOutput();
    }

    private void renderOutput() {
        outputListview = (ListView) findViewById(R.id.outputListView);
        outputArrayAdapter = new ArrayAdapter<>(this.getBaseContext(), android.R.layout.simple_list_item_1, outputList);
        outputListview.setAdapter(outputArrayAdapter);
    }

    private void setupNumberPickers() {
        startingRankPicker = (NumberPicker) findViewById(R.id.startingRank);
        targetRankPicker = (NumberPicker) findViewById(R.id.targetRank);
        startingRankPicker.setMinValue(0);
        startingRankPicker.setMaxValue(10);
        targetRankPicker.setMinValue(0); //fallback in case somehow the minimum doesn't get updated. I doubt it's possible, but who knows
        targetRankPicker.setMaxValue(10);

        startingRankPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                if(newVal > targetRankPicker.getValue())
                {
                    targetRankPicker.setValue(newVal);
                }
                targetRankPicker.setMinValue(newVal);
                calculate();
            }
        });
        targetRankPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                calculate();
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
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }
}
