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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.luthfi.watclass.data.ClassContract;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Luthfi on 22/8/2016.
 */
public class ClassEditActivity extends AppCompatActivity {

    public static final String ACTION_NEW = "action_new";
    public static final String ACTION_EDIT = "action_edit";

    private Uri klass;

    private Spinner termSpinner;
    private Spinner yearSpinner;
    private EditText subjectEditText;
    private EditText idEditText;
    private EditText titleEditText;
    private EditText instructorEditText;
    private EditText lectureEditText;
    private EditText tutorialEditText;

    private EditText assnEditText;
    private EditText mid1EditText;
    private EditText mid2EditText;
    private EditText finalsEditText;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_edit);

        // Initialize the toolbar for this view.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Edit Class");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final Intent intent = getIntent();

        if (intent.getAction().equals(ACTION_NEW)) {
            // Create a new class and insert it into the database.
            ContentValues initValues = new ContentValues();

            // Initialize the starting (blank) values.
            initValues.put(ClassContract.ClassEntry.COLUMN_TERM, "");
            initValues.put(ClassContract.ClassEntry.COLUMN_SUBJECT, "");
            initValues.put(ClassContract.ClassEntry.COLUMN_ID, "");
            initValues.put(ClassContract.ClassEntry.COLUMN_TITLE, "");
            initValues.put(ClassContract.ClassEntry.COLUMN_INSTRUCTOR, "");
            initValues.put(ClassContract.ClassEntry.COLUMN_LECTURE, "");
            initValues.put(ClassContract.ClassEntry.COLUMN_TUTORIAL, "");

            // Set the current class to be this new class.
            klass = getContentResolver().insert(ClassContract.ClassEntry.CONTENT_URI, initValues);
        } else if (intent.getAction().equals(ACTION_EDIT)) {
            // Set the current class to the selected one.
            klass = intent.getData();
        }

        // Initialize views.
        termSpinner = (Spinner) findViewById(R.id.term_spinner);
        yearSpinner = (Spinner) findViewById(R.id.year_spinner);
        subjectEditText = (EditText) findViewById(R.id.subject_edittext);
        idEditText = (EditText) findViewById(R.id.id_edittext);
        titleEditText = (EditText) findViewById(R.id.title_edittext);
        instructorEditText = (EditText) findViewById(R.id.instructor_edittext);
        lectureEditText = (EditText) findViewById(R.id.lecture_edittext);
        tutorialEditText = (EditText) findViewById(R.id.tutorial_edittext);

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
        cursor.close();

        // Set the Spinners and each EditText accordingly.
        if (intent.getAction().equals(ACTION_NEW)) {
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

        if (id == R.id.action_done) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Check if the subject and ID fields are empty (if so, don't save the class!)
    private boolean subjectIdAreNull() {
        return subjectEditText.getText().toString().isEmpty() &&
                idEditText.getText().toString().isEmpty();
    }

    // Save data.
    private void save() {
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

        /*
        // Get grading scheme.
        String assnWeight = assnEditText.getText().toString();
        String mid1Weight = mid1EditText.getText().toString();
        String mid2Weight = mid2EditText.getText().toString();
        String finalWeight = finalsEditText.getText().toString();

        int assn = 0;
        int mid1 = 0;
        int mid2 = 0;
        int finals = 0;

/*        boolean allBlank = true;

        if (!assnWeight.isEmpty()) {
            allBlank = false;
            assn = Integer.parseInt(assnWeight);
        }
        if (!mid1Weight.isEmpty()) {
            allBlank = false;
            mid1 = Integer.parseInt(mid1Weight);
        }
        if (!mid2Weight.isEmpty()) {
            allBlank = false;
            mid2 = Integer.parseInt(mid2Weight);
        }
        if (!finalWeight.isEmpty()) {
            allBlank = false;
            finals = Integer.parseInt(finalWeight);
        }

        int sum = assn + mid1 + mid2 + finals;

        if (!allBlank && sum != 100) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("The grading scheme must add up to 100%!")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    });

            AlertDialog alert = builder.create();
            alert.show();
        //} else {
            values.put(ClassContract.ClassEntry.COLUMN_ASSN, assnWeight);
            values.put(ClassContract.ClassEntry.COLUMN_MID1, mid1Weight);
            values.put(ClassContract.ClassEntry.COLUMN_MID2, mid2Weight);
            values.put(ClassContract.ClassEntry.COLUMN_FINAL, finalWeight);
        //}*/

        // Save to current class.
        getContentResolver().update(klass, values, null, null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!subjectIdAreNull()) save();
    }
}
