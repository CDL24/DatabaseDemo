package com.example.climbachiya.databasedemo;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    EditText edtName, edtEmail;
    Spinner mSpinner;
    RadioGroup rdbGroup;
    RadioButton rdbMale,rdbFemale;
    CheckBox chkAndroid, chkIphone;
    ArrayAdapter<String> dataAdapter;
    String gender = null;
    //class members
    String businessType[] = {"Automobile", "Food", "Computers", "Education",
            "Personal", "Travel"};

    DatabaseHandler dbHandler = null;
    ProgressDialog progress = null;
    String spinnerItem = null;
    ArrayList<String> arrInterest;
    boolean isEditMode = false;
    String rowId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUIControls();

        initClassObjects();

        registerEvents();

        receiveIntentData();

    }

    private void receiveIntentData() {

        if(null != getIntent().getStringExtra("ROW_ID")){ //User is in Edit mode
            rowId = getIntent().getStringExtra("ROW_ID");

            if(null != rowId){
                Log.v("receiveIntentData :: ","_id : "+rowId);
                isEditMode = true;
                getSetDataFromDB(rowId);
            }
        }
    }

    /**
     * get set data based on id from DB
     * @param id - row id
     */
    private void getSetDataFromDB(String id) {
        dbHandler = DatabaseHandler.getInstance(this);
        try {
            String SQL = "SELECT * FROM "+DatabaseHandler.TABLE_CONTACTS+" WHERE "+DatabaseHandler.KEY_ID+" = ?";
            Cursor cursor = dbHandler.getDataByCustomQuery(SQL, new String[]{id});
            if(cursor != null && cursor.getCount() > 0){

                cursor.moveToFirst();
                String _name = cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_NAME));
                String _email = cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_EMAIL));
                String _gender = cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_GENDER));
                String _interest = cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_INTEREST));
                String _course = cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_COURSE));
                Log.v("_course",_course);
                Log.v("_interest",_interest);
                if(_name != null){
                    edtName.setText(_name);
                }
                if(_email != null){
                    edtEmail.setText(_email);
                }
                if(_gender != null){
                    if(_gender.equalsIgnoreCase("male")){
                        gender = "male";
                        rdbMale.setChecked(true);
                    }else if(_gender.equalsIgnoreCase("female")){
                        gender = "female";
                        rdbFemale.setChecked(true);
                    }
                }

                if(_interest != null){
                    if(_interest.contains(",")){
                        String[] choice = _interest.split(",");
                        if(choice[0].equalsIgnoreCase("Android") || choice[1].equalsIgnoreCase("Android")){
                            chkAndroid.setChecked(true);
                        }
                        if(choice[0].equalsIgnoreCase("iPhone") || choice[1].equalsIgnoreCase("iPhone")){
                            chkIphone.setChecked(true);
                        }

                    }else{
                        if(_interest.equalsIgnoreCase("Android")){
                            chkAndroid.setChecked(true);
                        }else if(_interest.equalsIgnoreCase("iPhone")){
                            chkIphone.setChecked(true);
                        }
                    }
                }

                if(null != _course){
                    //if(mSpinner.getCount()_course)
                    int index = Arrays.asList(businessType).indexOf(_course);
                    Log.v("index : ",""+index);
                    mSpinner.setSelection(index, true);
                    spinnerItem = _course;
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void registerEvents() {
        rdbGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // find which radio button is selected
                if (checkedId == R.id.radio_male) {
                    gender = "male";
                } else if (checkedId == R.id.radio_female) {
                    gender = "female";
                }
            }

        });

        chkAndroid.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    arrInterest.add("android");
                }else{
                    if(arrInterest.contains("android")){
                        arrInterest.remove("android");
                    }
                }

            }
        });
        chkIphone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    if (isChecked){
                        arrInterest.add("iphone");
                    }else{
                        if(arrInterest.contains("iphone")){
                            arrInterest.remove("iphone");
                        }
                    }

            }
        });

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinnerItem = mSpinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    //Init custom class objects
    private void initClassObjects() {
        progress = new ProgressDialog(this);
        progress.setMessage("Saving...");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);


        arrInterest = new ArrayList<>();

        dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, businessType);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mSpinner.setAdapter(dataAdapter);
    }

    //Init all UI controls here
    private void initUIControls() {
        edtName = (EditText) findViewById(R.id.edit_name);
        edtEmail = (EditText) findViewById(R.id.edit_email);
        mSpinner = (Spinner) findViewById(R.id.spinner);
        rdbGroup = (RadioGroup) findViewById(R.id.radio_group);
        rdbMale = (RadioButton) findViewById(R.id.radio_male);
        rdbFemale = (RadioButton) findViewById(R.id.radio_female);
        chkAndroid = (CheckBox) findViewById(R.id.check_android);
        chkIphone = (CheckBox) findViewById(R.id.check_iphone);

    }

    //View all records from DB
    public void viewAllRecords(View view) {
        startActivity(new Intent(this, ViewData.class));
    }

    //Save record in DB
    public void saveData(View view) {

        String name = edtName.getText().toString();
        String email = edtEmail.getText().toString();


        if (null == name || name.isEmpty()) {
            Toast.makeText(MainActivity.this, "Name should not be empty", Toast.LENGTH_SHORT).show();
        } else if (null == email || email.isEmpty()) {
            Toast.makeText(MainActivity.this, "Email should not be empty", Toast.LENGTH_SHORT).show();
        } else if (!isEmailValid(email)) {
            Toast.makeText(MainActivity.this, "Enter valid email", Toast.LENGTH_SHORT).show();
        } else if (null == gender || gender.isEmpty()) {
            Toast.makeText(MainActivity.this, "Select gender", Toast.LENGTH_SHORT).show();
        }else if (null == spinnerItem || spinnerItem.isEmpty()) {
            Toast.makeText(MainActivity.this, "Select course", Toast.LENGTH_SHORT).show();
        }else if (null == arrInterest || arrInterest.size() <= 0) {
            Toast.makeText(MainActivity.this, "Select interest", Toast.LENGTH_SHORT).show();
        } else {
              //call async for save data in background
            new SaveAsync(name, email, spinnerItem, gender, arrInterest).execute();
        }
    }

    /**
     * Check for valid email
     *
     * @param email - email address
     * @return true/false
     */
    public boolean isEmailValid(String email) {
        final String EMAIL_PATTERN =
                "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        final Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        final Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    //Save data asychronously in DB
    class SaveAsync extends AsyncTask<String, String, String> {

        long result = 0;

        String name;
        String email;
        String spinnerItem;
        String gender;
        ArrayList<String> arrInterest;

        public SaveAsync(String name, String email, String spinnerItem, String gender, ArrayList<String> arrInterest) {
            this.name = name;
            this.email = email;
            this.spinnerItem = spinnerItem;
            this.gender = gender;
            this.arrInterest = arrInterest;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress.show();
        }

        @Override
        protected String doInBackground(String... params) {

            ContentValues cv = null;
            try {
                cv = new ContentValues();
                dbHandler = DatabaseHandler.getInstance(MainActivity.this);

                String interestString = "";

                for(int i=0; i<arrInterest.size(); i++){
                    interestString = interestString +","+arrInterest.get(i);
                }

                interestString = interestString.startsWith(",") ? interestString.substring(1) : interestString;


                cv.put(DatabaseHandler.KEY_NAME, name);
                cv.put(DatabaseHandler.KEY_EMAIL, email);
                cv.put(DatabaseHandler.KEY_GENDER, gender);
                cv.put(DatabaseHandler.KEY_INTEREST, interestString);
                cv.put(DatabaseHandler.KEY_COURSE, spinnerItem);

                if(!isEditMode){ //Add Mode
                    result = dbHandler.insertQuery(cv, DatabaseHandler.TABLE_CONTACTS);
                }else{ //Edit Mode

                    String whereClause = DatabaseHandler.KEY_ID + " = ? ";
                    String[] whereArgs = new String[]{rowId};
                    result = dbHandler.updateQuery(DatabaseHandler.TABLE_CONTACTS, cv, whereClause, whereArgs);
                }

            } catch (Exception e) {
                result = 0;
                e.printStackTrace();
            } finally {
                if (null != dbHandler) {
                    dbHandler.close();
                }
                if (null != cv) {
                    cv.clear();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(null != progress && progress.isShowing())progress.dismiss();

            if (result > 0) {
                refreshView();
                if(isEditMode){
                    startActivity(new Intent(MainActivity.this, ViewData.class));
                    finish();
                }
                Toast.makeText(MainActivity.this, "Saved...", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Oops!Something wrong while saving", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void refreshView() {
        edtName.setText("");
        edtEmail.setText("");
        rdbMale.setChecked(false);
        rdbFemale.setChecked(false);
        chkAndroid.setChecked(false);
        chkIphone.setChecked(false);
        mSpinner.setSelection(0, true);
        edtName.setFocusable(true);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
