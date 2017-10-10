package com.example.luthfi.watclass;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.example.luthfi.watclass.data.ClassContract;

import java.util.ArrayList;
import java.util.Calendar;
import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by Luthfi on 22/8/2016.
 * This allows a user to edit information about a class.
 */
public class ClassEditActivity extends AppCompatActivity {

    /***
     * Constants
     */

    /***
     * Action specifiers to be used with the Intent that sends
     * the user to this activity.
     */
    public static final String ACTION_NEW = "action_new";
    public static final String ACTION_EDIT = "action_edit";

    /***
     * Private Members
     */

    // References to URI and UI elements.
    private Uri klass;
    private Spinner termSpinner;
    private Spinner yearSpinner;
    private EditText subjectEditText;
    private EditText idEditText;
    private EditText titleEditText;
    private EditText instructorEditText;
    private EditText lectureEditText;
    private EditText tutorialEditText;
    private ArrayList<EditText> componentNameEditTexts;
    private ArrayList<EditText> componentWeightsEditTexts;

    /***
     * Static & Private Helper Methods
     */
    // Get the term code for the current term.
    public static String currentTerm() {
        String currentTerm = "1";
        int thisMonth = Calendar.getInstance().get(Calendar.MONTH);
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);

        currentTerm += Integer.toString(thisYear).substring(2);
        if (thisMonth <= 4) {
            currentTerm += "1";
        } else if (thisMonth <= 8) {
            currentTerm += "5";
        } else if (thisMonth <= 12) {
            currentTerm += "9";
        } else {
            throw new UnsupportedOperationException("Invalid month" + thisMonth);
        }

