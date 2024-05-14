package com.example.tasklists;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    EditText editAddTask;
    Button btnAddTask;

    EditText editPickDate;
    EditText editSelectTime;

    RecyclerView recyclerView;

    FloatingActionButton fabButton;

    List<MainData> dataList = new ArrayList<>();
    LinearLayoutManager linearLayoutManager;
    RoomDB database;

    MainAdapter mainAdapter;

    AlertDialog.Builder builder;

    private String formatTime(int hourOfDay, int minute) {
        String timeFormat;
        if (hourOfDay == 0) {
            hourOfDay += 12;
            timeFormat = "AM";
        } else if (hourOfDay == 12) {
            timeFormat = "PM";
        } else if (hourOfDay > 12) {
            hourOfDay -= 12;
            timeFormat = "PM";
        } else {
            timeFormat = "AM";
        }

        return String.format(Locale.getDefault(), "%d:%02d %s", hourOfDay, minute, timeFormat);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fabButton = findViewById(R.id.fab);
        recyclerView = findViewById(R.id.recycler_view);

        // Initial Database
        database = RoomDB.getInstance(this);

        // Store db value in datalist
        dataList = database.mainDao().getAll();

        // Init linear layout manager
        linearLayoutManager = new LinearLayoutManager(this);

        // Set layout manager
        recyclerView.setLayoutManager(linearLayoutManager);

        // Init adapter
        mainAdapter = new MainAdapter(dataList, MainActivity.this);

        // Set adapter
        recyclerView.setAdapter(mainAdapter);

        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create dialog
                Dialog dialog = new Dialog(MainActivity.this);

                // Set content view
                dialog.setContentView(R.layout.dialog_add);

                // Init width
                int width = WindowManager.LayoutParams.MATCH_PARENT;

                // Init height
                int height = WindowManager.LayoutParams.WRAP_CONTENT;

                // Set layout
                dialog.getWindow().setLayout(width, height);

                // Show dialog
                dialog.show();

                // Init and assign variable
                editAddTask = dialog.findViewById(R.id.edit_add_text);
                btnAddTask = dialog.findViewById(R.id.btn_add_task);

                editPickDate = dialog.findViewById(R.id.edit_pick_date);
                editSelectTime = dialog.findViewById(R.id.edit_selected_time);

                // Pick Date
                editPickDate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Calendar calendar = Calendar.getInstance();
                        int year = calendar.get(Calendar.YEAR);
                        int month = calendar.get(Calendar.MONTH);
                        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

                        DatePickerDialog datePickerDialog = new DatePickerDialog(
                                MainActivity.this,
                                new DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                        // Format selected date
                                        Calendar selectedDateCalendar = Calendar.getInstance();
                                        selectedDateCalendar.set(year, month, dayOfMonth);
                                        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMMM dd yyyy", Locale.US);
                                        String selectedDate = sdf.format(selectedDateCalendar.getTime());
                                        editPickDate.setText(selectedDate);
                                    }
                                },
                                year, month, dayOfMonth);

                        datePickerDialog.show();
                    }
                });

                // Select Time
                editSelectTime = dialog.findViewById(R.id.edit_selected_time);
                editSelectTime.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Get current time
                        final Calendar calendar = Calendar.getInstance();
                        int hour = calendar.get(Calendar.HOUR_OF_DAY);
                        int minute = calendar.get(Calendar.MINUTE);

                        // Create a TimePickerDialog
                        TimePickerDialog timePickerDialog = new TimePickerDialog(
                                MainActivity.this,
                                new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                        // Format selected time
                                        String selectedTime = formatTime(hourOfDay, minute);
                                        editSelectTime.setText(selectedTime);
                                    }
                                },
                                hour, minute, false); // false for AM/PM format

                        // Show the TimePickerDialog
                        timePickerDialog.show();
                    }
                });

                btnAddTask.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();

                        // Get string from edit text
                        String taskText = editAddTask.getText().toString().trim();
                        if (!taskText.equals("")) {
                            // Init main data
                            MainData data = new MainData();

                            // Set text on main data
                            data.setText(taskText);

                            // Insert text in database
                            database.mainDao().insert(data);

                            // Notify when data is inserted
                            dataList.clear();
                            Toast.makeText(MainActivity.this, "Successfully added!", Toast.LENGTH_LONG).show();

                            dataList.addAll(database.mainDao().getAll());

                            mainAdapter.notifyDataSetChanged();
                        } else {
                            builder = new AlertDialog.Builder(MainActivity.this);

                            // Setting message manually and performing action on button click
                            builder.setMessage("The text field must not be empty!!")
                                    .setCancelable(false)
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });

                            // Creating dialog box
                            AlertDialog alert = builder.create();

                            // Setting the title manually
                            alert.setTitle("Invalid");
                            alert.show();
                        }
                    }
                });
            }
        });
    }
}