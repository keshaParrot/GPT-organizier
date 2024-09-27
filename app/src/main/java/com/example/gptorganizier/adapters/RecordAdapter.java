package com.example.gptorganizier.adapters;

import android.animation.ValueAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gptorganizier.Menu.MenuManager;
import com.example.gptorganizier.R;
import com.example.gptorganizier.domain.Record;
import com.example.gptorganizier.service.ContentExchangeService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.ViewHolder> {

    private List<Record> itemList;
    private final ContentExchangeService contentExchangeService;
    private final MenuManager menuManager;
    private int expandedPosition = RecyclerView.NO_POSITION;

    public RecordAdapter(List<Record> itemList, ContentExchangeService contentExchangeService) {
        this.itemList = itemList;
        this.contentExchangeService = contentExchangeService;
        this.menuManager = MenuManager.getInstance();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView headerText;
        public TextView createDateText;
        public TextView expandedTextView;
        public ImageButton editButton;
        public ImageButton openButton;

        public ViewHolder(View view) {
            super(view);
            headerText = view.findViewById(R.id.item_header_text);
            createDateText = view.findViewById(R.id.item_createDate_text);
            expandedTextView = view.findViewById(R.id.item_description_text);
            editButton = view.findViewById(R.id.item_edit_button);
            openButton = view.findViewById(R.id.item_open_button);
        }
    }
    public void updateList(List<Record> newList) {
        itemList.clear();
        itemList.addAll(newList);
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.record_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Record item = itemList.get(position);
        holder.headerText.setText(item.getHeader());
        holder.createDateText.setText(formatDate(item.getCreateDate()));

        final boolean isExpanded = holder.getBindingAdapterPosition() == expandedPosition;
        if (isExpanded) {
            expandView(holder.expandedTextView);
        } else {
            collapseView(holder.expandedTextView);
        }
        holder.itemView.setActivated(isExpanded);

        holder.itemView.setOnClickListener(v -> {
            int currentPosition = holder.getBindingAdapterPosition();
            if (expandedPosition == currentPosition) {
                expandedPosition = RecyclerView.NO_POSITION;
                notifyItemChanged(currentPosition);
            } else {
                int prevExpandedPosition = expandedPosition;
                expandedPosition = currentPosition;
                notifyItemChanged(prevExpandedPosition);
                notifyItemChanged(expandedPosition);
            }
        });


        holder.editButton.setOnClickListener(v -> {
            menuManager.showEditRecordMenu(item.getId());
        });

        holder.openButton.setOnClickListener(v -> {
            contentExchangeService.openContentInGPT(item.getContent());
        });
    }
    private void expandView(View view) {
        view.setVisibility(View.VISIBLE);
        final int widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        final int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(widthSpec, heightSpec);

        ValueAnimator animator = slideAnimator(view, 0, view.getMeasuredHeight());
        animator.start();
    }
    private void collapseView(final View view) {
        int finalHeight = view.getHeight();

        ValueAnimator animator = slideAnimator(view, finalHeight, 0);

        animator.addListener(new android.animation.AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                view.setVisibility(View.GONE);
            }
        });
        animator.start();
    }
    private ValueAnimator slideAnimator(final View view, int start, int end) {
        ValueAnimator animator = ValueAnimator.ofInt(start, end);
        animator.addUpdateListener(valueAnimator -> {
            int value = (Integer) valueAnimator.getAnimatedValue();
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            layoutParams.height = value;
            view.setLayoutParams(layoutParams);
        });
        return animator.setDuration(300);
    }
    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static String formatDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM", Locale.getDefault());
        return dateFormat.format(date);
    }
}

