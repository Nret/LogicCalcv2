package com.logiccalcv2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.logiccalcv2.Equation.Constant;
import com.logiccalcv2.Equation.Operand;
import com.logiccalcv2.Equation.Solution;
import com.logiccalcv2.Equation.Step;

public class TestingActivity extends Activity {
    private TextView inputText;
    private TextView outputText;
    private boolean ver = false;
    private Equation inputEquation;

    protected void simpleTest() {
        Solution sol;
        Equation equ = new Equation();
        try {
            sol = equ.setEquation("T|F&T").solve();
            logAnswers(sol);
            sol = equ.setEquation("T&!T|F&!F").solve();
            logAnswers(sol);
            sol = equ.setEquation("F=(F=!T)").solve();
            logAnswers(sol);
            sol = equ.setEquation("!T(F&F)").solve();
            logAnswers(sol);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    protected void logAnswers(Solution sol) {
        Log.v("LogicCalc", "-------");
        Log.v("LogicCalc", sol.equation + " = " + sol.answer);
        Log.v("LogicCalc", "Steps: ");
        for (Step step : sol.steps)
            Log.v("LogicCalc", step.equationToString());
        Log.v("LogicCalc", sol.answer);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_testing_activity);
        setContentView(R.layout.horizontal_example);
        findViewIds();
        //simpleTest();

        inputEquation = new Equation();
    }

    private void findViewIds() {
        inputText = (TextView) findViewById(R.id.inputText);
        outputText = (TextView) findViewById(R.id.outputText);
        ((ProgressBar) findViewById(R.id.progressBar)).setVisibility(View.INVISIBLE); //Hopefully will make the progress bar disappear
    }

    public boolean insertOnClick(View view) {
        switch (view.getId()) {
            case R.id.button_not:
                inputTextAdd(Operand.NOT);
                break;
            case R.id.button_and:
                inputTextAdd(Operand.AND);
                break;
            case R.id.button_or:
                inputTextAdd(Operand.OR);
                break;
            case R.id.button_xor:
                inputTextAdd(Operand.XOR);
                break;
            case R.id.button_equal:
                inputTextAdd(Operand.EQU);
                break;
            case R.id.button_true:
                inputTextAdd(Constant.TRUE);
                break;
            case R.id.button_false:
                inputTextAdd(Constant.FALSE);
                break;
            case R.id.button_a:
                inputTextAdd(R.string.variable_a);
                break;
            case R.id.button_b:
                inputTextAdd(R.string.variable_b);
                break;
            case R.id.button_c:
                inputTextAdd(R.string.variable_c);
                break;
            case R.id.button_d:
                inputTextAdd(R.string.variable_d);
                break;
            case R.id.button_e:
                inputTextAdd(R.string.variable_e);
                break;
            case R.id.button_lparen:
                inputTextAdd(Operand.LPAREN);
                break;
            case R.id.button_rparen:
                inputTextAdd(Operand.RPAREN);
                break;
            case R.id.button_del:
                deleteLastChar();
                break;
            default:
                break;
        }

        setEquation();
        setSolution();

        return true;
    }

    /**
     * Tell the user what the answer would be
     */
    private void setSolution() {
        //inputEquation.solve();
    }

    /**
     * Opens the list view for all the answers
     * @param view
     * @return
     */
    public boolean answersOnClick(View view) {
        Intent intent = new Intent();
        //intent.put

        return true;
    }

    /**
     * Set the inputEquation with the text in the inputText View
     */
    private void setEquation() {
        inputEquation.setEquation(inputText.getText().toString());
    }

    private void deleteLastChar() {
        String text = inputText.getText().toString();
        int start = 0;
        int end = text.length() - 1;

        if (end < 0)
            return;

        inputText.setText(text.subSequence(start, end));
    }

    private void inputTextAdd(int resId) {
        inputTextAdd(getBaseContext().getString(resId));
    }

    private void inputTextAdd(CharSequence cs) {
        inputText.append(cs);
//		String text = inputText.getText().toString() + cs.toString();
//		inputText.setText(text);
    }

    public void changeUI() {
        if (ver) {
            setContentView(R.layout.horizontal_example);
            ver = !ver;
        } else {
            setContentView(R.layout.vertical_example);
            ver = !ver;
        }

        findViewIds();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.testing_acticity, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.switch_ui:
                changeUI();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
