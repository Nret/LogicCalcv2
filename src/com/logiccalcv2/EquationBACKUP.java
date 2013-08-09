/**
 * This version of Equation uses ArrayList<String> istead of a string builder
 */
package com.logiccalcv2;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author ProfUtonium Solves boolean equations.
 */
public class EquationBACKUP {
    protected String equationOriginal;
    protected List<String> equationWorking;// Do the char array operations make it
    // faster than if I was using
    // strings?
    protected Solution solution;

    /**
     * @param equation The equation to solve. Must be in the form of
     */
    EquationBACKUP(String equation) {
        this.equationOriginal = equation;
        // TODO if (equation does not ONLY consist of CONSTANTS and OPERATORS)
        // throw invalid equation
        this.solution = new Solution();
    }

    /**
     * NOTE: Don't forget to set the equation with setEquation() before calling solve() when using this constructor.
     */
    EquationBACKUP() {
        this.equationOriginal = null;
        this.solution = new Solution();
    }

    /**
     * @param equation
     * @return returns this, to use with chaining (e.g. foo.setEquation(...).solve();)
     */
    public EquationBACKUP setEquation(String equation) {
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
        equationWorking = new ArrayList<String>(Arrays.asList(equationOriginal.split("")));
        // Create a List from the input, so the List can be resized, positions
        // deleted, positions inserted.

        //i'am not sure why, but the first position using this method is always nothing. do I'm deleting it here.
        equationWorking.remove(0);

        int pos = 0;
        while (pos < equationWorking.size()) {
            String op = equationWorking.get(pos);

            if (Operand.isPrefixOperand(op)) {
                if (checkPrefixOperand(pos)) {
                    solution.steps.add(new Step(equationWorking, pos,
                            pos + 1));

                    solvePrefix(pos);

                    pos = 0; // reset i to begin looking at the start again
                    continue;
                }
            } else if (Operand.isInfixOperand(op)) {
                if (checkInfixOperand(pos)) {
                    solution.steps.add(new Step(equationWorking,
                            pos - 1, pos + 1));

                    solveInfix(pos);

                    pos = 0; // reset i to begin looking at the start again
                    continue;
                }
            } else if (Operand.isRparen(op)) {
                //Removes parens when needed
                //Right Paren checks
                if (pos < 2)
                    throw new Exception("Bad Equation");

                if (Constant.isConstant(equationWorking.get(pos - 1))
                        && Operand.isLparen(equationWorking.get(pos - 2))) {
                    //everything checks out, remove stuff as needed.
                    solution.steps.add(new Step(equationWorking,
                            pos - 2, pos));

                    equationWorking.remove(pos);
                    equationWorking.remove(pos - 2);

                    pos = 0; // reset i to begin looking at the start again
                    continue;
                }
            }
            pos++;
            // else if not constants, must be an invalid character, throw
            // invalid equation
        }

        if (equationWorking.size() != 1) {
            throw new Exception("Invalid equation: Solved to " +
                    solution.steps.get(solution.steps.size() - 1).equationToString());
        }

        solution.answer = equationWorking.get(0);//0 should contain the answer at this point.
        return solution;
    }

    protected void solvePrefix(int loc) throws Exception {
        String op = equationWorking.get(loc);
        // Get the right hand side of the equation to solve.
        String rhs = equationWorking.get(loc + 1);
        boolean rhsBool = Constant.convert(rhs);

        String ans = Constant.convert(Operand.solvePreFix(op, rhsBool));

        // Set the answer to the location of the operand.
        equationWorking.set(loc, ans);
        // Delete the location after the operand, as it contains the constant
        // that was solved using.
        equationWorking.remove(loc + 1);
    }

    protected void solveInfix(int loc) throws Exception {
        String op = equationWorking.get(loc);
        String lhs = equationWorking.get(loc - 1);
        boolean lhsBool = Constant.convert(lhs);
        String rhs = equationWorking.get(loc + 1);
        boolean rhsBool = Constant.convert(rhs);

        String ans = Constant.convert(Operand.solveInFix(lhsBool, op, rhsBool));

        equationWorking.set(loc, ans);
        // Remove the rhs first. Because other wise the removal of lhs first
        // would move the rhs and the Operand.
        equationWorking.remove(loc + 1);
        equationWorking.remove(loc - 1);
    }

