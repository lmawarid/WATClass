package com.example.luthfi.watclass;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.luthfi.watclass.data.ClassContract;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Luthfi on 4/9/2016.
 */
public class GradeCalcActivity extends AppCompatActivity {

    /***
     * Private Members
     */

    private Uri klass;
    private ArrayList<GradeComponentView> gradeComponents;

    /***
     * Private Helper Methods
     */

    private String getMarksData() {
        JSONObject marksObj = new JSONObject();
        for (GradeComponentView gradeComponent : gradeComponents) {
            String name = gradeComponent.getComponentName();
            ArrayList<Double> marks = gradeComponent.getComponentMarks();
            try {
                marksObj.put(name, marks);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return marksObj.toString();
    }

    private void setMarksData(String marksData) {
        JSONObject marksObj;
        try {
            marksObj = new JSONObject(marksData);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        int componentIdx = 0;
        Iterator<String> componentKeys = marksObj.keys();
        while (componentKeys.hasNext()) {
            try {
                gradeComponents.get(componentIdx).setMarksData(
                        marksObj.get(componentKeys.next()).toString()
                );
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
                return;
            }
            componentIdx += 1;
        }
    }

    // Save data.
    private boolean save() {
        ContentValues values = new ContentValues();

        // Get current values.
        values.put(ClassContract.ClassEntry.COLUMN_MARKS,
                getMarksData());

        // Save to current class.
        getContentResolver().update(klass, values, null, null);
        return true;
    }

    /***
     * Private Initialization Methods
     */

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Grade Calculator");
        setSupportActionBar(toolbar);
    }

    private void initViews() {
        // Set the current class to be this class.
        klass = getIntent().getData();
        // Initialize cursor, get class text, and close.
        Cursor cursor = getContentResolver().query(klass, null, null, null, null);
        cursor.moveToFirst();
        ArrayList<String> componentNames = ClassEditActivity.parseJSONArray(
                cursor.getString(cursor.getColumnIndexOrThrow(ClassContract.ClassEntry.COLUMN_GRADING))
        );
        ArrayList<String> componentWeights = ClassEditActivity.parseJSONArray(
                cursor.getString(cursor.getColumnIndexOrThrow(ClassContract.ClassEntry.COLUMN_WEIGHTS))
        );
        String marksData = cursor.getString(cursor.getColumnIndexOrThrow(ClassContract.ClassEntry.COLUMN_MARKS));
        cursor.close();

        gradeComponents = new ArrayList<>();

        LinearLayout mainLayout = (LinearLayout) findViewById(R.id.main_layout);
        for (int i = 0; i < componentNames.size() ; ++i) {
            String name = componentNames.get(i);
            if (name != null && !name.isEmpty()) {
                GradeComponentView gradeView = new GradeComponentView(getApplicationContext());
                gradeView.setComponentDetails(componentNames.get(i), componentWeights.get(i));
                gradeComponents.add(gradeView);
                mainLayout.addView(gradeView);
            }
        }

        setMarksData(marksData);
    }

    /***
     * Lifecycle Methods
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grade_calc);
        initToolbar();
        initViews();
    }

    @Override
    protected void onPause() {
        super.onPause();
        save();
    }

    /***
     * OnClick Handlers
     */

    public void calculateGrade(View view) {
        double grade = 0.0;
        for (GradeComponentView gradeView : gradeComponents) {
            grade += gradeView.getComponentWeight();
        }

        TextView finalGradeText = (TextView) findViewById(R.id.final_grade_textview);
        finalGradeText.setText(String.valueOf(grade));
    }
}
