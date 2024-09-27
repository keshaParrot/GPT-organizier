package com.example.gptorganizier.Menu;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.gptorganizier.R;
import com.example.gptorganizier.adapters.RecordAdapter;
import com.example.gptorganizier.domain.Record;
import com.example.gptorganizier.service.DatabaseService;
import com.example.gptorganizier.utils.StringUtils;

import java.util.Date;

public class MenuManager {

    //TODO maybe we need to implement here toast to notify user about add/delete/edit records
    //Toast.makeText(v.getContext(), "Редагувати: " + item.getHeader(), Toast.LENGTH_SHORT).show();

    private final Context context;
    private View currentView;
    private Dialog dialog;
    private final DatabaseService databaseService;
    private static MenuManager instance;

    private MenuManager(Context context) {
        this.context = context;
        this.databaseService = DatabaseService.getInstance();
    }
    public static synchronized MenuManager getInstance(Context context) {
        if (instance == null) {
            instance = new MenuManager(context);
        }
        return instance;
    }

    public static synchronized MenuManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("Menu manager is not initialized. Call getInstance(Context) first.");
        }
        return instance;
    }

    public void showConfirmDialog(Long id, String message, ConfirmDialogListener listener) {
        dialog = new Dialog(context);
        currentView = LayoutInflater.from(context).inflate(R.layout.confirm_dialog, null);
        dialog.setContentView(currentView);


        Button cancelButton = currentView.findViewById(R.id.cancel_Button);
        Button acceptButton = currentView.findViewById(R.id.accept_Button);
        TextView confirmMessage = currentView.findViewById(R.id.confirm_menu_message);
        confirmMessage.setText("Are you sure you want to "+message+"?");

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                listener.onDialogResult(false);
            }
        });

        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                listener.onDialogResult(true);
            }
        });
        dialog.show();
    }
    public void showEditRecordMenu(Long id) {
        Dialog dialog = new Dialog(context);
        View view = LayoutInflater.from(context).inflate(R.layout.open_record_dialog, null);
        dialog.setContentView(view);

        EditText recordType = view.findViewById(R.id.record_type_editText);
        EditText headerInput = view.findViewById(R.id.header_input);
        EditText contentInput = view.findViewById(R.id.content_input);
        EditText descriptionInput = view.findViewById(R.id.description_input);
        TextView createDateText = view.findViewById(R.id.createDate_editText);
        ImageButton deleteButton = view.findViewById(R.id.delete_button);
        ImageButton saveButton = view.findViewById(R.id.save_button);
        ImageButton closeButton = view.findViewById(R.id.close_button);

        Record record = databaseService.getById(id);
        recordType.setText(String.valueOf(record.getType().ordinal()));
        headerInput.setText(record.getHeader());
        contentInput.setText(record.getContent());
        descriptionInput.setText(record.getDescription());
        createDateText.setText(RecordAdapter.formatDate(record.getCreateDate()));


        closeButton.setOnClickListener(v -> {
            showConfirmDialog(null, "exit", new ConfirmDialogListener() {
                @Override
                public void onDialogResult(boolean accepted) {
                    if (accepted) {
                        dialog.dismiss();
                    }
                }
            });
        });

        deleteButton.setOnClickListener(v -> {
            showConfirmDialog(id, "delete", new ConfirmDialogListener() {
                @Override
                public void onDialogResult(boolean accepted) {
                    if (accepted) {
                        databaseService.delete(id);
                    }
                }
            });
            dialog.dismiss();
        });

        saveButton.setOnClickListener(v -> {
            if (headerInput.getText().toString().isEmpty()) {
                headerInput.setError("Header cannot be empty");
            } else if (contentInput.getText().toString().isEmpty()) {
                contentInput.setError("Content cannot be empty");
            } else {
                record.setHeader(headerInput.getText().toString());
                record.setContent(contentInput.getText().toString());
                record.setDescription(descriptionInput.getText().toString());
                record.setUpdateTime(new Date());
                databaseService.update(record);
                dialog.dismiss();
            }
        });

        dialog.show();
    }
    public void showCreateRecordMenu() {
        Dialog dialog = new Dialog(context);
        View view = LayoutInflater.from(context).inflate(R.layout.create_record_dialog, null);
        dialog.setContentView(view);

        Spinner recordTypeSpinner = view.findViewById(R.id.record_type_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,
                R.array.record_type_items, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        recordTypeSpinner.setAdapter(adapter);

        EditText headerInput = view.findViewById(R.id.header_input);
        EditText contentInput = view.findViewById(R.id.content_input);
        EditText descriptionInput = view.findViewById(R.id.description_input);
        ImageButton closeButton = view.findViewById(R.id.close_button);
        ImageButton saveButton = view.findViewById(R.id.save_button);

        closeButton.setOnClickListener(v -> {
            showConfirmDialog(null, "exit", new ConfirmDialogListener() {
                @Override
                public void onDialogResult(boolean accepted) {
                    if (accepted) {
                        dialog.dismiss();
                    }
                }
            });
        });

        saveButton.setOnClickListener(v -> {
            if (headerInput.getText().toString().isEmpty() && StringUtils.isLongerThan(40,headerInput.getText().toString())) {
                headerInput.setError("Header cannot be empty");
            } else if (contentInput.getText().toString().isEmpty()) {
                contentInput.setError("Content cannot be empty");
            } else if(StringUtils.isLongerThan(70,descriptionInput.getText().toString())){
                descriptionInput.setError("description should be shorter than 70 characters");
            }else {
                Record newRecord = new Record(null,
                        headerInput.getText().toString(),
                        contentInput.getText().toString(),
                        descriptionInput.getText().toString(),
                        new Date(),
                        new Date(),
                        recordTypeSpinner.getSelectedItem().toString());

                databaseService.save(newRecord);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void showLogOutMenu(){
        //confirm
    }
    public void showLogInMenu(){
        //log In

        //TODO make here after log in sync DB databaseService.synchronizeDatabase();
    }

}

