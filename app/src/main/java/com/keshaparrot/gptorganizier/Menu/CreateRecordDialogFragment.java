package com.keshaparrot.gptorganizier.Menu;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.keshaparrot.gptorganizier.R;
import com.keshaparrot.gptorganizier.utils.StringUtils;
import com.keshaparrot.gptorganizier.viewmodel.RecordViewModel;
import com.keshaparrot.gptorganizier.viewmodel.RecordViewModelFactory;
import com.keshaparrot.gptorganizier.domain.Record;

import java.util.Date;

public class CreateRecordDialogFragment extends DialogFragment {

    private RecordViewModel recordViewModel;
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
        return inflater.inflate(R.layout.create_record_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecordViewModelFactory factory = new RecordViewModelFactory(requireContext());
        recordViewModel = new ViewModelProvider(this, factory).get(RecordViewModel.class);

        setupUI(view);
    }

    private void setupUI(View view) {
        Spinner recordTypeSpinner = view.findViewById(R.id.record_type_spinner);
        setupRecordTypeSpinner(recordTypeSpinner);

        EditText headerInput = view.findViewById(R.id.header_input);
        EditText contentInput = view.findViewById(R.id.content_input);
        EditText descriptionInput = view.findViewById(R.id.description_input);
        ImageButton closeButton = view.findViewById(R.id.close_button);
        ImageButton saveButton = view.findViewById(R.id.save_button);

        closeButton.setOnClickListener(v -> showConfirmCloseDialog());
        saveButton.setOnClickListener(v -> handleSaveButtonClick(headerInput, contentInput, descriptionInput, recordTypeSpinner));
    }

    private void setupRecordTypeSpinner(Spinner recordTypeSpinner) {
        //TODO implement here layout item

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.record_type_items, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        recordTypeSpinner.setAdapter(adapter);
    }

    private void showConfirmCloseDialog() {
        ConfirmDialogFragment.newInstance(getString(R.string.exit_menu), accepted -> {
            if (accepted) {
                dismiss();
            }
        }).show(getParentFragmentManager(), "ConfirmDialog");
    }

    private void handleSaveButtonClick(EditText headerInput, EditText contentInput, EditText descriptionInput, Spinner recordTypeSpinner) {
        if (validateInputs(headerInput, contentInput, descriptionInput)) {
            createNewRecord(headerInput, contentInput, descriptionInput, recordTypeSpinner);
        }
    }

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

    private void createNewRecord(EditText headerInput, EditText contentInput, EditText descriptionInput, Spinner recordTypeSpinner) {
        Record newRecord = new Record(
                null,
                headerInput.getText().toString(),
                contentInput.getText().toString(),
                descriptionInput.getText().toString(),
                new Date(),
                new Date(),
                recordTypeSpinner.getSelectedItem().toString(),
                false
        );

        recordViewModel.insert(newRecord);
        dismiss();
        Toast.makeText(getContext(), R.string.success_create_record, Toast.LENGTH_SHORT).show();
    }
}

