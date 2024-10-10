package com.keshaparrot.gptorganizier.Menu;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.keshaparrot.gptorganizier.R;
import com.keshaparrot.gptorganizier.adapters.RecordAdapter;
import com.keshaparrot.gptorganizier.listener.RecordObserverListener;
import com.keshaparrot.gptorganizier.utils.StringUtils;
import com.keshaparrot.gptorganizier.viewmodel.RecordViewModel;
import com.keshaparrot.gptorganizier.viewmodel.RecordViewModelFactory;
import com.keshaparrot.gptorganizier.domain.Record;

import java.util.Date;

public class EditRecordDialogFragment extends DialogFragment {

    //TODO also check sync service
    //TODO we need to change style of input fields, add some text before fields

    private Long recordId;
    private RecordViewModel recordViewModel;
    private RecordObserverListener recordObserverListener;

    public static EditRecordDialogFragment newInstance(Long id, RecordObserverListener listener) {
        EditRecordDialogFragment fragment = new EditRecordDialogFragment();
        fragment.recordId = id;
        fragment.recordObserverListener = listener;
        return fragment;
    }
    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.open_record_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecordViewModelFactory factory = new RecordViewModelFactory(requireContext());
        recordViewModel = new ViewModelProvider(this, factory).get(RecordViewModel.class);

        TextView recordType = view.findViewById(R.id.record_type_editText);
        EditText headerInput = view.findViewById(R.id.header_input);
        EditText contentInput = view.findViewById(R.id.content_input);
        EditText descriptionInput = view.findViewById(R.id.description_input);
        TextView createDateText = view.findViewById(R.id.createDate_editText);
        ImageButton deleteButton = view.findViewById(R.id.delete_button);
        ImageButton saveButton = view.findViewById(R.id.save_button);
        ImageButton closeButton = view.findViewById(R.id.close_button);
        ImageButton recordMarkButton = view.findViewById(R.id.record_mark_button);

        boolean[] recordMarked = {false};

        recordViewModel.getById(recordId).observe(getViewLifecycleOwner(), record -> {
            if (record != null) {
                populateFields(record, recordType, headerInput, contentInput, descriptionInput, createDateText, recordMarked, recordMarkButton);
            }
        });

        recordMarkButton.setOnClickListener(view1 -> {
            recordMarked[0] = !recordMarked[0];
            setRecordMarkIcon(recordMarked[0], recordMarkButton);
        });

        closeButton.setOnClickListener(v -> {
            ConfirmDialogFragment.newInstance(getString(R.string.exit_menu), accepted -> {
                if (accepted) {
                    dismiss();
                }
            }).show(getParentFragmentManager(), "ConfirmDialog");
        });

        deleteButton.setOnClickListener(v -> {
            ConfirmDialogFragment.newInstance(getString(R.string.delete_record_accept), accepted -> {
                if (accepted) {
                    deleteRecord();
                }
            }).show(getParentFragmentManager(), "ConfirmDialog");
        });

        setupSaveButton(saveButton, headerInput, contentInput, descriptionInput, recordMarked);
    }

    private void populateFields(Record record, TextView recordType, EditText headerInput, EditText contentInput, EditText descriptionInput, TextView createDateText, boolean[] recordMarked, ImageButton recordMarkButton) {
        recordType.setText(record.getType().name());
        headerInput.setText(record.getHeader());
        contentInput.setText(record.getContent());
        descriptionInput.setText(record.getDescription());
        String createdOnText = String.format("%s %s", getString(R.string.created_on), RecordAdapter.formatDate(record.getCreateDate()));
        createDateText.setText(createdOnText);
        recordMarked[0] = record.isMarked();
        setRecordMarkIcon(recordMarked[0], recordMarkButton);
    }

    private void setupSaveButton(ImageButton saveButton, EditText headerInput, EditText contentInput, EditText descriptionInput, boolean[] recordMarked) {
        saveButton.setOnClickListener(v -> {
            if (validateInputs(headerInput, contentInput, descriptionInput)) {
                updateRecord(headerInput, contentInput, descriptionInput, recordMarked);
            }
        });
    }
    //TODO make more validate
    private boolean validateInputs(EditText headerInput, EditText contentInput, EditText descriptionInput) {
        if (headerInput.getText().toString().isEmpty()) {
            headerInput.setError(getString(R.string.header_error_empty));
            return false;
        }
        if (StringUtils.isLongerThan(40, headerInput.getText().toString())) {
            headerInput.setError(getString(R.string.header_error_long));
            return false;
        }
        if (contentInput.getText().toString().isEmpty()) {
            contentInput.setError(getString(R.string.content_error_empty));
            return false;
        }
        if (StringUtils.isLongerThan(70, descriptionInput.getText().toString())) {
            descriptionInput.setError(getString(R.string.description_error_long));
            return false;
        }
        return true;
    }

    private void updateRecord(EditText headerInput, EditText contentInput, EditText descriptionInput, boolean[] recordMarked) {
        recordViewModel.getById(recordId).observe(getViewLifecycleOwner(), originalRecord -> {
            if (originalRecord != null) {
                Record updatedRecord = new Record();
                updatedRecord.setId(originalRecord.getId());
                updatedRecord.setHeader(headerInput.getText().toString());
                updatedRecord.setContent(contentInput.getText().toString());
                updatedRecord.setDescription(descriptionInput.getText().toString());
                updatedRecord.setCreateDate(originalRecord.getCreateDate());
                updatedRecord.setUpdateTime(new Date());
                updatedRecord.setMarked(recordMarked[0]);
                updatedRecord.setType(originalRecord.getType());

                recordViewModel.update(updatedRecord);
                recordObserverListener.execObserveRecords();
                dismiss();
                Toast.makeText(getContext(), R.string.success_edit_record, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteRecord() {
        recordViewModel.delete(recordId);
        dismiss();
        Toast.makeText(getContext(), R.string.success_delete_record, Toast.LENGTH_SHORT).show();
    }

    private void setRecordMarkIcon(boolean state, ImageButton imageButton) {
        if (state) {
            imageButton.setImageResource(R.drawable.record_bookmark_selected);
        } else {
            imageButton.setImageResource(R.drawable.record_bookmark_unselected);
        }
    }
}

