package com.example.luthfi.watclass;

import android.content.ContentUris;
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
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.example.luthfi.watclass.data.ClassContract;

import java.util.ArrayList;

/**
 * Created by Luthfi on 24/8/2016.
 */
public class ClassListActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>,
        AbsListView.MultiChoiceModeListener,
        View.OnClickListener,
        AdapterView.OnItemClickListener {

    public static final String VIEW_TERM = "view_term_";
    private static final int TERM_INDEX = 10;

    private static final int CLASS_LOADER = 1;
    private static final int SUBJECT_COLUMN = 2;
    private static final int ID_COLUMN = 3;

    private static final String[] PROJECTION = {
            ClassContract.ClassEntry._ID,
            ClassContract.ClassEntry.COLUMN_TERM,
            ClassContract.ClassEntry.COLUMN_SUBJECT,
            ClassContract.ClassEntry.COLUMN_ID,
            ClassContract.ClassEntry.COLUMN_TITLE
    };
    private SimpleCursorAdapter adapter;

    ArrayList<Long> selectedIDs = new ArrayList<Long>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        ListView listView = (ListView) findViewById(R.id.listview);

        // Parameters to initialize adapter.
        // By default, set the SimpleCursorAdapter to map a class's subject code and title to
        // each view in the ListView.
        String[] from = new String[] {
                ClassContract.ClassEntry.COLUMN_SUBJECT,
                ClassContract.ClassEntry.COLUMN_TITLE};
        int[] to = new int[] {android.R.id.text1, android.R.id.text2};

        // Initialize the SimpleCursorAdapter. Each view in the ListView should show two items.
        adapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_activated_2, null, from, to, 0);

        // Set a customized ViewBinder - make the header (first text) of each view to be the
        // full course code (i.e. concatenate the subject and ID fields)
        adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                if (view.getId() == android.R.id.text1) {
                    TextView textView = (TextView) view;
                    String subject = cursor.getString(SUBJECT_COLUMN);
                    String id = cursor.getString(ID_COLUMN);
                    textView.setText(subject + " " + id);

                    return true;
                }
                return false;
            }
        });

        // Set adapter to ListView and initialize loader.
        listView.setAdapter(adapter);
        getSupportLoaderManager().initLoader(CLASS_LOADER, null, this);

        // Initialize toolbar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        String termCode = getIntent().getAction().substring(TERM_INDEX);
        toolbar.setTitle(TermListActivity.termCodeToString(termCode));
        setSupportActionBar(toolbar);

        // Configure ListView.
        listView.setOnItemClickListener(this);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(this);

        // Initialize FAB.
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; adds items to the action bar if it is present.
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
            case CLASS_LOADER:
                String SELECTION = ClassContract.ClassEntry.COLUMN_TERM + "=?";
                final Intent intent = getIntent();
                String term = intent.getAction().substring(TERM_INDEX);
                String[] SELECTION_ARGS = {term};
                return new CursorLoader(this, ClassContract.ClassEntry.CONTENT_URI,
                        PROJECTION, SELECTION, SELECTION_ARGS, null);
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
    public void onItemCheckedStateChanged(ActionMode actionMode, int pos,
                                          long id, boolean checked) {
        if (checked) selectedIDs.add(id);
        else selectedIDs.remove(id);
        actionMode.setTitle(String.valueOf(selectedIDs.size()));
    }

    @Override
    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
        getMenuInflater().inflate(R.menu.menu_cab, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
        return false;
    }

    // Delete selected class(es).
    @Override
    public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_delete:
                for (long id : selectedIDs) {
                    getContentResolver().delete(
                            ContentUris.withAppendedId(ClassContract.ClassEntry.CONTENT_URI, id),
                            null,
                            null
                    );
                }
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onDestroyActionMode(ActionMode actionMode) {
        selectedIDs.clear();
    }

    // Creates a new class upon clicking the Floating Action Button.
    @Override
    public void onClick(View view) {
        // Creates a new Intent and associates it to this activity.
        Intent intent = new Intent(this, ClassEditActivity.class);
        // Set intent action to 'create new class'
        intent.setAction(ClassEditActivity.ACTION_NEW);
        // Initialize activity.
        startActivity(intent);
    }

    // Edit existing classes.
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
        // Creates a new Intent and associates it to this activity.
        Intent intent = new Intent(this, ClassEditActivity.class);
        // Assign the ID of the current selected note.
        intent.setData(ContentUris.withAppendedId(ClassContract.ClassEntry.CONTENT_URI, id));
        // Set intent action to 'edit class'.
        intent.setAction(ClassEditActivity.ACTION_EDIT);
        // Start activity.
        startActivity(intent);
    }
}
