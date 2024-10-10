package com.keshaparrot.gptorganizier.adapters;

import android.animation.ValueAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;


import com.keshaparrot.gptorganizier.R;
import com.keshaparrot.gptorganizier.Menu.EditRecordDialogFragment;
import com.keshaparrot.gptorganizier.utils.RecordDiffCallback;
import com.keshaparrot.gptorganizier.domain.Record;
import com.keshaparrot.gptorganizier.listener.RecordObserverListener;
import com.keshaparrot.gptorganizier.service.ContentExchangeService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.ViewHolder> {

    private List<Record> itemList;
    private final ContentExchangeService contentExchangeService;
    private int expandedPosition = RecyclerView.NO_POSITION;

    public RecordAdapter(ContentExchangeService contentExchangeService) {
        itemList = new ArrayList<>();
        this.contentExchangeService = contentExchangeService;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView headerText;
        public TextView createDateText;
        public TextView expandedTextView;
        public ImageButton editButton;
        public ImageButton openButton;
        public ImageView recordMarked;

        public ViewHolder(View view) {
            super(view);
            headerText = view.findViewById(R.id.item_header_text);
            createDateText = view.findViewById(R.id.item_createDate_text);
            expandedTextView = view.findViewById(R.id.item_description_text);
            editButton = view.findViewById(R.id.item_edit_button);
            openButton = view.findViewById(R.id.item_open_button);
            recordMarked = view.findViewById(R.id.item_bookmark);
        }
    }
    public void updateList(List<Record> newList) {
        newList.sort(Comparator.comparing(Record::isMarked).reversed()
                .thenComparing(Record::getCreateDate));

        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new RecordDiffCallback(itemList, newList));
        diffResult.dispatchUpdatesTo(this);

        itemList.clear();
        itemList.addAll(newList);

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
        holder.expandedTextView.setText(item.getDescription());
        holder.recordMarked.setVisibility(getVisibilityStateByBool(item.isMarked()));

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
            AppCompatActivity activity = (AppCompatActivity) v.getContext();
            EditRecordDialogFragment.newInstance(item.getId(), (RecordObserverListener) v.getContext())
                    .show(activity.getSupportFragmentManager(), "EditRecordDialog");

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
    private int getVisibilityStateByBool(boolean state){
        return state? View.VISIBLE : View.GONE;
    }
}

