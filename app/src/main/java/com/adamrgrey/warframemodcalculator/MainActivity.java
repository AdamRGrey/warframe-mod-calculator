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
        double rarityValue = raritySpinner.getSelectedItemPosition() + 1;
        Log.d(LogTag, "RarityValue = " + rarityValue);
        double currentRank  = startingRankPicker.getValue();
        Log.d(LogTag, "currentRank  = " + currentRank );
        double currentEnergy = getFusionEnergy(rarityValue, currentRank);
        Log.d(LogTag, "currentEnergy = " + currentEnergy);

        double ranksDesired = targetRankPicker.getValue() - currentRank ;
        Log.d(LogTag, "ranksDesired = " + ranksDesired);
        double toRankUp = getRequiredEnergy(rarityValue, currentRank, ranksDesired);
        Log.d(LogTag, "toRankUp = " + toRankUp);

        double duplicateEfficiency = 1.0;
        double samePolarityAndFusionCoreEfficiency = 0.5;
        double unrelatedEfficiency = 0.25;
        double r0duplicate = toRankUp / (getFusionEnergy(rarityValue, 0) * duplicateEfficiency);
        double r0samePolarityCommon = toRankUp / (getFusionEnergy(1, 0) * samePolarityAndFusionCoreEfficiency);
        double r0samePolarityUncommon = toRankUp / (getFusionEnergy(2, 0) * samePolarityAndFusionCoreEfficiency);
        double r0samePolarityRare = toRankUp / (getFusionEnergy(3, 0) * samePolarityAndFusionCoreEfficiency);
        double r0unrelatedCommon = toRankUp / (getFusionEnergy(1, 0) * unrelatedEfficiency);
        double r0unrelatedUncommon = toRankUp / (getFusionEnergy(2, 0) * unrelatedEfficiency);
        double r0unrelatedRare = toRankUp / (getFusionEnergy(3, 0) * unrelatedEfficiency);
        double r1commonCore = toRankUp / (getFusionEnergy(1, 1) * samePolarityAndFusionCoreEfficiency);
        double r2commonCore = toRankUp / (getFusionEnergy(1, 2) * samePolarityAndFusionCoreEfficiency);
        double r3commonCore = toRankUp / (getFusionEnergy(1, 3) * samePolarityAndFusionCoreEfficiency);
        double r0uncommonCore = toRankUp / (getFusionEnergy(2, 0) * samePolarityAndFusionCoreEfficiency);
        double r5uncommonCore = toRankUp / (getFusionEnergy(2, 5) * samePolarityAndFusionCoreEfficiency);
        double r0rareCore = toRankUp / (getFusionEnergy(3, 0) * samePolarityAndFusionCoreEfficiency);
        double r5rareCore = toRankUp / (getFusionEnergy(3, 5) * samePolarityAndFusionCoreEfficiency);
        Log.d(LogTag, "takes " + r5rareCore + " rare5's");

        //TODO: credits


        outputList = new ArrayList<>();
        outputList.add("r0 duplicate: " + r0duplicate);
        outputList.add("r0 same Polarity Common: " + r0samePolarityCommon);
        outputList.add("r0 same Polarity Uncommon: " + r0samePolarityUncommon);
        outputList.add("r0 same Polarity Rare: " + r0samePolarityRare);
        outputList.add("r0 unrelated Common: " + r0unrelatedCommon);
        outputList.add("r0 unrelated Uncommon: " + r0unrelatedUncommon);
        outputList.add("r0 unrelated Rare: " + r0unrelatedRare);
        outputList.add("r1 common Core: " + r1commonCore);
        outputList.add("r2 common Core: " + r2commonCore);
        outputList.add("r3 common Core: " + r3commonCore);
        outputList.add("r0 uncommon Core: " + r0uncommonCore);
        outputList.add("r5 uncommon Core: " + r5uncommonCore);
        outputList.add("r0 rare Core: " + r0rareCore);
        outputList.add("r5 rare Core: " + r5rareCore);

        renderOutput();
    }

    /**
     * given a mod and a desired number of ranks, how much fusion energy would it take to level it up
     * a given number of ranks?
     * @param rarityValue
     * @param currentRank
     * @param ranksDesired
     * @return fusion energy required
     */
    private double getRequiredEnergy(double rarityValue, double currentRank, double ranksDesired) {
        double toRankUp = 0;
        for (int i = 0; i < ranksDesired; i++) {
            toRankUp += 4.0 * rarityValue * Math.pow(2, currentRank +i);
        }
        return toRankUp;
    }

    /**
     * given a mod, how much energy does it have?
     * @param rarityValue 1=common, and so on
     * @param currentRank 0-based
     * @return fusion energy contained
     */
    private double getFusionEnergy(double rarityValue, double currentRank) {
        return 2 * rarityValue * (2 + currentRank );
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
