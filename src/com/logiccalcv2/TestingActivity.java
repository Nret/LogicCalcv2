package com.logiccalcv2;

import android.app.*;
import android.content.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import com.logiccalcv2.Equation.*;

public class TestingActivity extends Activity {
    private TextView inputText;
    private TextView outputText;
    private boolean ver = false;
    private Equation inputEquation;
    private TextView stepsText;

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
            e.printStackTrace();
			
			new AlertDialog.Builder(this)
				.setTitle("Error")
				.setMessage(e.getMessage())
				.setNeutralButton("Close", null).show();
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
        //setContentView(R.layout.horizontal_example);
        setContentView(R.layout.vertical_example);
        findViewIds();
        //simpleTest();

        inputEquation = new Equation();
    }

    private void findViewIds() {
        inputText = (TextView) findViewById(R.id.inputText);
        outputText = (TextView) findViewById(R.id.outputText);
        stepsText = (TextView) findViewById(R.id.stepsText);
    }

    public boolean outputClicked(View view) {
        Log.v("LogicCalc", "outputClicked");

        Intent listView = new Intent(this, TestingListActivity.class);
        listView.putExtra("equation", inputText.getText());

        startActivity(listView);

        return true;
    }

    public boolean insertOnClick(View view) {
        Log.v("LogicCalc", "insertOnClick here: " + view.getId() + " called");
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
            case R.id.button_solv:
                setSolution();
                break;
            default:
                break;
        }

        //setEquation();
        //setSolution();

        return true;
    }

    /**
     * Tell the user what the answer would be
     */
    private void setSolution() {
        try {
            inputEquation.setEquation(inputText.getText().toString());

            inputEquation.solve();

            outputText.setText(Constant.convert(inputEquation.getSolution().answer.toString()) ? "True" : "False");
            stepsText.setText(inputEquation.getSolution().getMultiLineColorSteps(this));
        } catch (Exception e) {
            outputText.setText("Bad Input");
            e.printStackTrace();
        }
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
	
	private void inputTextAdd(char ch) {
		inputText.append(String.valueOf(ch));
		
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
