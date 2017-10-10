package com.example.luthfi.watclass;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.example.luthfi.watclass.data.ClassContract;

/**
 * Created by Luthfi on 25/8/2016.
 * This activity shows a list of terms from the classes the
 * user has stored in the database.
 */
public class TermListActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        AdapterView.OnItemClickListener,
        View.OnClickListener {

    /***
     * Constants
     */

    private static final int TERM_LOADER = 2;
    private static final int TERM_COLUMN = 1;
    private static final String[] PROJECTION = {
            ClassContract.ClassEntry._ID,
            ClassContract.ClassEntry.COLUMN_TERM
    };

    /***
     * Private Members
     */

    private SimpleCursorAdapter adapter;

    /***
     * Static Helper Methods
     */

    public static String termCodeToString(String termCode) {
        String year = "20" + termCode.substring(1, 3);
        String term;

        switch (Integer.parseInt(termCode.substring(3))) {
            case 1:
                term = "Winter";
                break;
            case 5:
                term = "Spring";
                break;
            case 9:
                term = "Fall";
                break;
            default:
                throw new UnsupportedOperationException("Unknown term code: " + termCode);
        }

        return term + " " + year;
    }

    public static String termStringToCode(String term) {
        Log.i("INFO", term);
        String season = term.split(" ")[0];
        String year = term.split(" ")[1];

        String termCode = "1" + year.substring(2, 4);
        if (season.equals("Winter")) {
            termCode += 1;
        } else if (season.equals("Spring")) {
            termCode += 5;
        } else if (season.equals("Fall")) {
            termCode += 9;
        } else {
            throw new UnsupportedOperationException("Unknown term: " + term);
        }

        return termCode;
    }

    /***
     * Private Initialization Methods
     */

    private void initListView() {
        ListView listView = (ListView) findViewById(R.id.listview);

        String[] from = new String[] {ClassContract.ClassEntry.COLUMN_TERM};
        int[] to = new int[] {android.R.id.text1};

        adapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_activated_1, null, from, to, 0);
        adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                TextView textView = (TextView) view;
                String termCode = cursor.getString(TERM_COLUMN);
                if (termCode.length() == 4) {
                    textView.setText(termCodeToString(termCode));
                    return true;
                } else {
                    return false;
                }
            }
        });

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        getSupportLoaderManager().initLoader(TERM_LOADER, null, this);
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("All Terms");
        setSupportActionBar(toolbar);
    }

    private void initFloatingActionButton() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);
    }

    /***
     * Lifecycle Methods
     */

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        initListView();
        initToolbar();
        initFloatingActionButton();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will automatically
        // handle clicks on the Home/Up button, so long as you specify a parent
        // activity in AndroidManifest.xml
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case TERM_LOADER:
                String SELECTION = "(" + ClassContract.ClassEntry.COLUMN_TERM
                        + " NOT NULL) GROUP BY ("
                        + ClassContract.ClassEntry.COLUMN_TERM + ")";
                return new CursorLoader(this, ClassContract.ClassEntry.CONTENT_URI,
                        PROJECTION, SELECTION, null, null);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.changeCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.changeCursor(null);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
        Intent intent = new Intent(this, ClassListActivity.class);
        TextView textView = (TextView) view;
        String termCode = null;
        try {
            termCode = termStringToCode(textView.getText().toString());
        } catch (ArrayIndexOutOfBoundsException exn) {
            exn.printStackTrace();
            termCode = ClassEditActivity.currentTerm();
        } finally {
            String action = ClassListActivity.ACTION_VIEW_TERM + termCode;
            intent.setAction(action);
            intent.setFlags(getIntent().getFlags());
            startActivity(intent);
        }
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent (this, ClassEditActivity.class);
        intent.setAction(ClassEditActivity.ACTION_NEW);
        startActivity(intent);
    }
}
