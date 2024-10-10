package com.keshaparrot.gptorganizier.service;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.keshaparrot.gptorganizier.Menu.CreateRecordDialogFragment;


public class ContentExchangeService {
    private Context context;
    private final AppCompatActivity activity;
    private final String GPTPackageName = "com.example.chatgpt";
    private final String GPTurl = "https://chat.openai.com";

    public ContentExchangeService(AppCompatActivity activity) {
        this.context = activity;
        this.activity = activity;
    }

    public void handleIncomingIntent(Intent intent) {
        if (intent != null && Intent.ACTION_SEND.equals(intent.getAction()) && "text/plain".equals(intent.getType())) {
            String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
            if (sharedText != null) {
                saveChatLink(sharedText);
            }
        }
    }

    private void saveChatLink(String link) {
        new CreateRecordDialogFragment().show(activity.getSupportFragmentManager(), "CreateRecordDialog");
    }

    public void openContentInGPT(String content){
        if (content.matches("^https://chatgpt\\.com/c/.*")){
            openChatLink(content);
        }else {
            openChatPrompt(content);
        }
    }

    private void openChatLink(String link) {
        Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(GPTPackageName);

        if (launchIntent != null) {
            context.startActivity(launchIntent);
        } else {
            Log.e("ChatLinkHandler", "ChatGPT app is not installed.");
            openChatGPTInBrowser();
        }
    }
    private void openChatPrompt(String Prompt) {
        Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(GPTPackageName);

        if (launchIntent != null) {
            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("ChatGPT Text", Prompt);
            clipboard.setPrimaryClip(clip);
            Log.d("ChatLinkHandler", "Copied text to clipboard: " + Prompt);

            context.startActivity(launchIntent);
        } else {
            Log.e("ChatLinkHandler", "ChatGPT app is not installed.");
            openChatGPTInBrowser();
        }
    }
    private void openChatGPTInBrowser() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(GPTurl));
        context.startActivity(intent);
    }
}
