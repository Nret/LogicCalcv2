package com.logiccalcv2;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

/**
 * Created by ProfUtonium on 6/11/13.
 */
public class TestingListActivity extends Activity {
    Equation.Solution[] solutions = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_example);

        getSolutions();

        ListView listView = (ListView) findViewById(R.id.listView);
        TruthTableListAdapter adapter = new TruthTableListAdapter(this, solutions);
        listView.setAdapter(adapter);
    }

    private void getSolutions() {
        try {
            solutions = new Equation.Solution[3];
            solutions[0] = new Equation("(T|F)&T").solve();
            solutions[1] = new Equation("T&(!T|F)&!F").solve();
            solutions[2] = new Equation("F=(F=!T)").solve();
            //solutions[3] = new Equation("!T(F&F)").solve();//Bad equation
            //solutions[4] = new Equation("!TF&F").solve();//Bad equation
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
