package com.example.luthfi.watclass;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

/***
 * Main activity.
 */
public class MainActivity extends AppCompatActivity {

    /***
     * Lifecycle Methods
     */

    /***
     * Initializes the activity, setting the toolbar.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /***
     * OnClick Handlers
     */

    /***
     * Sends the user to ClassEditActivity to create a new class.
     * @param view  The current view.
     */
    public void newClass(View view) {
        Intent intent = new Intent(this, ClassEditActivity.class);
        intent.setAction(ClassEditActivity.ACTION_NEW);
        startActivity(intent);
    }

    /***
     * Sends the user to ClassListActivity, set to the current term.
     * @param view  The current view.
     */
    public void currentTermClasses(View view) {
        Intent intent = new Intent(this, ClassListActivity.class);
        String viewCurrentTerm = ClassListActivity.ACTION_VIEW_TERM + ClassEditActivity.currentTerm();
        intent.setAction(viewCurrentTerm);
        intent.setFlags(ClassListActivity.FLAG_EDIT_CLASS);
        startActivity(intent);
    }

    /***
     * Sends the user to TermListActivity to view all terms.
     * @param view  The current view.
     */
    public void viewAllTerms(View view) {
        Intent intent = new Intent(this, TermListActivity.class);
        intent.setFlags(ClassListActivity.FLAG_EDIT_CLASS);
        startActivity(intent);
    }

    /***
     * Sends the user to TermListActivity, allowing them to pick
     * the term and class for which they want to calculate final marks.
     * @param view  The current view.
     */
    public void calculateGrade(View view) {
        Intent intent = new Intent(this, TermListActivity.class);
        intent.setFlags(ClassListActivity.FLAG_ENTER_GRADE);
        startActivity(intent);
    }
}
