package com.example.climbachiya.databasedemo;

import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by C.limbachiya on 6/3/2016.
 */
public class ViewData extends AppCompatActivity {

    List<Modal> modalList;
    RecyclerView mRecyclerView;
    ProgressBar progressBar;
    DatabaseHandler dbHandler = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_all_records);

        initUIControls();
        new GetDataAsync().execute();
    }

    private void initUIControls() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recycleview);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
    }

    class GetDataAsync extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {

            viewAllRecords();
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressBar.setVisibility(View.INVISIBLE);
            if (null != modalList && modalList.size() > 0) {
                mRecyclerView.setHasFixedSize(true);
                mRecyclerView.setAdapter(new MyRecyclerAdapter(ViewData.this, modalList));
                mRecyclerView.setLayoutManager(new LinearLayoutManager(ViewData.this));
                mRecyclerView.setItemAnimator(new DefaultItemAnimator());
            } else {
                Toast.makeText(ViewData.this, "No records found!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //View all records from DB
    public void viewAllRecords() {
        try {
            modalList = new ArrayList<>();

            modalList.clear();
            dbHandler = DatabaseHandler.getInstance(this);
            String sql = "SELECT * FROM " + DatabaseHandler.TABLE_CONTACTS;
            Cursor cursor = dbHandler.getDataByCustomQuery(sql, null);
            if (null != cursor && cursor.getCount() > 0) {
                cursor.moveToFirst();
                for (int i = 0; i < cursor.getCount(); i++) {
                    String rowId = cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_ID));
                    String name = cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_NAME));
                    String email = cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_EMAIL));
                    String gender = cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_GENDER));
                    String interest = cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_INTEREST));
                    String course = cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_COURSE));

                    modalList.add(new Modal(rowId, name, email, gender, interest, course));
                    cursor.moveToNext();
                }
            }
        } catch (Exception e) {

        }
    }
}
