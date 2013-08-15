package com.logiccalcv2;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by ProfUtonium on 6/11/13.
 */
public class TruthTableListAdapter extends ArrayAdapter<Equation.Solution> {
    private final Activity context;
    private final Equation.Solution[] solutions;

    TruthTableListAdapter(Activity context, Equation.Solution[] solutions) {
        super(context, R.layout.list_example, solutions);
        this.context = context;
        this.solutions = solutions;
    }

    TruthTableListAdapter(Activity context, int layout, Equation.Solution[] solutions) {
        super(context, layout, solutions);
        this.context = context;
        this.solutions = solutions;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        if (rowView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            rowView = inflater.inflate(R.layout.listitem, null);

            ViewHolder viewHolder = new ViewHolder();
            viewHolder.answer = (TextView) rowView.findViewById(R.id.answer);
            viewHolder.equation = (TextView) rowView.findViewById(R.id.equation);
            viewHolder.steps = (TextView) rowView.findViewById(R.id.steps);

            rowView.setTag(viewHolder);
        }

        String answer = "Err: No Answer";
        try {
            answer = Equation.Constant.convert(solutions[position].answer) ? "True" : "False";
        } catch (Exception e) {
            e.printStackTrace();

            new AlertDialog.Builder(context)
                    .setTitle("Error")
                    .setMessage(e.getMessage())
                    .setNeutralButton("Close", null).show();
        }

        ViewHolder holder = (ViewHolder) rowView.getTag();
        holder.answer.setText(answer);//("Answer");
        holder.equation.setText(solutions[position].equation);//("Equation");
        holder.steps.setText(solutions[position].getMultiLineColorSteps(context));//("Multi\nLine\nSteps");

        return rowView;
    }

    static class ViewHolder {
        public TextView answer;
        public TextView equation;
        public TextView steps;
    }
}
