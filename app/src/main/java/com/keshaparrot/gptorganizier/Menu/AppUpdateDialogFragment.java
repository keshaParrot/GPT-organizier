package com.keshaparrot.gptorganizier.Menu;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.keshaparrot.gptorganizier.R;
import com.keshaparrot.gptorganizier.service.UpdateService;

public class AppUpdateDialogFragment extends DialogFragment {

    private UpdateService updateService;

    public static AppUpdateDialogFragment newInstance(Context context) {
        AppUpdateDialogFragment fragment = new AppUpdateDialogFragment();
        fragment.updateService = UpdateService.getInstance(context);
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
        return inflater.inflate(R.layout.app_update_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView appInfoVersionMessage = view.findViewById(R.id.confirm_dialog_header);
        Button closeDialogButton = view.findViewById(R.id.download_update_button);
        Button updateButton = view.findViewById(R.id.close_dialog_button);
        Button disableReminderButton = view.findViewById(R.id.disable_update_reminder_button);

        String latestVersion = updateService.getLatestVersion();
        appInfoVersionMessage.setText(getString(R.string.available_version_is) + latestVersion);

        updateButton.setOnClickListener(view13 -> {
            updateService.downloadApk();
            dismiss();
        });
        closeDialogButton.setOnClickListener(view1 -> dismiss());
        disableReminderButton.setOnClickListener(view12 -> {
            updateService.disableUpdateReminder();
            Toast.makeText(getContext(), getString(R.string.reminder_update_skip_until_new_version), Toast.LENGTH_SHORT).show();
        });

    }
}