        return currentTerm;
    }

    // Get selected term from the spinners.
    private String getTerm() {
        String term = (String) termSpinner.getSelectedItem();
        String year = (String) yearSpinner.getSelectedItem();

        String currentTerm = "1" + year.substring(2);

        if (term.equals("Winter")) {
            currentTerm += "1";
        } else if (term.equals("Spring")) {
            currentTerm += "5";
        } else if (term.equals("Fall")) {
            currentTerm += "9";
        } else {
            throw new UnsupportedOperationException("Unknown term: " + term);
        }

        return currentTerm;
    }

    // Parse term to get corresponding spinner values.
    private void setTermSpinner(Spinner termSpinner, Spinner yearSpinner, String term) {
        // Set termSpinner.
        switch (Integer.parseInt(term.substring(3))) {
            case 1:
                termSpinner.setSelection(0);
                break;
            case 5:
                termSpinner.setSelection(1);
                break;
            case 9:
                termSpinner.setSelection(2);
                break;
            default:
                throw new UnsupportedOperationException("Unknown term: " + term);
        }

        // Set yearSpinner.
        int count = yearSpinner.getCount();
        for (int i = 0; i < count; ++i) {
            String year = (String) yearSpinner.getItemAtPosition(i);
            if (year.substring(2, 4).equals(term.substring(1, 3))) {
                yearSpinner.setSelection(i);
                break;
            }
        }
    }

    // Parse a JSONArray string into an ArrayList<String>
    public static ArrayList<String> parseJSONArray(String jsonArray) {
        JSONArray array;
        try {
            array = new JSONArray(jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
            array = new JSONArray();
        }

        ArrayList<String> result = new ArrayList<>();
        for (int i = 0; i < array.length(); ++i) {
            try {
                result.add(array.get(i).toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    // Check if the subject and ID fields are empty (if so, don't save the class!)
    private boolean subjectIdAreNull() {
        return subjectEditText.getText().toString().isEmpty() &&
                idEditText.getText().toString().isEmpty();
    }

    // Save data.
    private boolean save() {
        ContentValues values = new ContentValues();

        // Get current values.
        values.put(ClassContract.ClassEntry.COLUMN_TERM,
                getTerm());
        values.put(ClassContract.ClassEntry.COLUMN_SUBJECT,
                subjectEditText.getText().toString());
        values.put(ClassContract.ClassEntry.COLUMN_ID,
                idEditText.getText().toString());
        values.put(ClassContract.ClassEntry.COLUMN_TITLE,
                titleEditText.getText().toString());
        values.put(ClassContract.ClassEntry.COLUMN_INSTRUCTOR,
                instructorEditText.getText().toString());
        values.put(ClassContract.ClassEntry.COLUMN_LECTURE,
                lectureEditText.getText().toString());
        values.put(ClassContract.ClassEntry.COLUMN_TUTORIAL,
                tutorialEditText.getText().toString());

        // Get grading scheme.
        int sum = 0;
        ArrayList<String> componentNameValues = new ArrayList<>();
        ArrayList<String> componentWeightValues = new ArrayList<>();
        for (EditText weightEditText : componentWeightsEditTexts) {
            String weight = weightEditText.getText().toString();
            if (!weight.isEmpty()) {
                componentWeightValues.add(weight);
                try {
                    sum += Integer.parseInt(weight);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }

        if (!componentWeightValues.isEmpty() && sum != 100) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("The grading scheme must add up to 100%!")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    });

            AlertDialog alert = builder.create();
            alert.show();
            return false;
        } else {
            for (int i = 0; i < componentNameEditTexts.size(); ++i) {
                String name = componentNameEditTexts.get(i).getText().toString();
                if (!name.isEmpty()) {
                    componentNameValues.add(name);
                }
            }

            JSONArray names = new JSONArray(componentNameValues);
            JSONArray weights = new JSONArray(componentWeightValues);

            values.put(ClassContract.ClassEntry.COLUMN_GRADING, names.toString());
            values.put(ClassContract.ClassEntry.COLUMN_WEIGHTS, weights.toString());
        }

        // Save to current class.
        getContentResolver().update(klass, values, null, null);
        return true;
    }

    /***
     * Private Initialization Methods
     */

    // Initialize initial values for a new class and initialize the toolbar.
    private void initValues() {
        // Initialize the toolbar for this view.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        final Intent intent = getIntent();
        if (intent.getAction().equals(ACTION_NEW)) {
            toolbar.setTitle("Create New Class");
            // Create a new class and insert it into the database.
            ContentValues initValues = new ContentValues();

            // Initialize the starting values.
            initValues.put(ClassContract.ClassEntry.COLUMN_TERM, "");
            initValues.put(ClassContract.ClassEntry.COLUMN_SUBJECT, "");
            initValues.put(ClassContract.ClassEntry.COLUMN_ID, "");
            initValues.put(ClassContract.ClassEntry.COLUMN_TITLE, "");
            initValues.put(ClassContract.ClassEntry.COLUMN_INSTRUCTOR, "");
            initValues.put(ClassContract.ClassEntry.COLUMN_LECTURE, "");
            initValues.put(ClassContract.ClassEntry.COLUMN_TUTORIAL, "");
            initValues.put(ClassContract.ClassEntry.COLUMN_GRADING, "[]");
            initValues.put(ClassContract.ClassEntry.COLUMN_WEIGHTS, "[]");
            initValues.put(ClassContract.ClassEntry.COLUMN_MARKS, "{}");

            // Set the current class to be this new class.
            klass = getContentResolver().insert(ClassContract.ClassEntry.CONTENT_URI, initValues);
        } else if (intent.getAction().equals(ACTION_EDIT)) {
            toolbar.setTitle("Edit Class");
            // Set the current class to the selected one.
            klass = intent.getData();
        }

        setSupportActionBar(toolbar);
    }

    // Initialize view components.
    private void initView() {
        // Initialize views.
        termSpinner = (Spinner) findViewById(R.id.term_spinner);
        yearSpinner = (Spinner) findViewById(R.id.year_spinner);
        subjectEditText = (EditText) findViewById(R.id.subject_edittext);
        idEditText = (EditText) findViewById(R.id.id_edittext);
        titleEditText = (EditText) findViewById(R.id.title_edittext);
        instructorEditText = (EditText) findViewById(R.id.instructor_edittext);
        lectureEditText = (EditText) findViewById(R.id.lecture_edittext);
        tutorialEditText = (EditText) findViewById(R.id.tutorial_edittext);
        componentNameEditTexts = new ArrayList<EditText>();
        componentWeightsEditTexts = new ArrayList<EditText>();

        // Populate spinner.
        ArrayAdapter<String> termAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.terms));
        termAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayList<String> years = new ArrayList<String>();
        for (int i = 2000; i < 2030; ++i) {
            years.add(Integer.toString(i));
        }
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, years);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        termSpinner.setAdapter(termAdapter);
        yearSpinner.setAdapter(yearAdapter);
    }

    // Use a cursor to query data from the database and display it to the UI.
    private void displayData() {
        // Initialize cursor, get class text, and close.
        Cursor cursor = getContentResolver().query(klass, null, null, null, null);
        cursor.moveToFirst();
        String term = cursor.getString(
                cursor.getColumnIndexOrThrow(ClassContract.ClassEntry.COLUMN_TERM));
        String subject = cursor.getString(
                cursor.getColumnIndexOrThrow(ClassContract.ClassEntry.COLUMN_SUBJECT));
        String id = cursor.getString(
                cursor.getColumnIndexOrThrow(ClassContract.ClassEntry.COLUMN_ID));
        String title = cursor.getString(
                cursor.getColumnIndexOrThrow(ClassContract.ClassEntry.COLUMN_TITLE));
        String instructor = cursor.getString(
                cursor.getColumnIndexOrThrow(ClassContract.ClassEntry.COLUMN_INSTRUCTOR));
        String lecture = cursor.getString(
                cursor.getColumnIndexOrThrow(ClassContract.ClassEntry.COLUMN_LECTURE));
        String tutorial = cursor.getString(
                cursor.getColumnIndexOrThrow(ClassContract.ClassEntry.COLUMN_TUTORIAL));

        ArrayList<String> componentNameValues = parseJSONArray(
                cursor.getString(cursor.getColumnIndexOrThrow(ClassContract.ClassEntry.COLUMN_GRADING))
        );
        ArrayList<String> componentWeightValues = parseJSONArray(
                cursor.getString(cursor.getColumnIndexOrThrow(ClassContract.ClassEntry.COLUMN_WEIGHTS))
        );

        cursor.close();

        // Set the Spinners and each EditText accordingly.
        if (getIntent().getAction().equals(ACTION_NEW)) {
            setTermSpinner(termSpinner, yearSpinner, currentTerm());
        } else {
            setTermSpinner(termSpinner, yearSpinner, term);
        }

        subjectEditText.setText(subject);
        idEditText.setText(id);
        titleEditText.setText(title);
        instructorEditText.setText(instructor);
        lectureEditText.setText(lecture);
        tutorialEditText.setText(tutorial);

        for (int i = 0; i < componentNameValues.size(); ++i) {
            String name = componentNameValues.get(i);
            if (name != null && !name.isEmpty()) {
                addGradingComponent(findViewById(android.R.id.content));
            }

            componentNameEditTexts.get(i).setText(name);
            componentWeightsEditTexts.get(i).setText(componentWeightValues.get(i));
        }
    }

    /***
     * Lifecycle Methods
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_edit);

        initValues();
        initView();
        displayData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_done, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will automatically
        // handle clicks on the Home/Up button, so long as you specify a parent
        // activity in AndroidManifest.xml
        int id = item.getItemId();
        switch (id) {
            case R.id.action_done:
                if (subjectIdAreNull()) {
                    getContentResolver().delete(klass, null, null);
                    finish();
                } else if (save()) {
                    finish();
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (subjectIdAreNull()) {
            getContentResolver().delete(klass, null, null);
            super.onBackPressed();
        } else {
            if (save()) {
                super.onBackPressed();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!subjectIdAreNull()) save();
    }

    /***
     * OnClick Handlers
     */

    public void addGradingComponent(View view) {
        LinearLayout gradingView = (LinearLayout) findViewById(R.id.grading_view);

        LinearLayout.LayoutParams gcParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        gcParams.topMargin = 4;
        gcParams.bottomMargin = 4;
        LinearLayout.LayoutParams etParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        etParams.weight = 1;
        etParams.leftMargin = 4;

        LinearLayout gradingComponent = new LinearLayout(getApplicationContext());
        gradingComponent.setLayoutParams(gcParams);
        gradingComponent.setOrientation(LinearLayout.HORIZONTAL);

        EditText componentName = new EditText(getApplicationContext());
        componentName.setLayoutParams(etParams);
        componentName.setEms(10);
        componentName.setInputType(InputType.TYPE_CLASS_TEXT);

        EditText componentWeight = new EditText(getApplicationContext());
        componentWeight.setLayoutParams(etParams);
        componentWeight.setEms(10);
        componentWeight.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);

        componentNameEditTexts.add(componentName);
        componentWeightsEditTexts.add(componentWeight);

        gradingComponent.addView(componentName);
        gradingComponent.addView(componentWeight);
        gradingView.addView(gradingComponent);

        view.invalidate();
    }

    public void removeGradingComponent(View view) {
        LinearLayout gradingView = (LinearLayout) findViewById(R.id.grading_view);
        int last = componentNameEditTexts.size()-1;
        gradingView.removeViewAt(last);
        componentNameEditTexts.remove(last);
        componentWeightsEditTexts.remove(last);
        view.invalidate();
    }
}
