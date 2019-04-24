package com.example.todolist;

import android.view.View;

public interface OnClickListener {
    void onItemLongClick(View view, ItemsModel itemsModel, int position);
}
