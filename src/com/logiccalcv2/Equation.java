package com.logiccalcv2;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.TextAppearanceSpan;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author ProfUtonium Solves boolean equations.
 */
public class Equation {
    protected String equationOriginal;
    protected Solution solution;

    /**
     * @param equation The equation to solve. Must be in the form of
     */
    Equation(String equation) {
        this.equationOriginal = equation;
        // TODO if (equation does not ONLY consist of CONSTANTS and OPERATORS)
        // throw invalid equation
        this.solution = new Solution();
    }

    /**
     * NOTE: Don't forget to set the equation with setEquation() before calling solve() when using this constructor.
     */
    Equation() {
        this.equationOriginal = null; //todo what happens when it's null? i need to put in a null check
        this.solution = new Solution();
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
     *
     * @return The current Solution object.
     */
    public Solution getSolution() {
        return this.solution;
    }

    public Solution solve() throws Exception {
        solution.steps.clear();
        solution.equation = equationOriginal;
        StringBuilder equationWorking = new StringBuilder(equationOriginal);

        int indexR;
        while((indexR = equationWorking.indexOf(String.valueOf(Operand.RPAREN))) != -1) {
            int indexL = equationWorking.lastIndexOf(String.valueOf(Operand.RPAREN));
            if (indexL == -1)
                throw new Exception("Invalid equation: Solved to " +
                        solution.steps.get(solution.steps.size() - 1).equationToString());
			char ans = leftToRightSolve(equationWorking.toString());
			equationWorking.delete(indexL, indexR + 1);//+1 because it's exclusive
			equationWorking.insert(indexL, ans);//todo make sure this actually inserts to same place the ( was
		}
		
		//A final solve because there's no more parens to start from, or get in the way of anything
		char answer = leftToRightSolve(equationWorking.toString());
		
        solution.answer = String.valueOf(answer);
        return solution;
    }

    protected char leftToRightSolve(String equation) throws Exception {
        StringBuilder equationWorking = new StringBuilder(equation);
		int pos = 0;
        while (pos < equationWorking.length()) {
            char op = equationWorking.charAt(pos);

            if (Operand.isPrefixOperand(op)) {
                if (checkPrefixEqaution(equationWorking.substring(pos, pos + 1))) {
                    solution.steps.add(new Step(equationWorking, pos,
                            pos + 1));

                    char ans = solvePrefix(equationWorking.substring(pos, pos + 1));
					
					equationWorking.setCharAt(pos, ans);
					equationWorking.deleteCharAt(pos + 1);
					
                    pos = 0; // reset i to begin looking at the start again
                    continue;
                }
            } else if (Operand.isInfixOperand(op)) {
                if (checkInfixEquation(equationWorking.substring(pos - 1, pos + 1))) {
                    solution.steps.add(new Step(equationWorking,
                            pos - 1, pos + 1));

                    char ans = solveInfix(equationWorking.substring(pos - 1, pos + 1));

					equationWorking.setCharAt(pos - 1, ans);
					equationWorking.delete(pos, pos + 2); //+2 because it's exclusive 
					
                    pos = 0; // reset i to begin looking at the start again
                    continue;
                }
            }
            pos++;
        }

        if (equationWorking.length() != 1) {
            throw new Exception("Invalid equation: Solved to " +
                    solution.steps.get(solution.steps.size() - 1).equationToString());
        }
		
		return equationWorking.charAt(0);
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
     * @param loc
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
     * @param loc
     * @return
     */
    protected boolean checkPrefixEqaution(String equation) throws Exception {
		if (equation.length() != 3)
			throw new Exception("Invalid equation length.");
			
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
         * Converts a string of a Constant value to its boolean equivalent.
         *
         * @param str
         * @return true or false.
         */
        final static public boolean convert(char str) {
            return str == Constant.TRUE;
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
                } else if (Operand.isEqu(op)) {
                    return lhs == rhs;
                } else if (Operand.isOr(op)) {
                    return lhs | rhs;
                } else if (Operand.isXor(op)) {
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

        public String getMultiLineSteps() {
            StringBuilder builder = new StringBuilder();

            //append all the steps
            for (Step step : steps) {
                builder.append(step.equationToString())
                        .append(System.getProperty("line.separator"));
            }
            //append the answer
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

            for (Step step : steps) {
                Spannable tempSpan = new SpannableString(step.equationToString());

                tempSpan.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.equation)), step.startSolvingLoc, step.endSolvingLoc + 1, Spanned.SPAN_MARK_POINT);//ForegroundColorSpan(Color.BLUE)

                if (lastAnswerLoc != -1)
                    tempSpan.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.answer)), lastAnswerLoc, lastAnswerLoc + 1, Spanned.SPAN_MARK_POINT);//ForegroundColorSpan(Color.CYAN)

                span = (SpannedString) TextUtils.concat(span, tempSpan, System.getProperty("line.separator"));

                lastAnswerLoc = step.startSolvingLoc;
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

        Step(StringBuilder equation, int startSolvingLoc, int endSolvingLoc) {
            //this.equation = (List<String>) ((ArrayList<String>) equation).clone();//Other wise there needs to be a deep copy of equation.
            this.equation = new StringBuilder(equation);
            this.startSolvingLoc = startSolvingLoc;
            this.endSolvingLoc = endSolvingLoc;
        }

        public String equationToString() {
            return equation.toString();
        }
    }
}
