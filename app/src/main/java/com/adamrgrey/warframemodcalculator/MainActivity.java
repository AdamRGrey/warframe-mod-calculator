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
        long r0duplicate = (long)Math.ceil(toRankUp / (getModEnergy(rarityValue, 0) * duplicateEfficiency));
        long r0samePolarityCommon = (long)Math.ceil(toRankUp / (getModEnergy(1, 0) * samePolarityAndFusionCoreEfficiency));
        long r0samePolarityUncommon = (long)Math.ceil(toRankUp / (getModEnergy(2, 0) * samePolarityAndFusionCoreEfficiency));
        long r0samePolarityRare = (long)Math.ceil(toRankUp / (getModEnergy(3, 0) * samePolarityAndFusionCoreEfficiency));
        long r0unrelatedCommon = (long)Math.ceil(toRankUp / (getModEnergy(1, 0) * unrelatedEfficiency));
        long r0unrelatedUncommon = (long)Math.ceil(toRankUp / (getModEnergy(2, 0) * unrelatedEfficiency));
        long r0unrelatedRare = (long)Math.ceil(toRankUp / (getModEnergy(3, 0) * unrelatedEfficiency));
        long r1commonCore = (long)Math.ceil(toRankUp / (getCoreEnergy(1, 1) * samePolarityAndFusionCoreEfficiency));
        long r2commonCore = (long)Math.ceil(toRankUp / (getCoreEnergy(1, 2) * samePolarityAndFusionCoreEfficiency));
        long r3commonCore = (long)Math.ceil(toRankUp / (getCoreEnergy(1, 3) * samePolarityAndFusionCoreEfficiency));
        long r0uncommonCore = (long)Math.ceil(toRankUp / (getCoreEnergy(2, 0) * samePolarityAndFusionCoreEfficiency));
        long r5uncommonCore = (long)Math.ceil(toRankUp / (getCoreEnergy(2, 5) * samePolarityAndFusionCoreEfficiency));
        long r0rareCore = (long)Math.ceil(toRankUp / (getCoreEnergy(3, 0) * samePolarityAndFusionCoreEfficiency));
        long r5rareCore = (long)Math.ceil(toRankUp / (getCoreEnergy(3, 5) * samePolarityAndFusionCoreEfficiency));

        //TODO: credits
        long r0duplicateCredits = r0duplicate * getModCreditCost((int)rarityValue);
        long r0samePolarityCommonCredits = r0samePolarityCommon * getModCreditCost(1);
        long r0samePolarityUncommonCredits = r0samePolarityUncommon * getModCreditCost(2);
        long r0samePolarityRareCredits = r0samePolarityRare * getModCreditCost(3);
        long r0unrelatedCommonCredits = r0unrelatedCommon * getModCreditCost(1);
        long r0unrelatedUncommonCredits = r0unrelatedUncommon * getModCreditCost(2);
        long r0unrelatedRareCredits = r0unrelatedRare * getModCreditCost(3);
        long r1commonCoreCredits = r1commonCore * getCoreCreditCost(1, 1);
        long r2commonCoreCredits = r2commonCore * getCoreCreditCost(1, 2);
        long r3commonCoreCredits = r3commonCore * getCoreCreditCost(1, 3);
        long r0uncommonCoreCredits = r0uncommonCore * getCoreCreditCost(2, 0);
        long r5uncommonCoreCredits = r5uncommonCore * getCoreCreditCost(2, 5);
        long r0rareCoreCredits = r0rareCore * getCoreCreditCost(3, 0);
        long r5rareCoreCredits = r5rareCore * getCoreCreditCost(3, 5);

        outputList = new ArrayList<>();
        outputList.add("Energy Cost: " + toRankUp);
        outputList.add("r0 duplicate: " + r0duplicate + "x + cr" + r0duplicateCredits);
        outputList.add("r0 same Polarity Common: " + r0samePolarityCommon + "x + cr" + r0samePolarityCommonCredits);
        outputList.add("r0 same Polarity Uncommon: " + r0samePolarityUncommon + "x + cr" + r0samePolarityUncommonCredits);
        outputList.add("r0 same Polarity Rare: " + r0samePolarityRare + "x + cr" + r0samePolarityRareCredits);
        outputList.add("r0 unrelated Common: " + r0unrelatedCommon + "x + cr" + r0unrelatedCommonCredits);
        outputList.add("r0 unrelated Uncommon: " + r0unrelatedUncommon + "x + cr" + r0unrelatedUncommonCredits);
        outputList.add("r0 unrelated Rare: " + r0unrelatedRare + "x + cr" + r0unrelatedRareCredits);
        outputList.add("r1 common Core: " + r1commonCore + "x + cr" + r1commonCoreCredits);
        outputList.add("r2 common Core: " + r2commonCore + "x + cr" + r2commonCoreCredits);
        outputList.add("r3 common Core: " + r3commonCore + "x + cr" + r3commonCoreCredits);
        outputList.add("r0 uncommon Core: " + r0uncommonCore + "x + cr" + r0uncommonCoreCredits);
        outputList.add("r5 uncommon Core: " + r5uncommonCore + "x + cr" + r5uncommonCoreCredits);
        outputList.add("r0 rare Core: " + r0rareCore + "x + cr" + r0rareCoreCredits);
        outputList.add("r5 rare Core: " + r5rareCore + "x + cr" + r5rareCoreCredits);

        renderOutput();
    }

    private long getCoreCreditCost(int rarityValue,int level) {
        return (300 * rarityValue) + level * (150 * rarityValue);
    }
    private long getModCreditCost(int rarityValue){
//        //primed mods cost 1200 + (600 * level)
//        // other mods just cost 300 * rarityValue, regardless of level
//        return (300 * rarityValue) + (rarityValue > 3 ? (150 * rarityValue) * level: 0);
        //...but fuck all that, I'm not displaying level != 0 for mods
        return 300 * rarityValue;
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
