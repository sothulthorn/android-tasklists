package com.example.tasklists;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {
   //initialize variables
    private final List<MainData> dataList;
    private final Activity context;
    private  RoomDB database;

    AlertDialog.Builder builder;

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

        holder.btEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Init main data
                MainData d = dataList.get(holder.getAdapterPosition());

                // Get id
                int sID = d.getID();

                // Get text
                String sText = d.getText();

                // Create dialog
                Dialog dialog = new Dialog(context);

                // Set content view
                dialog.setContentView(R.layout.dialog_update);

                // Init width
                int width = WindowManager.LayoutParams.MATCH_PARENT;

                // Init height
                int height = WindowManager.LayoutParams.WRAP_CONTENT;

                // Set layout
                dialog.getWindow().setLayout(width,height);

                // Show dialog
                dialog.show();

                // Init and assign variable
                EditText editText = dialog.findViewById(R.id.edit_text);
                Button btUpdate = dialog.findViewById(R.id.bt_update);

                // Set text on edit text
                editText.setText(sText);

                btUpdate.setOnClickListener(new View.OnClickListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onClick(View v) {
                        // Dismiss dialog
                        dialog.dismiss();

                        // Get update text from edit text
                        String uText=editText.getText().toString().trim();

                        // Update text in db
                        database.mainDao().upate(sID,uText);

                        // Notify when data is updated
                        dataList.clear();
                        dataList.addAll(database.mainDao().getAll());
                        notifyDataSetChanged();
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
        TextView textView;
        ImageView btEdit,btDelete;

        public ViewHolder(@NonNull  View itemView) {
            super(itemView);
            // Assign variable
            textView = itemView.findViewById(R.id.text_view);
            btEdit = itemView.findViewById(R.id.bt_edit);
            btDelete = itemView.findViewById(R.id.bt_delete);
        }
    }
}
