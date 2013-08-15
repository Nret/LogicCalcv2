package com.logiccalcv2;

import android.content.*;
import android.text.*;
import android.text.style.*;
import android.util.*;
import java.util.*;

/**
 * @author ProfUtonium Solves boolean equations.
 */
public class Equation {
    protected String equationOriginal;
    protected Solution solution;
	protected StringBuilder equationWorking;
    protected int rParenPos;
    protected int lParenPos;

    /**
     * @param equation The equation to solve. Must be in the form of
     */
    Equation(String equation) {
        this.equationOriginal = equation;
		this.solution = null;
        // TODO if (equation does not ONLY consist of CONSTANTS and OPERATORS)
        // throw invalid equation
    }

    /**
     * NOTE: Don't forget to set the equation with setEquation() before calling solve() when using this constructor.
     */
    Equation() {
        this.equationOriginal = null; //todo what happens when it's null? i need to put in a null check
		this.solution = null;
    }

    /**
     * @param equation
     * @return returns this, to use with chaining (e.g. foo.setEquation(...).solve();)
     */
    public Equation setEquation(String equation) {
        this.equationOriginal = equation;

        return this;
    }

    /**
     * Useful to get the solution when an error was thrown.
     * Does not null check the solution before returning it. Will be null if .solve() was not called first.
     * @return The current Solution object.
     */
    public Solution getSolution() {
        return this.solution;
    }
	
    public Solution solve() throws Exception {
		solution = new Solution();

        solution.equation = equationOriginal;
        equationWorking = new StringBuilder(equationOriginal);


        while ((rParenPos = equationWorking.indexOf(String.valueOf(Operand.RPAREN))) != -1) {
            lParenPos = equationWorking.lastIndexOf(String.valueOf(Operand.LPAREN), rParenPos);
            if (lParenPos == -1)
                throw new Exception("Invalid equation: Solved to " +
									solution.steps.get(solution.steps.size() - 1).equationToString());
			leftToRightSolve();//+ and - to exclude parens
			
			equationWorking.deleteCharAt(rParenPos);//Removes right paren.
			equationWorking.deleteCharAt(lParenPos);//Removes left paren. + 1 would be the previous answer
			
			//Need to subtract to keep locs in line with where they should be after deleting the parens.
			solution.getLastStep().answerLoc--;
		}

        if (equationWorking.length() > 1) {
            //No more parens
            lParenPos = -1;
            rParenPos = equationWorking.length();

            Log.v("LogicCalc", "before the final solve " + equationWorking);
            //A final solve because there's no more parens to start from, or get in the way of anything
            leftToRightSolve();
        }

        solution.answer = new String(equationWorking);//.toString();//Only thing left in equationWorking is the answer.
        Log.v("LogicCalc", solution.getMultiLineSteps());
		Log.v("LogicCalc", "Answer=" + solution.answer);
		
		return solution;
    }

    protected void leftToRightSolve() throws Exception {
        char ans = ' ';
        if (lParenPos > rParenPos)
			throw new Exception("lParenPos is greater than rParenPos");

        int startPos = lParenPos + 1;
        int pos = startPos;
        while (rParenPos - 1 > lParenPos + 1) {
			Log.v("LogicCalc", "start=" + startPos + " - end=" + (rParenPos - 1) + " - pos=" + pos + " - equ=" + equationWorking + " - ans=" + ans);

			if (pos > startPos + rParenPos - 1)
				throw new Exception("Invalid equation: " + solution.equation + " Solved to " +
									solution.steps.get(solution.steps.size() - 1).equationToString());	

            char op = equationWorking.charAt(pos);

            if (Operand.isPrefixOperand(op)) {
                if (checkPrefixEquation(equationWorking.substring(pos, pos + 2))) {
                    solution.steps.add(new Step(equationWorking, pos,
												pos + 1, pos));

                    ans = solvePrefix(equationWorking.substring(pos, pos + 2));

					equationWorking.setCharAt(pos, ans);
					equationWorking.deleteCharAt(pos + 1);
                    rParenPos--;

                    pos = startPos; // reset i to begin looking at the start again
                    continue;
                }
            }
			else if (Operand.isInfixOperand(op)) {
                if (checkInfixEquation(equationWorking.substring(pos - 1, pos + 2))) {
                    solution.steps.add(new Step(equationWorking,
												pos - 1, pos + 1, pos - 1));

                    ans = solveInfix(equationWorking.substring(pos - 1, pos + 2));

					equationWorking.setCharAt(pos - 1, ans);
					equationWorking.delete(pos, pos + 2); //+2 because it's exclusive
                    rParenPos--;
                    rParenPos--;

                    pos = startPos; // reset i to begin looking at the start again
                    continue;
                }
            }
            pos++;
        }
        Log.v("LogicCalc", "start=" + startPos + " - end=" + (rParenPos - 1) + " - pos=" + pos + " - equ=" + equationWorking + " - ans=" + ans);
    }

