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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.keshaparrot.gptorganizier.R;
import com.keshaparrot.gptorganizier.service.UpdateService;

public class AppUpdateInfoDialogFragment extends DialogFragment {

    private UpdateService updateService;

    public static AppUpdateInfoDialogFragment newInstance(Context context) {
        AppUpdateInfoDialogFragment fragment = new AppUpdateInfoDialogFragment();
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
        return inflater.inflate(R.layout.app_update_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView appInfoVersionMessage = view.findViewById(R.id.app_info_version_message);
        Button closeInfoButton = view.findViewById(R.id.close_info_button);

        String currentVersion = updateService.getCurrentVersion();
        appInfoVersionMessage.setText(getString(R.string.your_version_is) + currentVersion);

        closeInfoButton.setOnClickListener(view1 -> dismiss());
    }
}
