/**
 * 
 */
package com.logiccalcv2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.logiccalcv2.Equation.Constant;
import com.logiccalcv2.Equation.Operand;

/**
 * @author ProfUtonium
 * 
 */
public class TruthTable {
	// The String of Binary representing the values past. The Equation created,
	// ready to provide information.
	protected Map<Boolean[], Equation> mapInputEquation;
	protected String equation;
	protected List<String> variables;

	TruthTable(String equation) {
		this.equation = equation;
	}

    TruthTable() {
        this.equation = null;
    }

    /**
     *
     * @param equation the equation to use
     * @return returns this for use with chaining
     */
    public TruthTable setEquation(String equation) {
        this.equation = equation;

        return this;
    }
	
	protected void first() throws Exception {
		generateVariables();
		generateEquationMap();
		solveEquationMap();
	}

	protected void solveEquationMap() throws Exception {
		for (Equation equ : mapInputEquation.values()) {
			equ.solve();
		}
	}

	protected void generateEquationMap() {
		mapInputEquation = new HashMap<Boolean[], Equation>();// Is created here because
														// this is where it's
														// populated.

		// Do you want count up or count down? Start with all falses or start
		// with all trues? This is where that happens.
		// And I am going to start with Trues.

		// get the number we want to count to. The number that will have enough
		// binary positions to count to to amount of variables
		int binaryInt = 2 ^ variables.size();

		// count down backards. So we start with trues.
		for (int i = binaryInt; i >= 0; i--) {
			String binaryString = Integer.toBinaryString(i);
			// This Boolean[] will serve as the container for the transition.
			Boolean[] binaryValues = stringToBooleanArray(binaryString);

			// Create the new equation string with variables replaced with
			// constants.
			String newEquation = new String(equation);
			for (int j = 0; j < variables.size(); j++) {
				newEquation = newEquation.replace(variables.get(i),
						String.valueOf(Constant.convert(binaryValues[i])));
			}

			Equation equationObject = new Equation(newEquation);
			mapInputEquation.put(binaryValues, equationObject);
		}
	}

	public Boolean[] stringToBooleanArray(String str) {
		Boolean[] binaryValues = new Boolean[str.length()];
		String[] binaryStringSplit = str.split("");
		
		for (int i = 0; i < str.length(); i++) {
			binaryValues[i] = binaryStringSplit[i].equals("1");
		}
		
		return binaryValues;
	}

	protected void generateVariables() {
		variables = new ArrayList<String>();

		for (String var : equation.split("")) {
			// can also check Constant.isConstant to if I have Constant's in the
			// string. when the String.replace happens on the equation if the
			// Constants aren't the first operation it might over right already
			// replaced strings. Or you can just not allow them.
			if (!(Operand.isOperand(var.charAt(0)) || Constant.isConstant(var.charAt(0)))) {
				// It's not a constant or operand so let's remember it.
				variables.add(var);
			}
		}
	}
	
	/**
	 * @return the mapInputEquation
	 */
	public Map<Boolean[], Equation> getEquationMap() {
		return mapInputEquation;
	}

	/**
	 * @return the equation
	 */
	public String getEquation() {
		return equation;
	}

	/**
	 * @return the variables
	 */
	public List<String> getVariables() {
		return variables;
	}
}
