package com.keshaparrot.gptorganizier.Menu;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.keshaparrot.gptorganizier.R;

public class ConfirmDialogFragment extends DialogFragment {

    public interface ConfirmDialogListener {
        void onDialogResult(boolean accepted);
    }

    private String headerMessage;
    private String message;
    private String acceptButtonMessage;
    private String rejectButtonMessage;
    private ConfirmDialogListener listener;

    public static ConfirmDialogFragment newInstance(String message, ConfirmDialogListener listener) {
        ConfirmDialogFragment fragment = new ConfirmDialogFragment();
        fragment.message = message;
        fragment.listener = listener;
        return fragment;
    }
    public static ConfirmDialogFragment newInstance(String message,String headerMessage, ConfirmDialogListener listener) {
        ConfirmDialogFragment fragment = new ConfirmDialogFragment();
        fragment.message = message;
        fragment.headerMessage = headerMessage;
        fragment.listener = listener;
        return fragment;
    }
    public static ConfirmDialogFragment newInstance(String message,String acceptButtonMessage,String rejectButtonMessage, ConfirmDialogListener listener) {
        ConfirmDialogFragment fragment = new ConfirmDialogFragment();
        fragment.message = message;
        fragment.acceptButtonMessage = acceptButtonMessage;
        fragment.rejectButtonMessage = rejectButtonMessage;
        fragment.listener = listener;
        return fragment;
    }
    public static ConfirmDialogFragment newInstance(String message,String headerMessage,String acceptButtonMessage,String rejectButtonMessage, ConfirmDialogListener listener) {
        ConfirmDialogFragment fragment = new ConfirmDialogFragment();
        fragment.message = message;
        fragment.headerMessage = headerMessage;
        fragment.acceptButtonMessage = acceptButtonMessage;
        fragment.rejectButtonMessage = rejectButtonMessage;
        fragment.listener = listener;
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
        return inflater.inflate(R.layout.confirm_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView confirmMessageTextView = view.findViewById(R.id.confirm_dialog_message);
        TextView headerMessageTextView = view.findViewById(R.id.confirm_dialog_header);
        Button cancelButtonTextView = view.findViewById(R.id.cancel_dialog_button);
        Button acceptButtonTextView = view.findViewById(R.id.accept_dialog_button);

        confirmMessageTextView.setText(message);
        if(headerMessage!=null) headerMessageTextView.setText(headerMessage);
        if(acceptButtonMessage!=null) acceptButtonTextView.setText(acceptButtonMessage);
        if(rejectButtonMessage!=null) cancelButtonTextView.setText(rejectButtonMessage);

        cancelButtonTextView.setOnClickListener(v -> {
            listener.onDialogResult(false);
            dismiss();
        });

        acceptButtonTextView.setOnClickListener(v -> {
            listener.onDialogResult(true);
            dismiss();
        });
    }
}