    protected char solvePrefix(String equation) throws Exception {
		char op = equation.charAt(0);
        // Get the right hand side of the equation to solve.
        char rhs = equation.charAt(1);
        boolean rhsBool = Constant.convert(rhs);

        char ans = Constant.convert(Operand.solvePreFix(op, rhsBool));

        return ans;
    }

    protected char solveInfix(String equation) throws Exception {
        char op = equation.charAt(1);
        char lhs = equation.charAt(0);
        boolean lhsBool = Constant.convert(lhs);
        char rhs = equation.charAt(2);
        boolean rhsBool = Constant.convert(rhs);

        char ans = Constant.convert(Operand.solveInFix(lhsBool, op, rhsBool));

		return ans;
    }

    /**
     * Similar to checkPrefixOperand. Check's if the two characters before and
     * after loc are constants. Other wise it's not ready to be solved. Or there
     * is a invalid equation error
     *
     * @param equation to be checked
     * @return
     */
    protected boolean checkInfixEquation(String equation) throws Exception {
		if (equation.length() != 3)
			throw new Exception("Invalid equation length.");

        if (Constant.isConstant(equation.charAt(0))//first argument
			&& Operand.isOperand(equation.charAt(1))//operand
			&& Constant.isConstant(equation.charAt(2)))//second argument
        	return true;

        return false;
    }

    /**
     * Checks the character after the NOT operator to make sure it's a Constant.
     * because if it's an operator other than RPAREN or another constant then it
     * will cause an unsolvable error, as such "T!F -> TT -> unsolvable". But
     * you could have "T&!F -> T&T -> T" or "!(T&F)"
     *
     * @param equation to be checked
     * @return
     */
    protected boolean checkPrefixEquation(String equation) throws Exception {
		if (equation.length() != 2)
			throw new Exception("Invalid equation length: " + equation);

        if (Operand.isOperand(equation.charAt(0))//operand
			&& Constant.isConstant(equation.charAt(1)))//argument
        	return true;

        return false;
    }

    /**
     * Values to be used for true or false. Dictates what form Equation has to
     * be in. e.g. T & F or 0 & 1 or what ever.
     *
     * @author ProfUtonium
     */
    public static class Constant {
        final static public char TRUE = 'T';
        final static public char FALSE = 'F';
        /**
         * If more Constants support is added, don't forget to upgrade this
         * string array.
         */
        final static public char[] CONSTS = {TRUE, FALSE};

        /**
         * Converts a boolean it's Constant equivalent.
         *
         * @param bool value to be converted.
         * @return Constant.TRUE or Constant.FALSE;
         */
        final static public char convert(boolean bool) {
            return bool ? TRUE : FALSE;
        }

        /**
         * Converts a char of a Constant value to its boolean equivalent.
         *
         * @param str
         * @return true or false.
         */
        final static public boolean convert(char str) {
            return str == Constant.TRUE;
        }

        /**
         * Converts a string of a Constant value to its boolean equivalent.
         *
         * @param str
         * @return true or false.
         */
        final static public boolean convert(String str) throws Exception {
            if (str.equals(String.valueOf(TRUE)))
                return true;
            else if (str.equals(String.valueOf(FALSE)))
                return false;
            else
                throw new Exception("Not a Constant");
        }

        final static public boolean isConstant(char con) {
            for (char cons : CONSTS)
                if (cons == con)
                    return true;
            return false;
        }
    }

    public static class Operand {
        /**
         * Note: the NOT operator must come before the argument.
         */
        final static public char NOT = '!';
        final static public char AND = '&';
        final static public char OR = '|';
        final static public char XOR = '^';
        final static public char EQU = '=';
        final static public char LPAREN = '(';
        final static public char RPAREN = ')';
        /**
         * If more Operands support is added, don't forget to upgrade this
         * string array.
         */
        final static public char[] OPS = {NOT, AND, OR, XOR, EQU, LPAREN,
			RPAREN};
        final static public char[] PREFIX_OPS = {NOT};
        final static public char[] INFIX_OPS = {AND, OR, XOR, EQU};

        final static public boolean isNot(char str) {
            return str == NOT;
        }

        final static public boolean isAnd(char str) {
            return str == AND;
        }

