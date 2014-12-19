package be.sourcery.ascent;

/*
 * This file is part of Ascent.
 *
 *  Ascent is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Ascent is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Ascent.  If not, see <http://www.gnu.org/licenses/>.
 */

import java.util.Date;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

public class MaterialMainActivity extends MyActivity {

    private static final int EXPORT_DATA_REQUEST = 1;
    private static final int IMPORT_DATA_REQUEST = 2;
    private static final int REPEAT_REQUEST = 3;

    private InternalDB db;
    private Cursor cursor;
    private RecyclerView recyclerView;
    private MyListCursorAdapter cursorAdapter;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_material);
        setTitle(R.string.latestAscents);
        db = new InternalDB(this);
        populateList();
        update();
    }

    private void populateList() {
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        cursor = db.getAscentsCursor();
        cursor.moveToFirst();
        cursorAdapter = new MyListCursorAdapter(this, cursor, db);
        recyclerView.setAdapter(cursorAdapter);
    }

    private void update() {
        cursor.requery();
        TextView countView = (TextView) this.findViewById(R.id.countView);
        int count12 = db.getCountLast12Months();
        int count = db.getCountAllTime();
        countView.setText("Ascents: " + count + " - 12M: " + count12);
        TextView scoreView = (TextView) this.findViewById(R.id.scoreView);
        Date now = new Date();
        int year = now.getYear() + 1900;
        scoreView.setText("Score: " + db.getScoreLast12Months() + " - All Time: " + db.getScoreAllTime() + " - Year: " + db.getScoreForYear(year));
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_actionbar, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        Ascent ascent = db.getAscent(info.id);
        switch (item.getItemId()) {
            case R.id.delete:
                db.deleteAscent(ascent);
                update();
                return true;
            case R.id.repeat:
                repeatAscent(ascent);
                update();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (EXPORT_DATA_REQUEST) : {
                if (resultCode == Activity.RESULT_OK) {
                    int count = data.getIntExtra("count", 0);
                    Toast.makeText(this, "Exported " + count + " ascents", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Failed exporting ascents", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case (IMPORT_DATA_REQUEST) : {
                if (resultCode == Activity.RESULT_OK) {
                    int count = data.getIntExtra("count", 0);
                    Toast.makeText(this, "Imported " + count + " ascents", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Failed importing ascents", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
        update();
    }

    public void onDestroy() {
        super.onDestroy();
        db.close();
    }

    public void onResume() {
        super.onResume();
        update();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_crags:
                cragsList();
                return true;
            case R.id.menu_import:
                importData();
                return true;
            case R.id.menu_export:
                exportData();
                return true;
            case R.id.menu_add:
                addAscent();
                return true;
            case R.id.menu_score:
                showScore();
                return true;
            case R.id.menu_top10:
                showTop10();
                return true;
            case R.id.menu_grade:
                showGrades();
                return true;
            case R.id.menu_projects:
                projectsList();
                return true;
        }
        return false;
    }

    private void showScore() {
        Intent myIntent = new Intent(this, ScoreGraphActivity.class);
        startActivity(myIntent);
    }

    private void showGrades() {
        Intent myIntent = new Intent(this, GradeGraphActivity.class);
        startActivity(myIntent);
    }

    private void showTop10() {
        Intent myIntent = new Intent(this, Top10Activity.class);
        startActivity(myIntent);
    }

    private void editAscent(Ascent ascent) {
        Intent myIntent = new Intent(this, EditAscentActivity.class);
        Bundle b = new Bundle();
        b.putLong("ascentId", ascent.getId());
        myIntent.putExtras(b);
        startActivity(myIntent);
    }

    private void importData() {
        Intent myIntent = new Intent(this, ImportDataActivity.class);
        startActivityForResult(myIntent, IMPORT_DATA_REQUEST);
    }

    private void exportData() {
        Intent myIntent = new Intent(this, ExportDataActivity.class);
        startActivityForResult(myIntent, EXPORT_DATA_REQUEST);
    }

    private void repeatAscent(Ascent ascent) {
        Intent myIntent = new Intent(this, RepeatAscentActivity.class);
        Bundle b = new Bundle();
        b.putLong("ascentId", ascent.getId());
        myIntent.putExtras(b);
        startActivityForResult(myIntent, REPEAT_REQUEST);
    }

    private void addCrag() {
        Intent myIntent = new Intent(this, AddCragActivity.class);
        startActivityForResult(myIntent, 0);
    }

    private void addAscent() {
        Intent myIntent = new Intent(this, AddAscentActivity.class);
        startActivityForResult(myIntent, 0);
    }

    private void cragsList() {
        Intent myIntent = new Intent(this, CragListActivity.class);
        startActivityForResult(myIntent, 0);
    }

    private void projectsList() {
        Intent myIntent = new Intent(this, ProjectListActivity.class);
        startActivityForResult(myIntent, 0);
    }

}