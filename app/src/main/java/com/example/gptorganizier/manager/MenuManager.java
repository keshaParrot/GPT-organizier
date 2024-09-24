package com.example.gptorganizier.manager;

import com.example.gptorganizier.R;

public class MenuManager {

    /*
        list of xml

        X welcome activity
        X record item
        X log in
        (need to make type slider) open record (will same layout but different methods for create or edit record)
        X confirm delete / logout
     */

    /*private Context context;
    private Dialog dialog;
    private View currentView;
    private Object DBMS;

    public MenuManager(Context context) {
        this.context = context;
        this.DBMS = new Object();
    }

    public void showDeleteRecordMenu(Long id) {
        dialog = new Dialog(context);
        currentView = LayoutInflater.from(context).inflate(R.layout.confirm_dialog_menu, null);
        dialog.setContentView(currentView);

        Button cancelButton = currentView.findViewById(R.id.cancel_Button);
        Button acceptButton = currentView.findViewById(R.id.accept_Button);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                //TODO delete record
            }
        });

        dialog.show();
    }
    public void showEditRecordMenu(Long id) {
        Dialog dialog = new Dialog(context);
        View view = LayoutInflater.from(context).inflate(R.layout.open_record_dialog_menu, null);
        dialog.setContentView(view);

        EditText recordType = view.findViewById(R.id.record_type_editText);
        EditText headerInput = view.findViewById(R.id.header_input);
        EditText contentInput = view.findViewById(R.id.content_input);
        EditText descriptionInput = view.findViewById(R.id.description_input);
        TextView createDateText = view.findViewById(R.id.createDate_editText);
        ImageButton deleteButton = view.findViewById(R.id.delete_button);
        ImageButton saveButton = view.findViewById(R.id.save_button);
        ImageButton closeButton = view.findViewById(R.id.close_button);

        Record record = DBMS.getRecordById(id);
        if (record != null) {
            recordType.setText(record.getRecordType());
            headerInput.setText(record.getHeader());
            contentInput.setText(record.getContent());
            descriptionInput.setText(record.getDescription());
            createDateText.setText(record.getCreateDate());
        }

        closeButton.setOnClickListener(v -> {
            // Ask for confirmation if changes were made
            // If no changes were made, close without asking
        });

        deleteButton.setOnClickListener(v -> {
            // Ask for confirmation before deletion
            recordDao.deleteRecord(id);
            dialog.dismiss();
        });

        saveButton.setOnClickListener(v -> {
            // Validate fields and update the record
            if (headerInput.getText().toString().isEmpty()) {
                headerInput.setError("Header cannot be empty");
            } else if (contentInput.getText().toString().isEmpty()) {
                contentInput.setError("Content cannot be empty");
            } else {
                // Update the record
                record.setRecordType(recordType.getText().toString());
                record.setHeader(headerInput.getText().toString());
                record.setContent(contentInput.getText().toString());
                record.setDescription(descriptionInput.getText().toString());
                recordDao.updateRecord(record);
                dialog.dismiss();
            }
        });

        dialog.show();
    }
    public void showCreateRecordMenu() {
        Dialog dialog = new Dialog(context);
        View view = LayoutInflater.from(context).inflate(R.layout.open_record_dialog_menu, null);
        dialog.setContentView(view);

        EditText recordType = view.findViewById(R.id.record_type_editText);
        EditText headerInput = view.findViewById(R.id.header_input);
        EditText contentInput = view.findViewById(R.id.content_input);
        EditText descriptionInput = view.findViewById(R.id.description_input);
        ImageButton closeButton = view.findViewById(R.id.close_button);
        ImageButton saveButton = view.findViewById(R.id.save_button);

        closeButton.setOnClickListener(v -> {
            if(confirmCloseMenu()){
                dialog.dismiss();
            }
        });

        saveButton.setOnClickListener(v -> {
            // Validate fields, highlight if necessary
            if (headerInput.getText().toString().isEmpty()) {
                headerInput.setError("Header cannot be empty");
            } else if (contentInput.getText().toString().isEmpty()) {
                contentInput.setError("Content cannot be empty");
            } else {
                // Save the record
                Record newRecord = new Record(null, recordType.getText().toString(), headerInput.getText().toString(),
                        contentInput.getText().toString(), descriptionInput.getText().toString(), "Current Date");
                recordDao.saveRecord(newRecord);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public boolean confirmCloseMenu(){

    }

    public void showLogOutMenu(){
        //confirm
    }
    public void showLogInMenu(){
        //log In
    }*/

}

