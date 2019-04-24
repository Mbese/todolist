package com.example.todolist;

public interface ToDoListView {
    void onCheckChanged(String itemDescription);

    void onCheckBoxUnchecked(String itemDescription);
}
