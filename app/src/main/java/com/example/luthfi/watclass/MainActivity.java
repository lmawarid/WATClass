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

public class MainActivity extends AppCompatActivity {

    public void newClass(View view) {
        // Create a new class upon clicking the new class button.
        Intent intent = new Intent(this, ClassEditActivity.class);
        intent.setAction(ClassEditActivity.ACTION_NEW);
        startActivity(intent);
    }

    public void currentTermClasses(View view) {
        // View current term's classes.
        Intent intent = new Intent(this, ClassListActivity.class);
        String viewCurrentTerm = ClassListActivity.VIEW_TERM + ClassEditActivity.currentTerm();
        intent.setAction(viewCurrentTerm);
        startActivity(intent);
    }

    public void viewAllTerms(View view) {
        Intent intent = new Intent(this, TermListActivity.class);
        startActivity(intent);
    }

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
}