    /**
     * Similar to checkPrefixOperand. Check's if the two characters before and
     * after loc are constants. Other wise it's not ready to be solved. Or there
     * is a invalid equation error
     *
     * @param loc
     * @return
     */
    protected boolean checkInfixOperand(int loc) {
        // If the 'argument' before and after are constant's, then it's ready to
        // be solved.
        if (Constant.isConstant(equationWorking.get(loc + 1))
                && Constant.isConstant(equationWorking.get(loc - 1)))
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
    protected boolean checkPrefixOperand(int loc) {
        // Operand.isOperand(equationWorking.get(loc - 1)) would let you know if
        // it's an invalid equation.

        // If 'argument' is a Constant, then it should be solved. Other wise
        // it's not ready to be solved. Or it's an invalid equation
        if (Constant.isConstant(equationWorking.get(loc + 1)))
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
        final static public String TRUE = "T";
        final static public String FALSE = "F";
        /**
         * If more Constants support is added, don't forget to upgrade this
         * string array.
         */
        final static public String[] CONSTS = {TRUE, FALSE};

        /**
         * Converts a boolean it's Constant equivalent.
         *
         * @param bool value to be converted.
         * @return Constant.TRUE or Constant.FALSE;
         */
        final static public String convert(boolean bool) {
            return bool ? TRUE : FALSE;
        }

        /**
         * Converts a string of a Constant value to its boolean equivalent.
         *
         * @param str
         * @return true or false.
         */
        final static public boolean convert(String str) {
            return (str.equals(Constant.TRUE));
        }

        final static public boolean isConstant(String con) {
            for (String cons : CONSTS)
                if (con.equals(cons))
                    return true;
            return false;
        }
    }

    public static class Operand {
        /**
         * Note: the NOT operator must come before the argument.
         */
        final static public String NOT = "!";
        final static public String AND = "&";
        final static public String OR = "|";
        final static public String XOR = "^";
        final static public String EQU = "=";
        final static public String LPAREN = "(";
        final static public String RPAREN = ")";
        /**
         * If more Operands support is added, don't forget to upgrade this
         * string array.
         */
        final static public String[] OPS = {NOT, AND, OR, XOR, EQU, LPAREN,
                RPAREN};
        final static public String[] PREFIX_OPS = {NOT};
        final static public String[] INFIX_OPS = {AND, OR, XOR, EQU};

        final static public boolean isNot(String str) {
            return str.equals(NOT);
        }

        final static public boolean isAnd(String str) {
            return str.equals(AND);
        }

        final static public boolean isOr(String str) {
            return str.equals(OR);
        }

        final static public boolean isXor(String str) {
            return str.equals(XOR);
        }

        final static public boolean isEqu(String str) {
            return str.equals(EQU);
        }

        final static public boolean isLparen(String str) {
            return str.equals(LPAREN);
        }

        final static public boolean isRparen(String str) {
            return str.equals(RPAREN);
        }

        final static public boolean isOperand(String op) {
            for (String ops : OPS)
                if (op.equals(ops))
                    return true;
            return false;
        }

        final static public boolean isInfixOperand(String op) {
            for (String ops : INFIX_OPS)
                if (op.equals(ops))
                    return true;
            return false;
        }

        final static public boolean isPrefixOperand(String op) {
            for (String ops : PREFIX_OPS)
                if (op.equals(ops))
                    return true;
            return false;
        }

        final static public boolean solveInFix(boolean lhs, String op,
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

        final static public boolean solvePreFix(String op, boolean rhs)
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
        public List<String> equation;
        public int startSolvingLoc;
        public int endSolvingLoc;

        Step(List<String> equation, int startSolvingLoc, int endSolvingLoc) {
            this.equation = (List<String>) ((ArrayList<String>) equation).clone();//Other wise there needs to be a deep copy of equation.
            this.startSolvingLoc = startSolvingLoc;
            this.endSolvingLoc = endSolvingLoc;
        }

        public String equationToString() {
            StringBuilder builder = new StringBuilder(equation.size());
            for (String str : equation) {
                builder.append(str);
            }
            return builder.toString();
        }
    }
}
