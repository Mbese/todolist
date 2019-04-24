package com.example.todolist;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ToDoListAdapter extends RecyclerView.Adapter<ToDoListAdapter.MyViewHolder> {

    private final ArrayList<ItemsModel> toDoList;
    private final ToDoListView view;

    private Context context;
    private OnClickListener onClickListener = null;

    private SparseBooleanArray selected_items;
    private int current_selected_id = -1;

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    ToDoListAdapter(ArrayList<ItemsModel> toDoList, ToDoListView view, Context context) {
        this.toDoList = toDoList;
        this.view = view;
        this.context = context;

        selected_items = new SparseBooleanArray();

        setHasStableIds(true);
    }

    @NonNull
    @Override
    public ToDoListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.items_list, viewGroup, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, final int listPosition) {
        final ItemsModel item = toDoList.get(listPosition);
        TextView titleTextView = myViewHolder.titleTextView;
        CheckBox checkBox = myViewHolder.checkBox;

        final String itemDescription = toDoList.get(listPosition).getItemDescription();

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    view.onCheckChanged(itemDescription);
                } else {
                    view.onCheckBoxUnchecked(itemDescription);
                }
            }
        });

        titleTextView.setText(itemDescription);

        if (toDoList.get(listPosition).isCompleted()) {
            checkBox.isChecked();
        }

        myViewHolder.layoutParent.setActivated(selected_items.get(listPosition, false));
        myViewHolder.layoutParent.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (onClickListener == null) {
                    return false;
                }

                onClickListener.onItemLongClick(v, item, listPosition);
                return true;
            }
        });

        highlighSelectedItems(myViewHolder, listPosition);
    }

    private void highlighSelectedItems(RecyclerView.ViewHolder holder, int position) {
        if (selected_items.get(position, false)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                holder.itemView.setBackground(new ColorDrawable(context.getColor(R.color.colorAccent)));
            }
            if (current_selected_id == position) {
                resetCurrentIndex();
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                holder.itemView.setBackground(new ColorDrawable(context.getColor(R.color.colorWhite)));
            }
        }
    }

    private void resetCurrentIndex() {
        current_selected_id = -1;
    }

    public void toggleSelection(int pos) {
        current_selected_id = pos;
        if (selected_items.get(pos, false)) {
            selected_items.delete(pos);
        } else {
            selected_items.put(pos, true);
        }

        notifyDataSetChanged();
    }

    public void clearSelection() {
        selected_items.clear();
        notifyDataSetChanged();
    }

    public List<Integer> getSelectedItems() {
        List<Integer> items = new ArrayList<>(selected_items.size());
        for (int i = 0; i < selected_items.size(); i++) {
            items.add(selected_items.keyAt(i));
        }

        return items;
    }

    public int getSelectedItemCount() {
        return selected_items.size();
    }

    @Override
    public int getItemCount() {
        return toDoList.size();
    }

    public void removeData(int pos) {
        toDoList.remove(pos);
        resetCurrentIndex();
    }


    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        CheckBox checkBox;
        View layoutParent;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);

            this.titleTextView = itemView.findViewById(R.id.item);
            this.checkBox = itemView.findViewById(R.id.checkbox);
            this.layoutParent = itemView.findViewById(R.id.layout_parent);
        }
    }
}
