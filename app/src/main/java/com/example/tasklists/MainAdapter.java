package com.example.tasklists;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {
   //initialize variables
    private final List<MainData> dataList;
    private final Activity context;
    private  RoomDB database;

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

    //create constructor
    @SuppressLint("NotifyDataSetChanged")
    public MainAdapter(List<MainData> dataList, Activity context) {
        this.dataList = dataList;
        this.context = context;
        notifyDataSetChanged();
    }

    @NonNull

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Init view
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_main,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MainAdapter.ViewHolder holder, int position) {
        // Init Main Data
        MainData data = dataList.get(position);

        // Init Database
        database = RoomDB.getInstance(context);

        // Set text in textview
        holder.textView.setText(data.getText());
        holder.dateView.setText(data.getDate());
        holder.timeView.setText(data.getTime());

        holder.btEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Init main data
                MainData data = dataList.get(holder.getAdapterPosition());

                // Get id
                int sID = data.getID();
                Log.d("MainAdapter", "ID: " + sID);

                // Get text
                String taskText = data.getText();
                String dateText = data.getDate();
                String timeText = data.getTime();

                // Create dialog
                Dialog dialog = new Dialog(context);

                // Set content view
                dialog.setContentView(R.layout.dialog_update);

                // Init width
                int width = WindowManager.LayoutParams.MATCH_PARENT;

                // Init height
                int height = WindowManager.LayoutParams.WRAP_CONTENT;

                // Set layout
                dialog.getWindow().setLayout(width, height);

                // Show dialog
                dialog.show();

                // Init and assign variable
                EditText editText = dialog.findViewById(R.id.edit_text);
                EditText editPickDate = dialog.findViewById(R.id.edit_update_pick_date);
                EditText editSelectedTime = dialog.findViewById(R.id.edit_update_selected_time);
                Button btUpdate = dialog.findViewById(R.id.bt_update);

                // Set text on edit text
                editText.setText(taskText);
                editPickDate.setText(dateText);
                editSelectedTime.setText(timeText);

                // Pick Date
                editPickDate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Calendar calendar = Calendar.getInstance();
                        int year = calendar.get(Calendar.YEAR);
                        int month = calendar.get(Calendar.MONTH);
                        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

                        DatePickerDialog datePickerDialog = new DatePickerDialog(
                                context,
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

                //select time
                editSelectedTime.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Get current time
                        final Calendar calendar = Calendar.getInstance();
                        int hour = calendar.get(Calendar.HOUR_OF_DAY);
                        int minute = calendar.get(Calendar.MINUTE);

                        // Create a TimePickerDialog
                        TimePickerDialog timePickerDialog = new TimePickerDialog(
                                context,
                                new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                        // Format selected time
                                        String selectedTime = formatTime(hourOfDay, minute);
                                        editSelectedTime.setText(selectedTime);
                                    }
                                },
                                hour, minute, false); // false for AM/PM format

                        // Show the TimePickerDialog
                        timePickerDialog.show();
                    }
                });

                btUpdate.setOnClickListener(new View.OnClickListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onClick(View v) {
                        // Dismiss dialog
                        dialog.dismiss();

                        // Get update text from edit text
                        String updateText = editText.getText().toString().trim();
                        String updateDateText = editPickDate.getText().toString().trim();
                        String updateTimeText = editSelectedTime.getText().toString().trim();

                        if (!updateText.equals("") && !updateDateText.equals("") && !updateTimeText.equals("")) {
                            // Update text in db
                            database.mainDao().update(sID, updateText, updateDateText, updateTimeText);

                            // Notify when data is updated
                            dataList.clear();
                            dataList.addAll(database.mainDao().getAll());
                            notifyDataSetChanged();
                        } else {
                            builder = new AlertDialog.Builder(context);

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

        holder.btDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder= new AlertDialog.Builder(v.getContext());

                // Setting message manually and performing action on button click
                builder.setMessage("Are you sure you want to delete this task?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                MainData d = dataList.get(holder.getAdapterPosition());

                                // Delete text from database
                                database.mainDao().delete(d);

                                // Notify when data is deleted
                                int position = holder.getAdapterPosition();
                                dataList.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position,dataList.size());
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Action for 'NO' Button
                                dialog.cancel();
                            }
                        });
                // Creating dialog box
                AlertDialog alert = builder.create();

                // Setting the title manually
                alert.setTitle("Delete Confirmation");
                alert.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Init variables
        TextView textView, dateView, timeView;
        ImageView btEdit,btDelete;

        public ViewHolder(@NonNull  View itemView) {
            super(itemView);
            // Assign variable
            textView = itemView.findViewById(R.id.text_view);
            dateView = itemView.findViewById(R.id.text_date);
            timeView = itemView.findViewById(R.id.text_time);

            btEdit = itemView.findViewById(R.id.bt_edit);
            btDelete = itemView.findViewById(R.id.bt_delete);
        }
    }
}