        final static public boolean isOr(char str) {
            return str == OR;
        }

        final static public boolean isXor(char str) {
            return str == XOR;
        }

        final static public boolean isEqu(char str) {
            return str == EQU;
        }

        final static public boolean isLparen(char str) {
            return str == LPAREN;
        }

        final static public boolean isRparen(char str) {
            return str == RPAREN;
        }

        final static public boolean isOperand(char op) {
            for (char ops : OPS)
                if (op == ops)
                    return true;
            return false;
        }

        final static public boolean isInfixOperand(char op) {
            for (char ops : INFIX_OPS)
                if (op == ops)
                    return true;
            return false;
        }

        final static public boolean isPrefixOperand(char op) {
            for (char ops : PREFIX_OPS)
                if (op == ops)
                    return true;
            return false;
        }

        final static public boolean solveInFix(boolean lhs, char op,
                                               boolean rhs) throws Exception {
            if (isInfixOperand(op)) {
                if (Operand.isAnd(op)) {
                    return lhs & rhs;
                }
				else if (Operand.isEqu(op)) {
                    return lhs == rhs;
                }
				else if (Operand.isOr(op)) {
                    return lhs | rhs;
                }
				else if (Operand.isXor(op)) {
                    return lhs ^ rhs;
                }
            }
            throw new Exception("Bad Operand: " + op);
        }

        final static public boolean solvePreFix(char op, boolean rhs)
		throws Exception {
            if (isPrefixOperand(op)) {
                if (Operand.isNot(op)) {
                    return !rhs;
                }
            }
            throw new Exception("Bad Operand: " + op);
        }
    }

    public class Solution {
        public List<Step> steps;
        public String answer;
        public String equation;

        Solution() {
            steps = new ArrayList<Step>();
        }

		/*
		 * Returns the last step, throws "No Steps" if there are no steps.
		 */
		public Step getLastStep() throws Exception {
			if (steps.size() == 0)
				throw new Exception("No Steps");
			return steps.get(steps.size() - 1);
		}

        public String getMultiLineSteps() {
            StringBuilder builder = new StringBuilder();

            //append all the steps
            for (Step step : steps) {
                builder.append(step.equationToString())
					.append(System.getProperty("line.separator"));
            }
            //append the answer
			if (answer == null)
				answer = "No answer yet";
            builder.append(answer)
				.append(System.getProperty("line.separator"));

            return builder.toString();
        }

        /**
         * Only for use on android
         * @param context required  to use context.getResources().getColor to get equation and answer colors from the color.xml resource file.
         * @return
         */
        public SpannedString getMultiLineColorSteps(Context context) {
            int lastAnswerLoc = -1;
            SpannedString span = new SpannedString("");

			Log.v("LogicCalc", "------------");
			Log.v("LogicCalc", getMultiLineSteps());
            for (Step step : steps) {
                Spannable tempSpan = new SpannableString(step.equationToString());

                tempSpan.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.equation)), step.startSolvingLoc, step.endSolvingLoc + 1, Spanned.SPAN_MARK_POINT);//ForegroundColorSpan(Color.BLUE)

				Log.v("LogicCalc", "lastAnswerLoc="+lastAnswerLoc + " equ="+step.equation);
                if (lastAnswerLoc != -1) 
					tempSpan.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.answer)), lastAnswerLoc, lastAnswerLoc + 1, Spanned.SPAN_MARK_POINT);//ForegroundColorSpan(Color.CYAN)

                span = (SpannedString) TextUtils.concat(span, tempSpan, System.getProperty("line.separator"));

                lastAnswerLoc = step.answerLoc;
            }

            Spannable tempSpan = new SpannableString(answer);
            tempSpan.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.answer)), 0, 1, Spanned.SPAN_MARK_POINT);//ForegroundColorSpan(Color.CYAN)

            span = (SpannedString) TextUtils.concat(span, tempSpan);

            return span;
        }
    }

    public class Step {
        public StringBuilder equation;
        public int startSolvingLoc;
        public int endSolvingLoc; //inclusive
		public int answerLoc;

        Step(StringBuilder equation, int startSolvingLoc, int endSolvingLoc, int answerLoc) {
            //this.equation = (List<String>) ((ArrayList<String>) equation).clone();//Other wise there needs to be a deep copy of equation.
            this.equation = new StringBuilder(equation);
            this.startSolvingLoc = startSolvingLoc;
            this.endSolvingLoc = endSolvingLoc;
			this.answerLoc = answerLoc;
        }

        public String equationToString() {
            return equation.toString();
        }
    }
}
