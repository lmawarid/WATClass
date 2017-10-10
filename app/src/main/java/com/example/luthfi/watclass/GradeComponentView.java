package com.example.luthfi.watclass;

import android.content.Context;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class GradeComponentView extends LinearLayout {

    /***
     * Constants
     */

    // For each grade component, for now we only allow 10 entries for marks
    // spread over two rows.
    private final int GRADE_ROWS = 2;
    private final int GRADE_ENTRIES_PER_ROW = 5;

    /***
     * Private Members
     */
    private ArrayList<EditText> gradeEntries;
    private double weight;

    /**
     * Constructors
     */
    public GradeComponentView(Context context) {
        super(context);
        initView();
    }

    public GradeComponentView(Context context, AttributeSet attr) {
        super(context, attr);
        initView();
    }

    public GradeComponentView(Context context, AttributeSet attr, int defStyle) {
        super(context, attr, defStyle);
        initView();
    }

    /***
     * Private Initialization Methods
     */

    public void initView() {
        inflate(getContext(), R.layout.grade_component, this);
        gradeEntries = new ArrayList<>();

        LinearLayout mainLayout = (LinearLayout) findViewById(R.id.main_linear_layout);

        LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams etParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        lParams.topMargin = 4;
        lParams.bottomMargin = 4;
        etParams.weight = 1;

        for (int j = 0; j < GRADE_ROWS; ++j) {
            LinearLayout gradeRow = new LinearLayout(getContext());
            gradeRow.setOrientation(LinearLayout.HORIZONTAL);
            gradeRow.setLayoutParams(lParams);

            for (int k = 0; k < GRADE_ENTRIES_PER_ROW; ++k) {
                EditText gradeEditText = new EditText(getContext());
                gradeEditText.setLayoutParams(etParams);
                gradeEditText.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
                gradeEditText.setEms(10);
                gradeEntries.add(gradeEditText);

                gradeRow.addView(gradeEditText);
            }

            mainLayout.addView(gradeRow);
        }

        this.invalidate();
    }

    /**
     * Public Methods
     */

    public void setComponentDetails(String name, String weight) {
        TextView componentName = (TextView) findViewById(R.id.component_name);
        componentName.setText(name);
        TextView componentWeight = (TextView) findViewById(R.id.component_weight);
        componentWeight.setText(weight);
        try {
            this.weight = Double.parseDouble(weight);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            this.weight = 0;
        }
    }

    public void setMarksData(String marks) {
        JSONArray marksArray;
        try {
            marksArray = new JSONArray(marks);
        } catch (JSONException e) {
            marksArray = new JSONArray();
        }

        for (int i = 0; i < marksArray.length(); ++i) {
            try {
                gradeEntries.get(i).setText(marksArray.get(i).toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public String getComponentName() {
        TextView componentName = (TextView) findViewById(R.id.component_name);
        return componentName.getText().toString();
    }

    public double getComponentWeight() {
        double total = 0;
        int entries = 0;
        for (EditText gradeEditText : gradeEntries) {
            String gradeStr = gradeEditText.getText().toString();
            if (!gradeStr.isEmpty()) {
                try {
                    total += Double.parseDouble(gradeStr);
                    entries++;
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }

        if (entries == 0) {
            return 0;
        } else {
            return ((total / entries) / 100.0) * weight;
        }
    }

    public ArrayList<Double> getComponentMarks() {
        ArrayList<Double> marks = new ArrayList<>();
        for (EditText gradeEditText : gradeEntries) {
            String gradeStr = gradeEditText.getText().toString();
            if (!gradeStr.isEmpty()) {
                try {
                    marks.add(Double.parseDouble(gradeStr));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }

        return marks;
    }
}
