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
        double currentRank  = startingRankPicker.getValue();

        double ranksDesired = targetRankPicker.getValue() - currentRank ;
        double toRankUp = getRequiredEnergy(rarityValue, currentRank, ranksDesired);

        double duplicateEfficiency = 1.0;
        double samePolarityAndFusionCoreEfficiency = 0.5;
        double unrelatedEfficiency = 0.25;
        double r0duplicate = toRankUp / (getModEnergy(rarityValue, 0) * duplicateEfficiency);
        double r0samePolarityCommon = toRankUp / (getModEnergy(1, 0) * samePolarityAndFusionCoreEfficiency);
        double r0samePolarityUncommon = toRankUp / (getModEnergy(2, 0) * samePolarityAndFusionCoreEfficiency);
        double r0samePolarityRare = toRankUp / (getModEnergy(3, 0) * samePolarityAndFusionCoreEfficiency);
        double r0unrelatedCommon = toRankUp / (getModEnergy(1, 0) * unrelatedEfficiency);
        double r0unrelatedUncommon = toRankUp / (getModEnergy(2, 0) * unrelatedEfficiency);
        double r0unrelatedRare = toRankUp / (getModEnergy(3, 0) * unrelatedEfficiency);
        double r1commonCore = toRankUp / (getCoreEnergy(1, 1) * samePolarityAndFusionCoreEfficiency);
        double r2commonCore = toRankUp / (getCoreEnergy(1, 2) * samePolarityAndFusionCoreEfficiency);
        double r3commonCore = toRankUp / (getCoreEnergy(1, 3) * samePolarityAndFusionCoreEfficiency);
        double r0uncommonCore = toRankUp / (getCoreEnergy(2, 0) * samePolarityAndFusionCoreEfficiency);
        double r5uncommonCore = toRankUp / (getCoreEnergy(2, 5) * samePolarityAndFusionCoreEfficiency);
        double r0rareCore = toRankUp / (getCoreEnergy(3, 0) * samePolarityAndFusionCoreEfficiency);
        double r5rareCore = toRankUp / (getCoreEnergy(3, 5) * samePolarityAndFusionCoreEfficiency);

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
     * @param rank 0-based
     * @return fusion energy contained
     */
    private double getModEnergy(double rarityValue, double rank) {
        return 2 * rarityValue * (2 + rank );
    }

    /**
     * cores work differently from mods
     * @param rarityValue - 1=common, and so on
     * @param rank 0-based
     * @return fusion energy contained
     */
    private double getCoreEnergy(double rarityValue, double rank){
        switch ((int) rarityValue)
        {
            case 1:
                return 2.6 * (rank) + 2;
            case 2:
                return 6 * rank + 4;
            case 3:
                return 10 * rank + 12;
        }
        Log.e(LogTag, "invalid rarity value " + rarityValue + "!");
        throw new IllegalArgumentException("rarityValue invalid! must be 1-3, given " + rarityValue + "(which gets intcasted to " + (int)rarityValue + ")!");
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
